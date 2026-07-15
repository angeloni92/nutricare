package com.angeloni.nutricare.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenCryptoUtil {

	private static final String AES_ALGORITHM = "AES";
	private static final String AES_GCM = "AES/GCM/NoPadding";
	private static final int GCM_TAG_LENGTH = 128;
	private static final int IV_LENGTH = 12;

	private final SecretKeySpec secretKey;
	private final SecureRandom secureRandom = new SecureRandom();

	public TokenCryptoUtil(
			@Value("${nutricare.crypto.secret:MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=}") String base64Secret) {
		byte[] decoded = Base64.getDecoder().decode(base64Secret);
		if (decoded.length != 16 && decoded.length != 24 && decoded.length != 32) {
			throw new IllegalArgumentException("nutricare.copilot.crypto.secret must decode to 16, 24 or 32 bytes");
		}
		this.secretKey = new SecretKeySpec(decoded, AES_ALGORITHM);
	}

	public String encrypt(String plainValue) {
		if (plainValue == null) {
			return null;
		}
		try {
			byte[] iv = new byte[IV_LENGTH];
			secureRandom.nextBytes(iv);
			Cipher cipher = Cipher.getInstance(AES_GCM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
			byte[] encrypted = cipher.doFinal(plainValue.getBytes(StandardCharsets.UTF_8));
			byte[] payload = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, payload, 0, iv.length);
			System.arraycopy(encrypted, 0, payload, iv.length, encrypted.length);
			return Base64.getEncoder().encodeToString(payload);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unable to encrypt token", ex);
		}
	}

	public String decrypt(String encryptedValue) {
		if (encryptedValue == null) {
			return null;
		}
		try {
			byte[] payload = Base64.getDecoder().decode(encryptedValue);
			byte[] iv = Arrays.copyOfRange(payload, 0, IV_LENGTH);
			byte[] cipherText = Arrays.copyOfRange(payload, IV_LENGTH, payload.length);
			Cipher cipher = Cipher.getInstance(AES_GCM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
			byte[] plain = cipher.doFinal(cipherText);
			return new String(plain, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unable to decrypt token", ex);
		}
	}
}

