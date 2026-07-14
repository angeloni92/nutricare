package com.angeloni.nutricare.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.angeloni.nutricare.dto.CopilotAuthResultDto;
import com.angeloni.nutricare.dto.CopilotAuthStartDto;
import com.angeloni.nutricare.dto.CopilotConnectionStatusDto;
import com.angeloni.nutricare.entity.CopilotConnectionEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.enums.OAuthProviderEnum;
import com.angeloni.nutricare.exception.AuthException;
import com.angeloni.nutricare.repository.CopilotConnectionRepository;
import com.angeloni.nutricare.repository.UserRepository;
import com.angeloni.nutricare.util.TokenCryptoUtil;

@Service
public class CopilotAuthServiceImpl implements CopilotAuthService {

	private static final OAuthProviderEnum PROVIDER = OAuthProviderEnum.GITHUB_COPILOT;
	private static final String SCOPE = "read:user read:org";
	private static final String STATUS_SUCCESS = "Success";
	private static final String STATUS_CONNECTED = "Connected";
	private static final String STATUS_DISCONNECTED = "Disconnected";

	private final RestTemplate restTemplate = new RestTemplate();
	private final Map<String, OAuthStateData> pendingStates = new ConcurrentHashMap<>();

	@Autowired
	private UserContextService userContextService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CopilotConnectionRepository copilotConnectionRepository;

	@Autowired
	private TokenCryptoUtil tokenCryptoUtil;

	@Value("${nutricare.copilot.oauth.client-id:}")
	private String clientId;

	@Value("${nutricare.copilot.oauth.client-secret:}")
	private String clientSecret;

	@Value("${nutricare.copilot.oauth.redirect-uri:}")
	private String redirectUri;

	@Value("${nutricare.copilot.oauth.authorize-url:https://github.com/login/oauth/authorize}")
	private String authorizeUrl;

	@Value("${nutricare.copilot.oauth.token-url:https://github.com/login/oauth/access_token}")
	private String tokenUrl;

	@Value("${nutricare.copilot.oauth.user-url:https://api.github.com/user}")
	private String userUrl;

	@Value("${nutricare.copilot.oauth.orgs-url:https://api.github.com/user/orgs}")
	private String orgsUrl;

	@Value("${nutricare.copilot.oauth.required-org:}")
	private String requiredOrg;

	@Value("${nutricare.copilot.oauth.state-ttl-seconds:600}")
	private long stateTtlSeconds;

	@Override
	public CopilotAuthStartDto startAuthorization() {
		validateOauthConfiguration();
		UserEntity currentUser = userContextService.getCurrentUser();
		String state = UUID.randomUUID().toString();
		pendingStates.put(state, new OAuthStateData(currentUser.getId(), Instant.now().plusSeconds(stateTtlSeconds)));

		String authorizationUrl = UriComponentsBuilder.fromHttpUrl(authorizeUrl).queryParam("client_id", clientId)
				.queryParam("redirect_uri", redirectUri).queryParam("scope", SCOPE).queryParam("state", state)
				.toUriString();

		CopilotAuthStartDto response = new CopilotAuthStartDto();
		response.setAuthorizationUrl(authorizationUrl);
		response.setState(state);
		return response;
	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public CopilotAuthResultDto completeAuthorization(String code, String state) {
		validateOauthConfiguration();
		OAuthStateData stateData = Optional.ofNullable(pendingStates.remove(state))
				.orElseThrow(() -> new AuthException("Invalid OAuth state"));
		if (Instant.now().isAfter(stateData.expiresAt())) {
			throw new AuthException("OAuth state expired. Start authorization again.");
		}

		UserEntity user = userRepository.findById(stateData.userId())
				.orElseThrow(() -> new AuthException("User not found for OAuth state"));

		HttpHeaders tokenHeaders = new HttpHeaders();
		tokenHeaders.setContentType(MediaType.APPLICATION_JSON);
		tokenHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
		Map<String, String> tokenRequest = Map.of("client_id", clientId, "client_secret", clientSecret, "code", code,
				"redirect_uri", redirectUri, "state", state);
		ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl,
				new HttpEntity<>(tokenRequest, tokenHeaders), Map.class);

		Map<String, Object> tokenBody = tokenResponse.getBody();
		if (tokenBody == null || tokenBody.get("access_token") == null) {
			throw new AuthException("Unable to obtain GitHub OAuth access token");
		}

		String accessToken = tokenBody.get("access_token").toString();
		String refreshToken = tokenBody.get("refresh_token") != null ? tokenBody.get("refresh_token").toString() : null;
		String tokenType = tokenBody.get("token_type") != null ? tokenBody.get("token_type").toString() : "bearer";
		String scope = tokenBody.get("scope") != null ? tokenBody.get("scope").toString() : SCOPE;
		LocalDateTime expiresAt = extractExpiry(tokenBody.get("expires_in"));

		HttpHeaders userHeaders = new HttpHeaders();
		userHeaders.setBearerAuth(accessToken);
		userHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
		ResponseEntity<Map> userResponse = restTemplate.exchange(userUrl, HttpMethod.GET, new HttpEntity<>(userHeaders),
				Map.class);
		Map<String, Object> userBody = userResponse.getBody();
		if (userBody == null || userBody.get("id") == null || userBody.get("login") == null) {
			throw new AuthException("Unable to read GitHub user profile");
		}
		Long githubUserId = Long.parseLong(userBody.get("id").toString());
		String githubLogin = userBody.get("login").toString();

		String organization = resolveOrganization(userHeaders);

		CopilotConnectionEntity connection = copilotConnectionRepository.findByUserAndProvider(user, PROVIDER)
				.orElse(CopilotConnectionEntity.builder().user(user).provider(PROVIDER).build());
		connection.setGithubUserId(githubUserId);
		connection.setGithubLogin(githubLogin);
		connection.setOrganization(organization);
		connection.setEncryptedAccessToken(tokenCryptoUtil.encrypt(accessToken));
		connection.setEncryptedRefreshToken(tokenCryptoUtil.encrypt(refreshToken));
		connection.setTokenType(tokenType);
		connection.setScope(scope);
		connection.setExpiresAt(expiresAt);
		copilotConnectionRepository.save(connection);

		CopilotAuthResultDto result = new CopilotAuthResultDto();
		result.setStatus(STATUS_SUCCESS);
		result.setMessage(COPILOT_LINKED);
		result.setGithubLogin(githubLogin);
		result.setOrganization(organization);
		return result;
	}

	@Override
	public CopilotConnectionStatusDto getCurrentConnectionStatus() {
		UserEntity user = userContextService.getCurrentUser();
		Optional<CopilotConnectionEntity> connection = copilotConnectionRepository.findByUserAndProvider(user, PROVIDER);
		CopilotConnectionStatusDto status = new CopilotConnectionStatusDto();
		if (connection.isPresent()) {
			status.setConnected(Boolean.TRUE);
			status.setGithubLogin(connection.get().getGithubLogin());
			status.setOrganization(connection.get().getOrganization());
			status.setScope(connection.get().getScope());
			return status;
		}
		status.setConnected(Boolean.FALSE);
		return status;
	}

	@Override
	@Transactional
	public void disconnectCurrentUser() {
		UserEntity user = userContextService.getCurrentUser();
		copilotConnectionRepository.deleteByUserAndProvider(user, PROVIDER);
	}

	@Override
	public String resolveAccessTokenForUser(UserEntity user) {
		CopilotConnectionEntity connection = copilotConnectionRepository.findByUserAndProvider(user, PROVIDER)
				.orElseThrow(() -> new AuthException("Copilot SSO is not connected for this user"));
		if (connection.getExpiresAt() != null && connection.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
			throw new AuthException("Copilot access token expired. Reconnect GitHub account.");
		}
		return tokenCryptoUtil.decrypt(connection.getEncryptedAccessToken());
	}

	@SuppressWarnings("unchecked")
	private String resolveOrganization(HttpHeaders userHeaders) {
		ResponseEntity<List> orgsResponse = restTemplate.exchange(orgsUrl, HttpMethod.GET, new HttpEntity<>(userHeaders),
				List.class);
		List<Object> orgs = orgsResponse.getBody();
		if (orgs == null || orgs.isEmpty()) {
			if (requiredOrg != null && !requiredOrg.isBlank()) {
				throw new AuthException("GitHub account is not in required organization: " + requiredOrg);
			}
			return null;
		}
		String matchedOrg = null;
		for (Object item : orgs) {
			if (item instanceof Map<?, ?> orgMap && orgMap.get("login") != null) {
				String orgLogin = orgMap.get("login").toString();
				if (requiredOrg != null && !requiredOrg.isBlank() && requiredOrg.equalsIgnoreCase(orgLogin)) {
					matchedOrg = orgLogin;
					break;
				}
				if (matchedOrg == null) {
					matchedOrg = orgLogin;
				}
			}
		}
		if (requiredOrg != null && !requiredOrg.isBlank() && (matchedOrg == null
				|| !requiredOrg.equalsIgnoreCase(matchedOrg))) {
			throw new AuthException("GitHub account is not in required organization: " + requiredOrg);
		}
		return matchedOrg;
	}

	private LocalDateTime extractExpiry(Object expiresIn) {
		if (expiresIn == null) {
			return null;
		}
		long seconds = Long.parseLong(expiresIn.toString());
		return LocalDateTime.now(ZoneOffset.UTC).plusSeconds(seconds);
	}

	private record OAuthStateData(Long userId, Instant expiresAt) {
	}

	private void validateOauthConfiguration() {
		if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()
				|| redirectUri == null || redirectUri.isBlank()) {
			throw new AuthException("Copilot OAuth is not configured. Set nutricare.copilot.oauth.* properties.");
		}
	}
}

