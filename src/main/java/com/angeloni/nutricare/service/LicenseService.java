package com.angeloni.nutricare.service;

import java.io.IOException;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LicenseService {

    public enum Status { TRIAL_ACTIVE, TRIAL_EXPIRED, LICENSED }

    private static final int TRIAL_DAYS = 30;
    private static final Path NUTRICARE_DIR = Path.of(System.getProperty("user.home"), ".nutricare");
    private static final Path TRIAL_FILE   = NUTRICARE_DIR.resolve("trial.dat");
    private static final Path LICENSE_FILE = NUTRICARE_DIR.resolve("license.dat");

    // Public key embedded — private key stays only in LicenseKeyGenerator (developer tool)
    private static final String PUBLIC_KEY_B64 =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0L5XUSF8M+CVJKyNm2Tz" +
        "CGcBSC+Xxaj9JuS/B+rytxJtx6EEa++FLJJ2B3InPUOFO9xiwCbi5d4qyB4M7f3p" +
        "I/SP2mD74e4vcEMziYYOqkDB+YoDoLuCHMYukhVNxBkBXPmdECxi8Y2JIJVi2Orw" +
        "MtCjRIdYMbXyJcsdNjecy/W684xppYgK1Yur4wZvwGqWctR/rkn3Wu5qrQU9B22i" +
        "WT19/u+0pm/8W916Crc9eXHDrCiFB4v+0/hIpJm7dBKquHGiJrbpz2sbowm+J5d7" +
        "vT51wEneX/NToSUWbzPGPQX2odOxoqcmzJKdEaBvUfFIGYOPVWSF24yhJ7PWp/wj" +
        "UwIDAQAB";

    private Status cachedStatus;
    private String cachedMachineId;

    public Status getStatus() {
        if (cachedStatus != null) return cachedStatus;
        cachedStatus = computeStatus();
        return cachedStatus;
    }

    public void invalidateCache() {
        cachedStatus = null;
    }

    public long getTrialDaysRemaining() {
        if (getStatus() == Status.LICENSED) return -1L;
        LocalDate start = readTrialStart();
        if (start == null) return 0L;
        long elapsed = ChronoUnit.DAYS.between(start, LocalDate.now());
        return Math.max(0L, TRIAL_DAYS - elapsed);
    }

    public String getMachineId() {
        if (cachedMachineId != null) return cachedMachineId;
        cachedMachineId = computeMachineId();
        return cachedMachineId;
    }

    public boolean activate(String licenseKeyB64) {
        if (licenseKeyB64 == null || licenseKeyB64.isBlank()) return false;
        try {
            byte[] sigBytes = Base64.getDecoder().decode(licenseKeyB64.trim());
            PublicKey pub = loadPublicKey();
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(pub);
            sig.update(getMachineId().getBytes(StandardCharsets.UTF_8));
            boolean valid = sig.verify(sigBytes);
            if (valid) {
                ensureDir();
                Files.writeString(LICENSE_FILE, licenseKeyB64.trim());
                invalidateCache();
                log.info("License activated successfully");
            }
            return valid;
        } catch (Exception e) {
            log.warn("License activation failed: {}", e.getMessage());
            return false;
        }
    }

    // ── internals ────────────────────────────────────────────────────────────

    private Status computeStatus() {
        if (isLicensed()) return Status.LICENSED;
        LocalDate start = readTrialStart();
        if (start == null) {
            initTrial();
            return Status.TRIAL_ACTIVE;
        }
        long elapsed = ChronoUnit.DAYS.between(start, LocalDate.now());
        return elapsed <= TRIAL_DAYS ? Status.TRIAL_ACTIVE : Status.TRIAL_EXPIRED;
    }

    private boolean isLicensed() {
        if (!Files.exists(LICENSE_FILE)) return false;
        try {
            String stored = Files.readString(LICENSE_FILE).trim();
            byte[] sigBytes = Base64.getDecoder().decode(stored);
            PublicKey pub = loadPublicKey();
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(pub);
            sig.update(getMachineId().getBytes(StandardCharsets.UTF_8));
            return sig.verify(sigBytes);
        } catch (Exception e) {
            log.warn("License file invalid: {}", e.getMessage());
            return false;
        }
    }

    private LocalDate readTrialStart() {
        if (!Files.exists(TRIAL_FILE)) return null;
        try {
            String content = Files.readString(TRIAL_FILE).trim();
            return LocalDate.parse(content);
        } catch (Exception e) {
            return null;
        }
    }

    private void initTrial() {
        try {
            ensureDir();
            Files.writeString(TRIAL_FILE, LocalDate.now().toString());
            log.info("Trial started: {}", LocalDate.now());
        } catch (IOException e) {
            log.error("Cannot write trial file: {}", e.getMessage());
        }
    }

    private void ensureDir() throws IOException {
        Files.createDirectories(NUTRICARE_DIR);
    }

    private String computeMachineId() {
        try {
            StringBuilder raw = new StringBuilder();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                for (NetworkInterface ni : Collections.list(interfaces)) {
                    byte[] mac = ni.getHardwareAddress();
                    if (mac != null) {
                        for (byte b : mac) raw.append(String.format("%02x", b));
                        break;
                    }
                }
            }
            raw.append(java.net.InetAddress.getLocalHost().getHostName());
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            log.error("Cannot compute machine ID: {}", e.getMessage());
            return "unknown";
        }
    }

    private PublicKey loadPublicKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(PUBLIC_KEY_B64.replaceAll("\\s", ""));
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
    }
}
