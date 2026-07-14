# Build Options - Nutricare

## Quick Build Commands

### Opzione 1: Aliyun Mirror (CONSIGLIATO)
```bash
mvn clean package -DskipTests -s settings-aliyun.xml
```

### Opzione 2: Maven Central HTTP (No SSL)
```bash
mvn clean package -DskipTests -s settings.xml
```

### Opzione 3: Con Nexus Interno (Se disponibile)
```bash
mvn clean package -DskipTests
```

## Se il Build Fallisce

### Per vedere gli errori dettagliati:
```bash
mvn clean compile -X -s settings-aliyun.xml
```

### Per forzare download:
```bash
rm -rf ~/.m2/repository
mvn clean package -U -DskipTests -s settings-aliyun.xml
```

### Con disabilitazione SSL (ultima risorsa):
```bash
mvn clean package -DskipTests \
  -Dmaven.wagon.http.ssl.insecure=true \
  -Dmaven.wagon.http.ssl.allowall=true \
  -s settings.xml
```

## Repository Disponibili

| File | Repository | Tipo |
|------|-----------|------|
| settings-aliyun.xml | Aliyun Mirror | HTTPS Mirror |
| settings.xml | Maven Central | HTTP Direct |
| (default) | Nexus Interno | HTTPS Aziendale |

## Build Status

Se ricevi errore di connessione:
1. ✅ Prova con `settings-aliyun.xml` PRIMA
2. ✅ Se fallisce, prova `settings.xml`
3. ✅ Se entrambi falliscono, usa il default (Nexus)

Usa sempre il flag `-s nomefile.xml` quando fai il build!

