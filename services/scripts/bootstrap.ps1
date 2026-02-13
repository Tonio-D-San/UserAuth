param(
  [string]$ComposeFile = ".\docker-compose-test.yml",
  [string]$MobileDir = ".\user-mobile",
  [string]$NgrokYml = ".\ngrok.yml"
)

$ErrorActionPreference = "Stop"

function Require-Cmd($name) {
  if (-not (Get-Command $name -ErrorAction SilentlyContinue)) {
    throw "Missing command: $name (install it and retry)"
  }
}

Write-Host "==> Checking required tools..."
Require-Cmd node
Require-Cmd npm
Require-Cmd docker
Require-Cmd ngrok

Write-Host "==> node:" (node -v)
Write-Host "==> npm:" (npm -v)
Write-Host "==> docker:" (docker -v)
Write-Host "==> ngrok:" (ngrok version)

Write-Host "==> Checking docker compose file..."
if (-not (Test-Path $ComposeFile)) { throw "Compose file not found: $ComposeFile" }

Write-Host "==> Ensuring ngrok.yml exists..."
if (-not (Test-Path $NgrokYml)) {
  @"
version: "2"
tunnels:
  gateway:
    proto: http
    addr: 9001
"@ | Set-Content -Path $NgrokYml -Encoding UTF8
  Write-Host "Created $NgrokYml"
} else {
  Write-Host "Found $NgrokYml"
}

Write-Host "==> Installing mobile dependencies..."
if (-not (Test-Path $MobileDir)) { throw "MobileDir not found: $MobileDir" }

Push-Location $MobileDir
npm install
Pop-Location

Write-Host "==> Pulling docker images (optional but useful)..."
docker compose -f $ComposeFile pull | Out-Host

Write-Host ""
Write-Host "DONE - Bootstrap complete."
Write-Host ""
Write-Host "Next manual steps (one-time):"
Write-Host "1) Set NGROK_AUTHTOKEN (in .env or as env var). Example: setx NGROK_AUTHTOKEN <TOKEN>"
Write-Host "   If you use ngrok CLI instead of Docker: ngrok config add-authtoken <TOKEN>"
Write-Host "2) Install Expo Go on Android"
Write-Host ""
Write-Host "Then run:"
Write-Host "  .\services\scripts\start-dev.ps1 -ComposeFile `"$ComposeFile`""
