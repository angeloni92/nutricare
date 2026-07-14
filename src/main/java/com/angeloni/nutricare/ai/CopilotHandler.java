package com.angeloni.nutricare.ai;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.service.CopilotAuthService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CopilotHandler extends AbstractAIHandler {

	@Autowired(required = false)
	private CopilotAuthService copilotAuthService;

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
				log.info("Generating with Copilot");
				return generateDieta(prompt);
			} catch (Exception e) {
				log.error("Copilot error: {}", e.getMessage());
				return "Errore";
			}
		});
	}

	private boolean supports(String aiModel) {
		return aiModel != null && aiModel.equals("COPILOT_GPT4O");
	}

	private String generateDieta(String prompt) {
		return "PIANO ALIMENTARE - GitHub Copilot\n\n" + 
			   prompt.substring(0, Math.min(80, prompt.length())) + "...\n\n" +
			   "MERCOLEDI':\n- Yogurt + granola\n- Pollo\n- Tofu";
	}
}

