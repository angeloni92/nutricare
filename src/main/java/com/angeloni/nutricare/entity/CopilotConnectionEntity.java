package com.angeloni.nutricare.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.angeloni.nutricare.enums.OAuthProviderEnum;

import jakarta.persistence.Column;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "copilot_connections", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "user_id" }),
		@UniqueConstraint(columnNames = { "provider", "github_user_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CopilotConnectionEntity implements Serializable {

	private static final long serialVersionUID = -6836447615796435146L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
	private UserEntity user;

	@Enumerated(EnumType.STRING)
	@Column(name = "provider", nullable = false, length = 50)
	private OAuthProviderEnum provider;

	@Column(name = "github_user_id", nullable = false)
	private Long githubUserId;

	@Column(name = "github_login", nullable = false, length = 255)
	private String githubLogin;

	@Column(name = "organization", length = 255)
	private String organization;

	@Column(name = "encrypted_access_token", nullable = false, length = 4096)
	private String encryptedAccessToken;

	@Column(name = "encrypted_refresh_token", length = 4096)
	private String encryptedRefreshToken;

	@Column(name = "token_type", length = 50)
	private String tokenType;

	@Column(name = "scope", length = 1024)
	private String scope;

	@Column(name = "expires_at")
	private LocalDateTime expiresAt;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}

