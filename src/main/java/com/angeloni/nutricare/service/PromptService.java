package com.angeloni.nutricare.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PromptService {

	// Templ dieta base per la generazione
	private static final String DIET_PROMPT_TEMPLATE = """
			Generate a personalized diet plan for a nutritionist client with the following specifications:

			**Client Information:**
			- Client Name: [CLIENT_NAME]
			- Age: [CLIENT_AGE]
			- Primary Goal: [CLIENT_REQUEST_DIET_DETAIL_PRIMARY_GOAL]
			- Activity Level: [CLIENT_REQUEST_DIET_DETAIL_ACTIVITY_LEVEL]
			- Dietary Preferences: [CLIENT_REQUEST_DIET_DETAIL_DIETARY_PREFERENCE]
			- Target Calories: [CLIENT_REQUEST_DIET_DETAIL_CALORY_TARGET]

			**Anthropometric Data:**
			- Height: [CLIENT_REQUEST_ANTHROPOMETRY_HEIGHT] cm
			- Weight: [CLIENT_REQUEST_ANTHROPOMETRY_WEIGHT] kg

			**Circumference Data:**
			- Chest: [CLIENT_REQUEST_CIRCUMFERENCE_CHEST] cm
			- Waist: [CLIENT_REQUEST_CIRCUMFERENCE_WAIST] cm
			- Hip: [CLIENT_REQUEST_CIRCUMFERENCE_HIP] cm
			- Arm: [CLIENT_REQUEST_CIRCUMFERENCE_ARM] cm
			- Thigh: [CLIENT_REQUEST_CIRCUMFERENCE_THIGH] cm

			**Month/Season:** [CLIENT_REQUEST_DIET_DETAIL_MONTH]

			Please provide:
			1. A detailed meal plan for a full week
			2. Breakfast, lunch, snack, and dinner suggestions
			3. Nutritional balance recommendations
			4. Foods to avoid based on dietary preferences
			5. Tips for meal preparation and adherence

			Format the response in a clear, professional manner suitable for nutritionists to present to their clients.
			""";

	public String getDietPromptTemplate() {
		log.info("Retrieving diet prompt template");
		return DIET_PROMPT_TEMPLATE;
	}

	public String replacePromptVariables(String template, java.util.Map<String, String> data) {
		String result = template;
		for (java.util.Map.Entry<String, String> entry : data.entrySet()) {
			String placeholder = "[" + entry.getKey() + "]";
			result = result.replace(placeholder, entry.getValue() != null ? entry.getValue() : "");
		}
		return result;
	}
}

