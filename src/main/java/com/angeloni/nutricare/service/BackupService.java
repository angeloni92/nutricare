package com.angeloni.nutricare.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BackupService {

    private static final DateTimeFormatter TIMESTAMP_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Esegue il backup del database H2 nella cartella indicata.
     * Usa il comando SQL BACKUP TO di H2, che produce uno snapshot
     * consistente anche a database aperto.
     *
     * @param targetDir directory di destinazione
     * @return percorso del file .zip creato
     */
    public Path backup(Path targetDir) throws IOException {
        Files.createDirectories(targetDir);
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        Path backupFile = targetDir.resolve("nutricare-backup-" + timestamp + ".zip");
        String safePath = backupFile.toAbsolutePath().toString().replace("\\", "/");
        log.info("Starting database backup to: {}", backupFile);
        jdbcTemplate.execute("BACKUP TO '" + safePath + "'");
        log.info("Backup completed: {}", backupFile);
        return backupFile;
    }
}
