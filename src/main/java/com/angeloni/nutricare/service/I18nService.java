package com.angeloni.nutricare.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.angeloni.nutricare.event.LocaleChangedEvent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class I18nService {

    private static final Path PREF_FILE =
            Path.of(System.getProperty("user.home"), ".nutricare", "lang.properties");
    private static final String BUNDLE_BASE = "i18n/messages";
    private static final List<Locale> AVAILABLE = List.of(Locale.ITALIAN, Locale.ENGLISH);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private Locale currentLocale;
    private ResourceBundle bundle;

    public I18nService() {
        currentLocale = loadSavedLocale();
        bundle = ResourceBundle.getBundle(BUNDLE_BASE, currentLocale, new Utf8Control());
        log.info("I18nService initialised with locale: {}", currentLocale);
    }

    public String t(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            log.warn("Missing i18n key: {}", key);
            return key;
        }
    }

    public String t(String key, Object... args) {
        return MessageFormat.format(t(key), args);
    }

    public void setLocale(Locale locale) {
        if (locale.equals(currentLocale)) return;
        currentLocale = locale;
        bundle = ResourceBundle.getBundle(BUNDLE_BASE, locale, new Utf8Control());
        saveLocale(locale);
        log.info("Locale changed to: {}", locale);
        eventPublisher.publishEvent(new LocaleChangedEvent(this));
    }

    public Locale getLocale() { return currentLocale; }

    public List<Locale> getAvailableLocales() { return AVAILABLE; }

    private Locale loadSavedLocale() {
        if (Files.exists(PREF_FILE)) {
            Properties p = new Properties();
            try (InputStream in = Files.newInputStream(PREF_FILE)) {
                p.load(in);
                String lang = p.getProperty("lang", "it");
                return Locale.forLanguageTag(lang);
            } catch (IOException e) {
                log.warn("Cannot read lang preference: {}", e.getMessage());
            }
        }
        return Locale.ITALIAN;
    }

    private void saveLocale(Locale locale) {
        try {
            Files.createDirectories(PREF_FILE.getParent());
            Properties p = new Properties();
            p.setProperty("lang", locale.toLanguageTag());
            try (OutputStream out = Files.newOutputStream(PREF_FILE)) {
                p.store(out, "NutriCare language preference");
            }
        } catch (IOException e) {
            log.warn("Cannot save lang preference: {}", e.getMessage());
        }
    }

    private static class Utf8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format,
                                        ClassLoader loader, boolean reload) throws IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            try (InputStream is = loader.getResourceAsStream(resourceName)) {
                if (is == null) return null;
                return new PropertyResourceBundle(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
        }
    }
}
