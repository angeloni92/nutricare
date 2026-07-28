# build-installer.ps1 - genera NutriCare-1.1.0.msi in target\installer\
# Prerequisiti: JDK 21, Maven, WiX Toolset 3.x
# Uso: .\build-installer.ps1

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$JDK_HOME = "C:\Program Files\Java\jdk-21.0.9"
$WIX_BIN  = "C:\Program Files (x86)\WiX Toolset v3.14\bin"
$M2_REPO  = "$env:USERPROFILE\.m2\repository\org\openjfx"
$JFX_VER  = "21.0.2"

# --- 1. Maven build ---
Write-Host ""
Write-Host "[1/4] Building fat JAR..." -ForegroundColor Cyan
mvn clean package -DskipTests -q
if ($LASTEXITCODE -ne 0) { Write-Error "Maven build failed"; exit 1 }
Write-Host "      OK - target\nutricare-1.1.0.jar" -ForegroundColor Green

# --- 2. Prepare input directory ---
Write-Host ""
Write-Host "[2/4] Preparing package input..." -ForegroundColor Cyan
$inputDir = "target\pkg-input"
Remove-Item -Recurse -Force $inputDir -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force $inputDir | Out-Null
Copy-Item "target\nutricare-1.1.0.jar" "$inputDir\"
Write-Host "      OK" -ForegroundColor Green

# --- 3. JavaFX module path (Windows platform JARs) ---
$jfxModPath = (
    "$M2_REPO\javafx-base\$JFX_VER\javafx-base-$JFX_VER-win.jar",
    "$M2_REPO\javafx-controls\$JFX_VER\javafx-controls-$JFX_VER-win.jar",
    "$M2_REPO\javafx-fxml\$JFX_VER\javafx-fxml-$JFX_VER-win.jar",
    "$M2_REPO\javafx-graphics\$JFX_VER\javafx-graphics-$JFX_VER-win.jar",
    "$M2_REPO\javafx-swing\$JFX_VER\javafx-swing-$JFX_VER-win.jar"
) -join ";"

# --- 4. Run jpackage ---
Write-Host ""
Write-Host "[3/4] Running jpackage (1-2 minutes)..." -ForegroundColor Cyan

$env:PATH = "$WIX_BIN;$env:PATH"
$outputDir = "target\installer"
Remove-Item -Recurse -Force $outputDir -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force $outputDir | Out-Null

& "$JDK_HOME\bin\jpackage.exe" `
    --type msi `
    --name "NutriCare" `
    --app-version "1.1.0" `
    --input $inputDir `
    --main-jar "nutricare-1.1.0.jar" `
    --icon "src\main\resources\images\logo.ico" `
    --dest $outputDir `
    --module-path $jfxModPath `
    --add-modules "javafx.controls,javafx.fxml,javafx.graphics,javafx.swing" `
    --java-options "--add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.swing" `
    --java-options "-Xmx512m" `
    --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED" `
    --java-options "--add-opens=java.base/java.util=ALL-UNNAMED" `
    --win-menu `
    --win-shortcut `
    --win-dir-chooser `
    --win-menu-group "NutriCare" `
    --vendor "Andrea Angeloni" `
    --description "NutriCare - Nutrition Management System" `
    --copyright "2025 Andrea Angeloni"

if ($LASTEXITCODE -ne 0) { Write-Error "jpackage failed"; exit 1 }

# --- 5. Done ---
$msi = Get-ChildItem $outputDir -Filter "*.msi" | Select-Object -First 1
Write-Host ""
Write-Host "[4/4] Done!" -ForegroundColor Green
Write-Host "      Installer: $($msi.FullName)" -ForegroundColor Yellow
Write-Host "      Size:      $([math]::Round($msi.Length/1MB, 1)) MB" -ForegroundColor Yellow
