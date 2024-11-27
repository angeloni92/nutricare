package com.angeloni.nutricare.entity;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.angeloni.nutricare.enums.ActivityLevelEnum;
import com.angeloni.nutricare.enums.DietaryPreferenceEnum;
import com.angeloni.nutricare.enums.PrimaryGoalEnum;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "client_details")
@Data
public class DietDetailEntity implements Serializable {
	
	private static final long serialVersionUID = -4676580010375723030L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false) 
    @JoinColumn(name = "client_id", nullable = false) 
	private ClientEntity client;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "activity_level", nullable = false, length = 255)
	private ActivityLevelEnum activityLevel;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "primary_goal", nullable = false, length = 255)
	private PrimaryGoalEnum primaryGoal;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "dietary_preference", nullable = false, length = 255)
	private DietaryPreferenceEnum dietaryPreference;
	
	@Column(name = "calory_target")
	private Integer caloryTarget;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "month", nullable = false, length = 255)
	private Month month;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "free_day", nullable = false, length = 255)
	private DayOfWeek freeDay;
	
	@ElementCollection
    @CollectionTable(name = "client_allergies", 
                     joinColumns = @JoinColumn(name = "client_id")) 
    @Column(name = "allergy_name") 
    private List<String> allergies;
	
	@ElementCollection
    @CollectionTable(name = "client_health_conditions", 
                     joinColumns = @JoinColumn(name = "client_id")) 
    @Column(name = "health_condition") 
    private List<String> healthConditions;
	
	@ElementCollection
    @CollectionTable(name = "client_food_preferences", 
                     joinColumns = @JoinColumn(name = "client_id")) 
    @Column(name = "food_preference") 
    private List<String> foodPreferences;
	
	@ElementCollection
    @CollectionTable(name = "client_food_dislikes", 
                     joinColumns = @JoinColumn(name = "client_id")) 
    @Column(name = "food_dislike") 
    private List<String> foodDislikes;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
