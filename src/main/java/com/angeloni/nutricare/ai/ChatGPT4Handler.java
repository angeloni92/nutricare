package com.angeloni.nutricare.ai;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatGPT4Handler extends AbstractAIHandler {

	private final String apiKey;

	public ChatGPT4Handler(@Value("${nutricare.openai.api-key:}") String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public CompletableFuture<String> handle(String aiModel, String prompt) {
		if (!supports(aiModel)) {
			if (next != null) {
				return next.handle(aiModel, prompt);
			}
			return CompletableFuture.failedFuture(new IllegalArgumentException("Model not supported: " + aiModel));
		}

		return CompletableFuture.supplyAsync(() -> {
			try {
				log.info("Generating diet with OpenAI GPT4O");
				return generateDietResponse(prompt);
			} catch (Exception e) {
				log.error("OpenAI generation error: {}", e.getMessage());
				return "Errore nella generazione della dieta";
			}
		});
	}

	private boolean supports(String aiModel) {
		return aiModel != null && (aiModel.equals("GPT4O") || aiModel.equals("GPT3TURBO"));
	}

	private String generateDietResponse(String prompt) {
		return "PIANO ALIMENTARE PERSONALIZZATO\n\nGenerato con OpenAI GPT-4O\n\nBASATO SU:\n" + 
			   prompt.substring(0, Math.min(100, prompt.length())) + "...\n\n" +
			   "LUNEDI':\n- Colazione: Omelette con verdure\n- Pranzo: Petto di pollo\n- Cena: Branzino al forno\n\n" +
			   "[Dieta completa disponibile con API key configurato]";
	}
}

