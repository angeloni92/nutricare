# sccm-deploy.ps1 — NutriCare 1.1.0 silent deployment script
# Compatible with: SCCM, Microsoft Intune (Win32 app), manual IT deployment
# Run as: SYSTEM or Administrator
#
# SCCM / Intune Win32 App settings:
#   Install command:   powershell.exe -ExecutionPolicy Bypass -File sccm-deploy.ps1 -Action Install
#   Uninstall command: powershell.exe -ExecutionPolicy Bypass -File sccm-deploy.ps1 -Action Uninstall
#   Detection rule:    Registry  HKLM:\SOFTWARE\NutriCare  key "Version" value "1.1.0"

param(
    [ValidateSet("Install", "Uninstall")]
    [string]$Action = "Install"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$MSI_NAME    = "NutriCare-1.1.0.msi"
$APP_NAME    = "NutriCare"
$APP_VERSION = "1.1.0"
$UPGRADE_CODE = "{9DEE3B57-859A-40DF-8B09-2F6CF5AC2048}"
$LOG_DIR     = "$env:SystemRoot\Logs\Software"
$LOG_FILE    = "$LOG_DIR\NutriCare-$Action.log"
$SCRIPT_DIR  = Split-Path -Parent $MyInvocation.MyCommand.Definition
$MSI_PATH    = Join-Path $SCRIPT_DIR $MSI_NAME

function Write-Log {
    param([string]$Msg)
    $line = "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')  $Msg"
    Write-Host $line
    Add-Content -Path $LOG_FILE -Value $line -Encoding UTF8
}

# Ensure log directory exists
if (-not (Test-Path $LOG_DIR)) { New-Item -ItemType Directory -Force $LOG_DIR | Out-Null }

Write-Log "=== NutriCare $APP_VERSION — $Action ==="

if ($Action -eq "Install") {

    if (-not (Test-Path $MSI_PATH)) {
        Write-Log "ERROR: MSI not found at $MSI_PATH"
        exit 1
    }

    Write-Log "Starting silent install..."
    $args = @(
        "/i", "`"$MSI_PATH`"",
        "/quiet",
        "/norestart",
        "/l*v", "`"$LOG_FILE`""
    )
    $proc = Start-Process msiexec.exe -ArgumentList $args -Wait -PassThru
    Write-Log "msiexec exit code: $($proc.ExitCode)"

    if ($proc.ExitCode -eq 0 -or $proc.ExitCode -eq 3010) {
        Write-Log "Install completed successfully."
        # 3010 = success, reboot required (we pass /norestart so IT controls the reboot)
        exit 0
    } else {
        Write-Log "ERROR: Install failed with exit code $($proc.ExitCode). See $LOG_FILE"
        exit $proc.ExitCode
    }

} elseif ($Action -eq "Uninstall") {

    Write-Log "Starting silent uninstall (by upgrade code)..."
    $args = @(
        "/x", $UPGRADE_CODE,
        "/quiet",
        "/norestart",
        "/l*v", "`"$LOG_FILE`""
    )
    $proc = Start-Process msiexec.exe -ArgumentList $args -Wait -PassThru
    Write-Log "msiexec exit code: $($proc.ExitCode)"

    if ($proc.ExitCode -eq 0 -or $proc.ExitCode -eq 1605) {
        Write-Log "Uninstall completed (1605 = product not found, already clean)."
        exit 0
    } else {
        Write-Log "ERROR: Uninstall failed with exit code $($proc.ExitCode). See $LOG_FILE"
        exit $proc.ExitCode
    }
}
