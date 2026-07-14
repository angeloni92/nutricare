# Nutricare Desktop Application

Una moderna applicazione desktop per la gestione nutrizionale, costruita con **Spring Boot** e **JavaFX**.

## Architettura

L'applicazione integra completamente:
- **Backend**: Spring Boot con JPA, Hibernate, Liquibase
- **Frontend**: JavaFX (UI desktop nativa)
- **Database**: H2 (embedded)
- **API**: REST per comunicazioni interne
- **AI Integration**: Supporto per GPT-4, Claude, Gemini

## Requisiti

- **Java 17+** (mandatorio per JavaFX)
- **Maven 3.8+**
- **Memoria RAM**: 2GB minimo

## Caratteristiche Principali

✅ **Autenticazione**: Login/Password con JWT Token  
✅ **Gestione Clienti**: CRUD completo per clienti  
✅ **Gestione Diete**: Creazione e monitoraggio diete  
✅ **Generatore AI**: Diete generate automaticamente con modelli AI  
✅ **Database Interno**: H2 embedded, nessuna dipendenza esterna  
✅ **Desktop Native**: Interfaccia JavaFX native e responsiva  

## Build e Esecuzione

### Metodo 1: Build diretta con Maven

```bash
cd nutricare
mvn clean package -s settings.xml
```

Questo genererà un file JAR eseguibile:
```bash
java -jar target/nutricare-0.0.1-SNAPSHOT.jar
```

Oppure con più memoria:
```bash
java -Xmx2G -jar target/nutricare-0.0.1-SNAPSHOT.jar
```

### Metodo 2: Build senza Test

```bash
mvn clean package -DskipTests -s settings.xml
```

### Metodo 3: Run da IDE

Dall'IDE (IntelliJ IDEA, Eclipse, VS Code):
1. Cliccare su "Run" o eseguire con F5
2. La classe principale è: `com.angeloni.nutricare.NutricareApplication`

### Metodo 4: Maven Run

```bash
mvn spring-boot:run -s settings.xml
```

## Struttura del Progetto

```
src/main/java/com/angeloni/nutricare/
├── NutricareApplication.java          # Entry point (Spring + JavaFX)
├── config/
│   ├── SecurityConfig.java            # Spring Security configuration
│   ├── ModelMapperConfig.java          # DTO mapping configuration
│   ├── RestClientConfig.java           # REST template configuration
│   └── WebMvcConfig.java               # Web configuration (se needed)
├── ui/
│   ├── StageManager.java               # Scene navigation manager
│   ├── SceneBuilder.java               # Scene factory
│   ├── ApplicationInitializer.java     # UI lifecycle management
│   ├── controller/                     # JavaFX FXML controllers
│   │   ├── LoginController.java
│   │   ├── DashboardController.java
│   │   ├── ClientController.java
│   │   ├── DietController.java
│   │   └── DietGeneratorController.java
│   └── service/
│       └── UiAuthService.java          # UI authentication service
├── service/                            # Business logic services
├── repository/                         # Data access layer
├── entity/                             # JPA entities
├── dto/                                # Data Transfer Objects
├── controller/                         # REST API controllers (se needed)
└── exception/                          # Custom exceptions
```

## Configurazione

### application.properties

File principal di configurazione: `src/main/resources/application.properties`

Variabili importanti:
- `spring.main.web-application-type=none` → Disabilita Tomcat (app desktop)
- `spring.datasource.url` → Connection string database
- `nutricare.openai.api-key` → API key per GPT-4
- `nutricare.anthropic.api-key` → API key per Claude

## Funzionalità

### 1. Autenticazione
- Login con email/password
- Registrazione nuovi utenti
- Token JWT per sessioni
- GitHub Copilot SSO (opzionale)

### 2. Gestione Clienti
- Crea, leggi, aggiorna, elimina clienti
- Profili antropometrici
- Preferenze dietariche
- Storico misurazioni

### 3. Gestione Diete
- Visualizza tutte le diete
- Stato diete (attiva, completata, archiviata)
- Esporta diete in PDF
- Storico modifiche

### 4. Generatore IA
- Genera diete con AI (GPT-4, Claude, Gemini)
- Parametri personalizzabili:
  - Obiettivo principale (peso, muscoli, salute)
  - Preferenza dietetica (omnivoro, vegetariano, vegano)
  - Livello attività
  - Numero pasti al giorno
- Feedback in tempo reale

## API REST Interne

L'applicazione espone API REST interne su `http://localhost:8080/api/nutricare`:

```
POST   /api/nutricare/auth/login          - Login
POST   /api/nutricare/auth/register       - Registrazione
GET    /api/nutricare/clients             - Lista clienti
POST   /api/nutricare/clients             - Crea cliente
PUT    /api/nutricare/clients/{id}        - Modifica cliente
DELETE /api/nutricare/clients/{id}        - Elimina cliente
GET    /api/nutricare/diets               - Lista diete
POST   /api/nutricare/diets/generate      - Genera dieta con AI
```

## Troubleshooting

### Errore: "JavaFX Runtime components are missing"
Soluzione: Assicurarsi che Java 17+ sia installato e che il pom.xml abbia le dipendenze JavaFX.

### Errore: "Database connection failed"
Soluzione: Verificare che la porta non sia in uso. H2 in-memory non richiede configurazione aggiuntiva.

### Applicazione lenta all'avvio
Soluzione: Aumentare heap memory:
```bash
java -Xmx2G -jar target/nutricare-0.0.1-SNAPSHOT.jar
```

## Versionamento

- **Java**: 17
- **Spring Boot**: 3.3.5
- **JavaFX**: 21.0.2
- **Angular** (legacy UI): 18.2.0

## Note di Sviluppo

1. **Aggiungi nuove schermate**: Crea controller in `ui/controller/` e builder method in `SceneBuilder.java`
2. **Aggiungi business logic**: Aggiungi servizi in `service/`
3. **Aggiungi entità**: Crea entità in `entity/` e repository in `repository/`
4. **Database migration**: Usa Liquibase changelog in `db/changelog/`

## Debug

Per abilitare debug mode, modificare `application.properties`:
```properties
logging.level.com.angeloni.nutricare=DEBUG
logging.level.org.springframework=DEBUG
```

## Crediti

Progetto originale: **nutriui** (Angular 18) → Convertito a **JavaFX** per app desktop integrata.

## Licenza

Proprietario

