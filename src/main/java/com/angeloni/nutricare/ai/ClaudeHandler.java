package com.angeloni.nutricare.ai;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ClaudeHandler extends AbstractAIHandler {

	private final String apiKey;

	public ClaudeHandler(@Value("${nutricare.anthropic.api-key:}") String apiKey) {
		this.apiKey = apiKey;
	}

	@Override
	public CompletableFuture<String> handle(String aiModel, String prompt) {
		if (!supports(aiModel)) {
			if (next != null) {
				return next.handle(aiModel, prompt);
			}
			return CompletableFuture.failedFuture(new IllegalArgumentException("No handler: " + aiModel));
		}

		return CompletableFuture.supplyAsync(() -> {
			try {
				log.info("Generating with Claude: {}", aiModel);
				String demoResponse = String.format(
					"PIANO ALIMENTARE GENERATO CON CLAUDE %s\n\n" +
					"Basato su: %s\n\n" +
					"MARTEDI':\n" +
					"- Colazione: Pancake integrali\n" +
					"- Pranzo: Salmone al forno\n" +
					"- Cena: Pasta integrale con verdure\n\n" +
					"[Dieta completa richiede configurazione API key]",
					aiModel, prompt.substring(0, Math.min(50, prompt.length())));
				return demoResponse;
			} catch (Exception e) {
				log.error("Claude error: {}", e.getMessage());
				return "Errore nella generazione";
			}
		});
	}

	@Override
	public void setNext(AbstractAIHandler next) {
		this.next = next;
	}

	private boolean supports(String aiModel) {
		return aiModel != null && (aiModel.equals("CLAUDE3SONNET") || aiModel.equals("CLAUDE35SONNET"));
	}
}

