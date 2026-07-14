package com.angeloni.nutricare.dto;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;

import com.angeloni.nutricare.enums.ActivityLevelEnum;
import com.angeloni.nutricare.enums.DietaryPreferenceEnum;
import com.angeloni.nutricare.enums.PrimaryGoalEnum;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class DietDetailDto {

	private Long id;
	private ActivityLevelEnum activityLevel;
	private PrimaryGoalEnum primaryGoal;
	private DietaryPreferenceEnum dietaryPreference;
	@PositiveOrZero(message = "Age must be higher or equal to zero")
	private Integer caloryTarget;
	private Month month;
	private DayOfWeek freeDay;  
    private List<String> foodPreferences;
    private List<String> foodDislikes;

}
