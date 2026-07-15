package com.angeloni.nutricare.ai;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AIHandlerFactory {

	@Autowired
	private ChatGPT4Handler chatGPT4Handler;

	@Autowired
	private ClaudeHandler claudeHandler;

	private AbstractAIHandler chain;

	public AIHandlerFactory() {
	}

	public void initialize() {
		chatGPT4Handler.setNext(claudeHandler);
		claudeHandler.setNext(null);
		this.chain = chatGPT4Handler;
		log.info("AI Handler chain initialized");
	}

	public CompletableFuture<String> generateDietResponse(String aiModel, String prompt) {
		if (chain == null) {
			initialize();
		}
		return chain.handle(aiModel, prompt);
	}
}
