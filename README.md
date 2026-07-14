# NutriCare Pro - Desktop Application

Applicazione desktop unificata per Nutritionisti con AI multi-provider, SSO Copilot, e database H2 embedded.

## Architettura Integrata

Questo progetto integra tre microservizi originali in un'unica applicazione desktop:
- **Backend**: Spring Boot 3.3.5 (JPA/Hibernate)
- **Frontend**: Dashboard HTML5 + TailwindCSS + AlpineJS
- **AI**: Supporto multiplo (OpenAI GPT-4O, Claude, GitHub Copilot)
- **Database**: H2 Embedded (Liquibase migrations)

## Features

✅ **Multi-Provider AI**
- OpenAI GPT-4O / GPT-3.5-Turbo
- Anthropic Claude 3.5 Sonnet
- GitHub Copilot (Enterprise SSO-ready)

✅ **Enterprise Auth**
- JWT-based authentication
- GitHub Copilot OAuth2 integration
- Role-based access control (User/Admin)

✅ **Diet Management**
- Personalized diet generation via AI
- Client anthropometric data tracking
- Historical diet results storage
- Integration con Python nutriask service

✅ **Professional UI**
- Responsive dashboard (Tailwind CSS)
- Real-time generation status
- Modern glassmorphism design

## Configurazione

### Variabili d'Ambiente Essenziali

```properties
# Database (default: H2 embedded, automatic initialization)
spring.datasource.url=jdbc:h2:mem:nutricare;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE

# AI Providers (opzionali)
nutricare.openai.api-key=sk-...       # OpenAI API key
nutricare.anthropic.api-key=ant-...   # Anthropic API key

# GitHub Copilot SSO
nutricare.copilot.oauth.client-id=your-github-app-id
nutricare.copilot.oauth.client-secret=your-github-app-secret
nutricare.copilot.oauth.redirect-uri=http://localhost:8080/api/nutricare/auth/copilot/callback
nutricare.copilot.crypto.secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=

# Server
server.port=8080
server.servlet.context-path=/api/nutricare
```

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Avviare l'applicazione

```bash
# Clone & navigate
cd nutricare

# Build
mvn clean package

# Run
mvn spring-boot:run
```

L'app sarà disponibile a:
- **Backend API**: http://localhost:8080/api/nutricare
- **Dashboard**: http://localhost:8080/api/nutricare/dashboard
- **H2 Console**: http://localhost:8080/api/nutricare/h2-console

### Credenziali di test

```
Email: test@nutricare.local
Password: (registrati via endpoint /auth/register)
```

## API Endpoints

### Authentication
```
POST   /auth/register              - Register new nutritionist
POST   /auth/login                 - Login
GET    /auth/confirm?token=XXX     - Email confirmation
```

### Copilot SSO
```
GET    /auth/copilot/start         - Start OAuth flow
GET    /auth/copilot/callback      - OAuth callback handler
GET    /auth/copilot/status        - Check connection status
DELETE /auth/copilot               - Disconnect account
```

### AI Management
```
GET    /ai/ais                     - List available AI providers
```

### Diet Generation
```
POST   /diet/generate              - Initiate diet generation
```

### Clients
```
POST   /client                     - Create new client
GET    /client                     - List clients
PUT    /client/{id}                - Update client
DELETE /client/{id}                - Delete client
```

## Project Structure

```
src/main/
├── java/com/angeloni/nutricare/
│   ├── ai/                        # AI Handler chain (OpenAI, Claude, Copilot)
│   ├── check/                     # Strategy pattern for AI validation
│   ├── config/                    # Security & ModelMapper configuration
│   ├── controller/                # REST endpoints
│   ├── dto/                       # Data transfer objects
│   ├── entity/                    # JPA entities
│   ├── enums/                     # AI models, roles, goals
│   ├── exception/                 # Custom exceptions & handlers
│   ├── message/                   # ActiveMQ message models
│   ├── producer/                  # ActiveMQ message producer
│   ├── repository/                # JPA repositories
│   ├── service/                   # Business logic
│   └── util/                      # Crypto, JWT, data processing
│
├── resources/
│   ├── application.properties     # Full config
│   ├── templates/dashboard.html   # Frontend (Tailwind + Alpine)
│   └── db/changelog/              # Liquibase DDL/DML
│
└── test/resources/
    └── application-test.properties
```

## Diet Generation Flow

```
Frontend (Dashboard)
    ↓
DietController /diet/generate
    ↓
DietServiceImpl.generateDiet()
    ↓
AIHandlerFactory (Chain of Responsibility)
    ├→ ChatGPT4Handler
    ├→ ClaudeHandler
    └→ CopilotHandler
    ↓
PromptService (Template replacement)
    ↓
DietGenerationService (Async execution)
    ↓
DietResultRepository (Persist result)
    ↓
DietStartProducer (Send to nutriask via ActiveMQ)
```

## Integrazione con Python nutriask

Il backend invia messaggi JSON della dieta a una coda ActiveMQ:

```json
{
  "userId": 123,
  "dietRequest": {
    "ai": {"name": "CHATGPT", "model": "GPT4O", "aiKey": "..."},
    "clientRequest": {...},
    "clientAge": 34,
    "goal": "LOSE_WEIGHT"
  }
}
```

Il servizio `nutriask` (Python Django) riceve il messaggio e:
1. Estrae i parametri
2. Seleziona il modello AI appropriato
3. Esegue il chain handler (AI Workflow)
4. Salva il risultato e notifica l'utente

## Database Migrations

Liquibase gestisce automaticamente le migrazioni:

```sql
-- Tabelle principali
users
ai_provider s
ai_users (relazione user ↔ provider)
clients
diet_results
copilot_connections (SSO tokens cifrati)
anthropometries, circumferences, folds
```

Aggiungere migrazione nuova:
```xml
<!-- src/main/resources/db/changelog/ddl/changelog.xml -->
<changeSet id="create_new_table" author="angeloni">
    <sqlFile path="db/changelog/ddl/new_table/create_new_table.sql" dbms="mysql"/>
    <sqlFile path="db/changelog/ddl/new_table/create_new_table.sql" dbms="h2"/>
</changeSet>
```

## Security

- **JWT Token**: Scadenza 1 ora, refresh token 7 giorni
- **Password**: BCrypt encoding (cost: 10)
- **OAuth SSO**: GitHub Copilot con SAML enterprise support
- **Token Encryption**: AES-GCM per Copilot tokens a riposo
- **CORS**: Limitato a http://localhost:4200

## Development

### Run tests
```bash
mvn test
```

### Format code
```bash
mvn formatter:format
```

### Build & Deploy
```bash
# Build JAR
mvn clean package

# Run with custom config
java -jar target/nutricare-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:h2:file:./nutricare \
  --nutricare.openai.api-key=sk-...
```

## Troubleshooting

### Database Connection Errors
- Verificare H2 console: http://localhost:8080/api/nutricare/h2-console
- Login: user=sa, password=(empty)
- Reset DB: rimuv ere il file `nutricare.db` se usando file persistence

### JWT Token Expired
- Frontend deve gestire il refresh token
- POST /auth/login ritorna: `{"token": "Bearer eyJ...", "refreshToken": "..."}`

### Copilot OAuth Fails
- Verificare le credenziali GitHub App in application.properties
- Confermare che redirect_uri corrisponde esattamente

## Future Enhancements

- [ ] Web UI (React/Vue conversion)
- [ ] Mobile app (React Native)
- [ ] Multi-language support
- [ ] Advanced analytics (diete storiche per client)
- [ ] Integration con servizi di NFS (delivery diete)
- [ ] Batch diet generation

## License

MIT

## Author

Andrea Angeloni - NutriCare Development Team 2026


