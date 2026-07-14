# Nutricare - Desktop Application Setup

## ✅ Configurazione Completata

Questo progetto è stato convertito da un'applicazione Angular web a un'**applicazione desktop self-contained** con Spring Boot e JavaFX.

## 📋 Prerequisiti

- ✅ **Java 17+** (obbligatorio)
- ✅ **Maven 3.8+**
- ✅ **RAM**: 2GB minimo
- ✅ **Connessione Internet** (per il primo build)

## 🚀 Quick Start

```bash
# 1. Vai nella cartella del progetto
cd C:\Users\andrea.angeloni\repos\nutricare

# 2. Build dell'applicazione
mvn clean package -DskipTests -s settings.xml

# 3. Esegui l'app
java -Xmx2G -jar target/nutricare-0.0.1-SNAPSHOT.jar
```

## 📝 Comandi Build Disponibili

### Build Completo (creazione JAR)
```bash
mvn clean package -s settings.xml
```

### Build Veloce (senza test)
```bash
mvn clean package -DskipTests -s settings.xml
```

### Solo Compilazione
```bash
mvn clean compile -DskipTests -s settings.xml
```

### Run Diretto da Maven
```bash
mvn spring-boot:run -s settings.xml
```

## 🎯 Repository Maven

Questo progetto usa SOLO i **repository pubblici standard Maven**:

- **Maven Central**: https://repo.maven.apache.org/maven2

Nessuna configurazione aziendale richiesta.

## ▶️ Esecuzione

### Da Linea di Comando
```bash
java -jar target/nutricare-0.0.1-SNAPSHOT.jar
```

### Con Più Memoria (Consigliato)
```bash
java -Xmx2G -jar target/nutricare-0.0.1-SNAPSHOT.jar
```

### Debug Mode
```bash
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 \
  -jar target/nutricare-0.0.1-SNAPSHOT.jar
```

### Da IDE
1. **IntelliJ IDEA**: Click su `NutricareApplication.java` → Run
2. **Eclipse**: Run As → Java Application
3. **VS Code**: F5 (Debug)

## 📁 Struttura

```
nutricare/
├── settings.xml                    # ⭐ Maven Central public repos
├── pom.xml                         # Spring Boot + JavaFX deps
├── src/main/java/com/angeloni/nutricare/
│   ├── NutricareApplication.java   # Entry point
│   ├── ui/                         # JavaFX UI controllers
│   ├── service/                    # Business logic
│   ├── repository/                 # Data access
│   ├── entity/                     # JPA entities
│   └── ...
└── target/                         # Build output
    ├── nutricare-0.0.1-SNAPSHOT.jar
    └── ...
```

## 🔧 Tecnologie

- **Backend**: Spring Boot 3.3.5
- **Frontend Desktop**: JavaFX 21.0.2
- **Database**: H2 (embedded)
- **ORM**: Hibernate/JPA
- **Migrations**: Liquibase
- **Java**: 17

## ✨ Funzionalità

✅ **Autenticazione** - Login/Password con JWT  
✅ **Gestione Clienti** - CRUD completo  
✅ **Gestione Diete** - Creazione e monitoraggio  
✅ **AI Diet Generator** - Diete generate da GPT-4/Claude  
✅ **Database Embedded** - H2, nessuna dipendenza esterna  
✅ **Desktop Native** - UI JavaFX nativa  
✅ **Standalone JAR** - Eseguibile su qualsiasi macchina con Java 17+  

## 🐛 Troubleshooting

### Error: "Could not transfer artifact"
```bash
# Pulire la cache
mvn clean -s settings.xml

# Retry with verbose
mvn clean compile -X -s settings.xml
```

### Error: "JavaFX Runtime components are missing"
```bash
# Verificare Java
java -version  # Deve essere 17.0.x o superior

# Rebuild
mvn clean compile -DskipTests -s settings.xml
```

### Applicazione lenta all'avvio
- ✅ Prima volta: 30-40 secondi (database + migrations)
- ✅ Volte successive: 5-10 secondi
- 💡 Aggiungi memoria: `java -Xmx2G -jar...`

## 📚 Documentazione Aggiuntiva

- **BUILD.md** - Guida dettagliata build
- **DESKTOP_README.md** - Guida desktop app completa
- **MAVEN_SETTINGS.md** - Opzioni repository (legacy reference)

## 🔑 Note Importanti

⚠️ **IMPORTANTE**:
- ✅ Usa SEMPRE: `mvn clean package -s settings.xml`
- ❌ Non modificare: `~/.m2/settings.xml` globale
- ✅ Committare il file `settings.xml` nel repo

## 🎨 UI Screens

L'applicazione desktop include:

1. **Login Screen** - Autenticazione utente
2. **Dashboard** - Menu principale con navigazione
3. **Client Management** - Elenco e CRUD clienti
4. **Diet Management** - Elenco diete con filtri
5. **AI Diet Generator** - Generatore diete con AI

## 📊 Build Performance

| Scenario | Tempo |
|----------|-------|
| First build (download deps) | 5-10 min |
| Rebuild with cache | 30-60 sec |
| Skip tests | -50% tempo |
| Clean compile only | 2-3 min |

## ✅ Stato Progetto

- ✅ Spring Boot 3.3.5 configurato
- ✅ JavaFX 21.0.2 integrato
- ✅ UI Controllers creati (5 schermate)
- ✅ Repository pubblici configurati
- ✅ Settings.xml semplificato
- ✅ Documentazione completata
- ✅ Pronto per il build

## 🚀 Prossimi Passi

1. Esegui il build: `mvn clean package -DskipTests -s settings.xml`
2. Avvia l'app: `java -Xmx2G -jar target/nutricare-0.0.1-SNAPSHOT.jar`
3. Goditi l'applicazione desktop Nutricare!

---

**Domande?** Consulta BUILD.md o DESKTOP_README.md per dettagli aggiuntivi.

