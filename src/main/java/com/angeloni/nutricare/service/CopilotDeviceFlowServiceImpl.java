package com.angeloni.nutricare.service;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.angeloni.nutricare.dto.CopilotDeviceCodeDto;
import com.angeloni.nutricare.entity.CopilotConnectionEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.OAuthProviderEnum;
import com.angeloni.nutricare.exception.AuthException;
import com.angeloni.nutricare.repository.CopilotConnectionRepository;
import com.angeloni.nutricare.util.TokenCryptoUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CopilotDeviceFlowServiceImpl implements CopilotDeviceFlowService {

    private static final String DEVICE_CODE_URL = "https://github.com/login/device/code";
    private static final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String USER_URL = "https://api.github.com/user";
    private static final OAuthProviderEnum PROVIDER = OAuthProviderEnum.GITHUB_COPILOT;
    private static final String SCOPE = "copilot";

    private final RestTemplate restTemplate = buildTrustAllRestTemplate();

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
        } catch (Exception e) {
            // se fallisce si usa il RestTemplate standard
        }
        return new RestTemplate();
    }

    @Value("${nutricare.copilot.oauth.client-id:}")
    private String clientId;

    @Autowired
    private CopilotConnectionRepository copilotConnectionRepository;

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private TokenCryptoUtil tokenCryptoUtil;

    @Override
    @SuppressWarnings("unchecked")
    public CopilotDeviceCodeDto startDeviceFlow() {
        if (clientId == null || clientId.isBlank() || "replace-me".equals(clientId)) {
            throw new AuthException("Copilot OAuth client_id non configurato in application.properties");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("scope", SCOPE);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                DEVICE_CODE_URL, new HttpEntity<>(body, headers), Map.class);

        Map<String, Object> rb = response.getBody();
        if (rb == null || rb.get("device_code") == null) {
            throw new AuthException("Risposta non valida da GitHub Device Flow");
        }

        CopilotDeviceCodeDto dto = new CopilotDeviceCodeDto();
        dto.setDeviceCode(rb.get("device_code").toString());
        dto.setUserCode(rb.get("user_code").toString());
        dto.setVerificationUri(rb.getOrDefault("verification_uri", "https://github.com/login/device").toString());
        dto.setExpiresIn(Integer.parseInt(rb.getOrDefault("expires_in", "900").toString()));
        dto.setInterval(Integer.parseInt(rb.getOrDefault("interval", "5").toString()));
        return dto;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<String> pollForToken(String deviceCode, int intervalSeconds) {
        UserEntity user = userContextService.getCurrentUser();
        return CompletableFuture.supplyAsync(() -> {
            int interval = Math.max(intervalSeconds, 5);
            long deadline = System.currentTimeMillis() + 900_000L;

            while (System.currentTimeMillis() < deadline) {
                try {
                    Thread.sleep(interval * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Device flow interrotto");
                }

                HttpHeaders h = new HttpHeaders();
                h.setAccept(List.of(MediaType.APPLICATION_JSON));
                h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("client_id", clientId);
                body.add("device_code", deviceCode);
                body.add("grant_type", "urn:ietf:params:oauth:grant-type:device_code");

                try {
                    ResponseEntity<Map> resp = restTemplate.postForEntity(
                            TOKEN_URL, new HttpEntity<>(body, h), Map.class);
                    Map<String, Object> tb = resp.getBody();
                    if (tb == null) {
                        continue;
                    }

                    String error = tb.get("error") != null ? tb.get("error").toString() : null;

                    if (error == null && tb.get("access_token") != null) {
                        return persistToken(tb, user);
                    }
                    if ("slow_down".equals(error)) {
                        interval += 5;
                    } else if ("expired_token".equals(error)) {
                        throw new AuthException("Device code scaduto. Riavvia la procedura.");
                    } else if ("access_denied".equals(error)) {
                        throw new AuthException("Autorizzazione negata dall'utente.");
                    }
                    // "authorization_pending" → continue polling
                } catch (AuthException e) {
                    throw new RuntimeException(e.getMessage(), e);
                } catch (Exception e) {
                    log.warn("Errore durante il polling Copilot: {}", e.getMessage());
                }
            }
            throw new RuntimeException("Timeout: autorizzazione non completata entro 15 minuti.");
        });
    }

    @SuppressWarnings("unchecked")
    private String persistToken(Map<String, Object> tokenBody, UserEntity user) {
        String accessToken = tokenBody.get("access_token").toString();

        HttpHeaders uh = new HttpHeaders();
        uh.setBearerAuth(accessToken);
        uh.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<Map> userResp = restTemplate.exchange(
                USER_URL, HttpMethod.GET, new HttpEntity<>(uh), Map.class);
        Map<String, Object> ub = userResp.getBody();
        if (ub == null || ub.get("id") == null) {
            throw new RuntimeException("Impossibile recuperare il profilo GitHub");
        }

        Long githubUserId = Long.parseLong(ub.get("id").toString());
        String githubLogin = ub.get("login").toString();
        String tokenType = tokenBody.getOrDefault("token_type", "bearer").toString();
        String scope = tokenBody.getOrDefault("scope", SCOPE).toString();

        CopilotConnectionEntity conn = copilotConnectionRepository.findByUserAndProvider(user, PROVIDER)
                .orElse(CopilotConnectionEntity.builder().user(user).provider(PROVIDER).build());
        conn.setGithubUserId(githubUserId);
        conn.setGithubLogin(githubLogin);
        conn.setOrganization(null);
        conn.setEncryptedAccessToken(tokenCryptoUtil.encrypt(accessToken));
        conn.setEncryptedRefreshToken(null);
        conn.setTokenType(tokenType);
        conn.setScope(scope);
        conn.setExpiresAt(null);
        copilotConnectionRepository.save(conn);
        log.info("GitHub Copilot token salvato per: {}", githubLogin);
        return githubLogin;
    }
}