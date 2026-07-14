













# NUTRICARE - BUILD INSTRUCTIONS (FINAL)

copi

## ✅ Build Ready

Il progetto è configurato per eseguire il build usando il **Nexus interno aziendale**.

## 🚀 Comando Build

```bash
cd C:\Users\andrea.angeloni\repos\nutricare
mvn clean package -DskipTests -s settings.xml
```

## 📊 Configurazione

- **settings.xml**: Usa Nexus interno (`https://devops-eproc.it/nexus/repository/maven-eproc/`)
- **Dipendenze**: Cache nel Nexus (nessun download da internet)
- **SSL**: Verificato con Nexus interno (nessun errore certificato)

## ✨ Risultato Atteso

```
target/
├── nutricare-0.0.1-SNAPSHOT.jar          ✅ JAR eseguibile
├── nutricare-0.0.1-SNAPSHOT-desktop.jar  ✅ Shade JAR
└── ... (classi compilate e risorse)
```

## ▶️ Esecuzione

```bash
java -Xmx2G -jar target/nutricare-0.0.1-SNAPSHOT.jar
```

## 🔧 Opzioni Aggiuntive

### Build con Tests
```bash
mvn clean package -s settings.xml
```

### Solo Compilazione
```bash
mvn clean compile -DskipTests -s settings.xml
```

### Run Diretto da Maven
```bash
mvn spring-boot:run -s settings.xml
```

### Debug Output
```bash
mvn clean package -X -DskipTests -s settings.xml
```

## ⚠️ Se Fallisce

1. Pulisci la cache locale:
   ```bash
   rmdir /s C:\Users\%USERNAME%\.m2\repository
   ```

2. Riprova il build:
   ```bash
   mvn clean package -DskipTests -s settings.xml
   ```

3. Se persiste, verifica:
   - ✅ Connessione a: `https://devops-eproc.it/nexus/`
   - ✅ Java 17+: `java -version`
   - ✅ Maven 3.8+: `mvn -version`

## 📝 Summary

| Elemento | Valore |
|----------|--------|
| Backend | Spring Boot 3.3.5 |
| UI | JavaFX 21.0.2 |
| Java | 17+ |
| Env | Desktop Application |
| Repository | Nexus Interno |
| Build Time | 5-10 min (primo), 1 min (successivi) |

## 🎯 Prossimo Passo

Esegui il build e segnala eventuali errori! 🚀

