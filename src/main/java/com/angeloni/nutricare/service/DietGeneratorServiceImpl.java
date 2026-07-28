package com.angeloni.nutricare.service;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.dto.DietDetailDto;
import com.angeloni.nutricare.dto.DietRequestDto;
import com.angeloni.nutricare.entity.AiUserEntity;
import com.angeloni.nutricare.entity.DietResultEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.AIModelEnum;
import com.angeloni.nutricare.enums.AINameEnum;
import com.angeloni.nutricare.repository.AiRepository;
import com.angeloni.nutricare.repository.AiUserRepository;
import com.angeloni.nutricare.repository.DietResultRepository;
import com.angeloni.nutricare.util.TokenCryptoUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DietGeneratorServiceImpl implements DietGeneratorService {

    @Autowired
    private ClientService clientService;

    @Autowired
    private AiRepository aiRepository;

    @Autowired
    private AiUserRepository aiUserRepository;

    @Autowired
    private TokenCryptoUtil tokenCryptoUtil;

    @Autowired
    private DietResultRepository dietResultRepository;

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private I18nService i18nService;

    @Value("${nutricare.anthropic.api-key:}")
    private String anthropicApiKey;

    @Value("${nutricare.openai.api-key:}")
    private String openaiApiKey;

    @Value("${nutricare.gemini.api-key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = buildTrustAllRestTemplate();

    @Override
    public List<String> getClientsForSelection() {
        return clientService.getClients().stream()
                .map(c -> c.getName() + " " + c.getSurname())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public String generateDiet(DietRequestDto request) {
        AINameEnum provider = request.getAi().getName();
        AIModelEnum model   = request.getAi().getModel();
        ClientDto client    = request.getClientRequest().getClient();
        AnthropometryDto anthro = request.getClientRequest().getAnthropometry();
        DietDetailDto detail    = request.getClientRequest().getDietDetail();

        String prompt = buildPrompt(client, anthro, detail);
        String generatedDiet;

        if (provider == AINameEnum.CLAUDE) {
            generatedDiet = callClaude(model, prompt);
        } else if (provider == AINameEnum.CHATGPT) {
            generatedDiet = callOpenAi(model, prompt);
        } else if (provider == AINameEnum.GEMINI) {
            generatedDiet = callGemini(model, prompt);
        } else {
            throw new RuntimeException("Provider AI non supportato: " + provider);
        }

        UserEntity user = userContextService.getCurrentUser();
        DietResultEntity result = DietResultEntity.builder()
                .user(user)
                .clientId(client.getId())
                .generatedDiet(generatedDiet)
                .aiModel(model.name())
                .build();
        dietResultRepository.save(result);
        log.info("Diet saved for client {} using {}/{}", client.getId(), provider, model);

        return generatedDiet;
    }

    private String buildPrompt(ClientDto client, AnthropometryDto anthro, DietDetailDto detail) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sei un nutrizionista esperto. Genera un piano alimentare settimanale personalizzato per il seguente paziente:\n\n");

        sb.append("DATI PAZIENTE:\n");
        sb.append("- Nome: ").append(client.getName()).append(" ").append(client.getSurname()).append("\n");
        sb.append("- Età: ").append(client.getAge()).append(" anni\n");
        sb.append("- Paese: ").append(client.getCountry()).append("\n\n");

        if (anthro != null && anthro.getHeight() != null && anthro.getWeight() != null) {
            double bmi = anthro.getWeight() / Math.pow(anthro.getHeight() / 100.0, 2);
            sb.append("DATI ANTROPOMETRICI (ultima visita):\n");
            sb.append("- Altezza: ").append(String.format("%.1f", anthro.getHeight())).append(" cm\n");
            sb.append("- Peso: ").append(String.format("%.1f", anthro.getWeight())).append(" kg\n");
            sb.append("- BMI: ").append(String.format("%.1f", bmi)).append("\n\n");
        }

        if (detail != null) {
            if (detail.getPrimaryGoal() != null)
                sb.append("OBIETTIVO: ").append(detail.getPrimaryGoal().getValue()).append("\n");
            if (detail.getDietaryPreference() != null)
                sb.append("PREFERENZA ALIMENTARE: ").append(detail.getDietaryPreference().getValue()).append("\n");
            if (detail.getActivityLevel() != null)
                sb.append("LIVELLO DI ATTIVITÀ: ").append(detail.getActivityLevel().getValue()).append("\n");
            if (detail.getFoodPreferences() != null && !detail.getFoodPreferences().isEmpty()) {
                String notes = String.join(", ", detail.getFoodPreferences());
                if (!notes.isBlank())
                    sb.append("NOTE AGGIUNTIVE: ").append(notes).append("\n");
            }
        }

        sb.append("\nGenera un piano settimanale completo (lunedì-domenica) con colazione, pranzo, cena e spuntini. ");
        sb.append("Per ogni giorno indica le calorie totali stimate. Includi consigli pratici. ")
          .append(i18nService.t("prompt.language.instruction"));
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private String callClaude(AIModelEnum model, String prompt) {
        String apiKey = resolveApiKey(AINameEnum.CLAUDE, model, anthropicApiKey, "Claude");
        String modelId = claudeModelId(model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> body = Map.of(
                "model", modelId,
                "max_tokens", 4096,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            Map<String, Object> response = restTemplate.postForObject(
                    "https://api.anthropic.com/v1/messages",
                    new HttpEntity<>(body, headers),
                    Map.class);
            if (response == null)
                throw new RuntimeException("Risposta vuota da Claude (Anthropic).");
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            if (content == null || content.isEmpty())
                throw new RuntimeException("Nessun contenuto nella risposta Claude. Risposta: " + response);
            Object text = content.stream()
                    .filter(b -> "text".equals(b.get("type")))
                    .map(b -> b.get("text"))
                    .filter(t -> t != null)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Nessun blocco 'text' nella risposta Claude. Risposta: " + response));
            return text.toString();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException(mapApiError("Claude (Anthropic)", e), e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errore chiamata API Claude: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String callOpenAi(AIModelEnum model, String prompt) {
        String apiKey = resolveApiKey(AINameEnum.CHATGPT, model, openaiApiKey, "OpenAI");
        String modelId = openAiModelId(model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", modelId,
                "max_tokens", 4096,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            Map<String, Object> response = restTemplate.postForObject(
                    "https://api.openai.com/v1/chat/completions",
                    new HttpEntity<>(body, headers),
                    Map.class);
            if (response == null)
                throw new RuntimeException("Risposta vuota da OpenAI.");
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty())
                throw new RuntimeException("Nessuna scelta nella risposta OpenAI. Risposta: " + response);
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null)
                throw new RuntimeException("Campo 'message' mancante nella risposta OpenAI.");
            Object content = message.get("content");
            if (content == null)
                throw new RuntimeException("Campo 'content' mancante nella risposta OpenAI.");
            return content.toString();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException(mapApiError("OpenAI (ChatGPT)", e), e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errore chiamata API OpenAI: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String callGemini(AIModelEnum model, String prompt) {
        String apiKey = resolveApiKey(AINameEnum.GEMINI, model, geminiApiKey, "Gemini");
        String modelId = geminiModelId(model);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", modelId,
                "max_tokens", 4096,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            Map<String, Object> response = restTemplate.postForObject(
                    "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions",
                    new HttpEntity<>(body, headers),
                    Map.class);
            if (response == null)
                throw new RuntimeException("Risposta vuota da Google Gemini.");
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty())
                throw new RuntimeException("Nessuna scelta nella risposta Gemini. Risposta: " + response);
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null)
                throw new RuntimeException("Campo 'message' mancante nella risposta Gemini.");
            Object content = message.get("content");
            if (content == null)
                throw new RuntimeException("Campo 'content' mancante nella risposta Gemini.");
            return content.toString();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException(mapApiError("Google Gemini", e), e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Errore chiamata API Gemini: " + e.getMessage(), e);
        }
    }

    private String mapApiError(String providerLabel, HttpClientErrorException e) {
        int status = e.getStatusCode().value();
        String body = e.getResponseBodyAsString().toLowerCase();

        boolean isQuota = status == 402 || status == 429
                || body.contains("insufficient_quota")
                || body.contains("quota")
                || body.contains("credit")
                || body.contains("billing")
                || body.contains("resource_exhausted")
                || body.contains("rate limit")
                || body.contains("overloaded");

        if (isQuota) {
            return "Token esauriti o credito insufficiente per " + providerLabel + ".\n"
                    + "Verifica il piano e ricarica i crediti sul sito del provider.";
        }
        if (status == 401) {
            return "API Key non valida per " + providerLabel + ".\n"
                    + "Riconfigura le credenziali nella schermata Genera Dieta.";
        }
        if (status == 403) {
            return "Accesso negato per " + providerLabel + " (HTTP 403).\n"
                    + "Verifica che la tua API Key abbia i permessi necessari.";
        }
        return "Errore HTTP " + status + " da " + providerLabel + ": " + e.getMessage();
    }

    private String resolveApiKey(AINameEnum provider, AIModelEnum model, String propertyKey, String label) {
        if (propertyKey != null && !propertyKey.isBlank() && !"replace-me".equals(propertyKey)) {
            return propertyKey;
        }
        UserEntity user = userContextService.getCurrentUser();
        java.util.Optional<AiUserEntity> aiUser = aiRepository.findByNameAndModel(provider, model)
                .flatMap(ai -> aiUserRepository.findByUserAndAi(user, ai));
        if (aiUser.isEmpty()) {
            aiUser = aiRepository.findByName(provider).stream()
                    .map(ai -> aiUserRepository.findByUserAndAi(user, ai))
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .filter(e -> e.getAiKey() != null && !e.getAiKey().isBlank())
                    .findFirst();
        }
        return aiUser
                .map(e -> tokenCryptoUtil.decrypt(e.getAiKey()))
                .orElseThrow(() -> new RuntimeException("API Key " + label + " non configurata. Clicca su 'Configura Credenziali'."));
    }

    private String claudeModelId(AIModelEnum model) {
        return switch (model) {
            case CLAUDE5FABLE  -> "claude-fable-5";
            case CLAUDE5SONNET -> "claude-sonnet-5";
            case CLAUDE4SONNET -> "claude-sonnet-4-6";
            case CLAUDE48OPUS  -> "claude-opus-4-8";
            case CLAUDE4OPUS   -> "claude-opus-4-5";
            case CLAUDE37SONNET-> "claude-3-7-sonnet-20250219";
            case CLAUDE35SONNET-> "claude-3-5-sonnet-20241022";
            case CLAUDE35HAIKU -> "claude-3-5-haiku-20241022";
            case CLAUDE45HAIKU -> "claude-haiku-4-5-20251001";
            case CLAUDE3OPUS   -> "claude-3-opus-20240229";
            case CLAUDE3SONNET -> "claude-3-sonnet-20240229";
            case CLAUDE3HAIKU  -> "claude-3-haiku-20240307";
            default            -> "claude-sonnet-5";
        };
    }

    private String openAiModelId(AIModelEnum model) {
        return switch (model) {
            case GPT4O        -> "gpt-4o";
            case GPT4O_MINI   -> "gpt-4o-mini";
            case GPT4TURBO    -> "gpt-4-turbo";
            case GPT4         -> "gpt-4";
            case GPT3TURBO    -> "gpt-3.5-turbo";
            case OPENAIO1     -> "o1";
            case OPENAIO1MINI -> "o1-mini";
            case OPENAIO3     -> "o3";
            case OPENAIO3MINI -> "o3-mini";
            case OPENAIO4MINI -> "o4-mini";
            default           -> "gpt-4o";
        };
    }

    private String geminiModelId(AIModelEnum model) {
        return switch (model) {
            case GEMINI_25_PRO        -> "gemini-2.5-pro";
            case GEMINI_25_FLASH      -> "gemini-2.5-flash";
            case GEMINI_20_FLASH      -> "gemini-2.0-flash";
            case GEMINI_20_FLASH_LITE -> "gemini-2.0-flash-lite";
            case GEMINI_15_FLASH      -> "gemini-1.5-flash";
            case GEMINI_15_PRO        -> "gemini-1.5-pro";
            default                   -> "gemini-2.5-flash";
        };
    }

    private static RestTemplate buildTrustAllRestTemplate() {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAll, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((host, session) -> true);
        } catch (Exception ignored) {}
        return new RestTemplate();
    }
}
