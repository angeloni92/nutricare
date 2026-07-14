# Build Instructions - Nutricare Desktop Application

## Prerequisiti

- **Java 17+** (obbligatorio per JavaFX)
- **Maven 3.8+**
- **RAM**: 2GB minimo

## Configurazione Maven

Questo progetto usa un file `settings.xml` semplificato che scarica dai **repository pubblici standard di Maven Central**.

### Build Commands

#### Build Standard (Consigliato)

```bash
cd nutricare
mvn clean package -s settings.xml
```

Questo creerà un JAR eseguibile:
- `target/nutricare-0.0.1-SNAPSHOT.jar` (Spring Boot fat JAR)
- `target/nutricare-0.0.1-SNAPSHOT-desktop.jar` (Shade JAR con tutte le dipendenze)

#### Build senza Tests

```bash
mvn clean package -DskipTests -s settings.xml
```

#### Solo Compilazione

```bash
mvn clean compile -DskipTests -s settings.xml
```

#### Run Diretto da Maven

```bash
mvn spring-boot:run -s settings.xml
```

## Esecuzione

### Da Linea di Comando

```bash
# Esecuzione diretta del JAR
java -jar target/nutricare-0.0.1-SNAPSHOT.jar

# Con memoria aumentata
java -Xmx2G -jar target/nutricare-0.0.1-SNAPSHOT.jar

# Debug mode
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar target/nutricare-0.0.1-SNAPSHOT.jar
```

### Da IDE

1. **IntelliJ IDEA**: Click destro su `NutricareApplication.java` → Run
2. **Eclipse**: Click destro sul progetto → Run As → Java Application
3. **VS Code**: Debug → Start Debugging (F5)

## Struttura dei File di Build

```
nutricare/
├── pom.xml                    # Configurazione Maven principale
├── settings.xml               # Settings maven PUBBLICO (usa questo!)
├── target/                    # Output build (JARs, classi compilate, etc.)
│   ├── nutricare-0.0.1-SNAPSHOT.jar
│   ├── nutricare-0.0.1-SNAPSHOT-desktop.jar
│   ├── classes/
│   └── ...
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/angeloni/nutricare/
    │   │       ├── NutricareApplication.java
    │   │       ├── ui/
    │   │       ├── service/
    │   │       ├── repository/
    │   │       └── ...
    │   └── resources/
    │       └── application.properties
    └── test/
```

## Repository Maven Usati

Il file `settings.xml` usa SOLO il repository pubblico standard:

**Maven Central Repository**: https://repo.maven.apache.org/maven2

Questo è il repository ufficiale di Maven, pubblico e affidabile.

## Troubleshooting

### Errore: "Could not transfer artifact"

**Causa**: Problema di connessione ai repository

**Soluzione**:
```bash
# Pulire la cache locale
mvn clean -s settings.xml

# Provare con log verboso
mvn clean compile -X -s settings.xml

# Avviare un nuovo build
mvn clean compile -s settings.xml
```

### Errore: "PKIX path building failed"

**Causa**: Problema di certificato SSL con la connessione HTTPS

**Soluzione**: Se sei dietro un proxy o firewall:
```bash
# Impostare proxy se necessario (configurare in settings.xml)
# Oppure disabilitare SSL strictly (non consigliato):
mvn clean compile -s settings.xml -Dmaven.wagon.http.ssl.insecure=true
```

### Errore: "JavaFX Runtime components are missing"

**Causa**: Java non è 17+ o dipendenze JavaFX non trovate

**Soluzione**:
```bash
# Verificare versione Java
java -version

# Deve essere almeno 17.0.x

# Se necessario, eseguire clean rebuild
mvn clean compile -s settings.xml
```

### Arrivo lento dell'applicazione

**Causa**: H2 database si inizializza, Liquibase migrations in esecuzione

**Soluzione**: Aumentare memoria heap:
```bash
java -Xmx2G -jar target/nutricare-0.0.1-SNAPSHOT.jar
```

## CI/CD Integration

Per usare in pipelines CI/CD (Jenkins, GitLab CI, GitHub Actions):

```bash
mvn clean package -DskipTests -s settings.xml
```

## Note Importanti

⚠️ **Non modificare** `~/.m2/settings.xml` (il settings globale di Maven)  
✅ **Usare** `-s settings.xml` per il build  
✅ **Committare** il file `settings.xml` nel repository  

## Versioni delle Dipendenze

- **Spring Boot**: 3.3.5
- **JavaFX**: 21.0.2
- **Java**: 17
- **Liquibase**: 4.27.0 (da Spring Boot BOM)
- **H2 Database**: 2.2.220 (da Spring Boot BOM)

## Performance

Build typico (primo): 5-10 minuti (con download dipendenze)  
Build cache: 30-60 secondi (builds successivi)  
Build con skip tests: 2-3 minuti (primo)


