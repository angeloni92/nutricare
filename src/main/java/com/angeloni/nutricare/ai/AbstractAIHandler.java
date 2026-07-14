package com.angeloni.nutricare.ai;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractAIHandler {

	protected AbstractAIHandler next;

	public abstract CompletableFuture<String> handle(String aiModel, String prompt);

	public void setNext(AbstractAIHandler next) {
		this.next = next;
	}
}

