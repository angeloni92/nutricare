#!/bin/bash
# Nutricare Build Script - WITH SSL DISABLED

echo "Building Nutricare Desktop Application..."
echo "SSL verification: DISABLED"
echo ""

cd "C:\Users\andrea.angeloni\repos\nutricare"

mvn clean package -DskipTests -s settings.xml \
  -Dmaven.wagon.http.ssl.insecure=true \
  -Dmaven.wagon.http.ssl.allowall=true \
  -Dmaven.wagon.http.ssl.ignore.validity.dates=true

if [ $? -eq 0 ]; then
  echo ""
  echo "✅ Build SUCCESS!"
  echo ""
  echo "JAR file location:"
  echo "  - target/nutricare-0.0.1-SNAPSHOT.jar"
  echo ""
  echo "Run the application:"
  echo "  java -Xmx2G -jar target/nutricare-0.0.1-SNAPSHOT.jar"
else
  echo ""
  echo "❌ Build FAILED!"
  exit 1
fi

