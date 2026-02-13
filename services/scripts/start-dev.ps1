param(
  [string]$ComposeFile = ".\docker-compose-test.yml",
  [string]$Realm = "user-auth",
  [string]$NgrokConfig = ".\ngrok.yml",
  [string]$EnvTsPath = ".\user-mobile\src\shared\config\env.ts",
  [string]$MobileDir = ".\user-mobile"
)

$ErrorActionPreference = "Stop"

function Wait-HttpGet([string]$Url, [int]$Retries = 60, [int]$DelayMs = 1000) {
  for ($i=0; $i -lt $Retries; $i++) {
    try {
      $r = Invoke-WebRequest -Uri $Url -Method Get -TimeoutSec 5 -MaximumRedirection 5
      if ($r.StatusCode -ge 200 -and $r.StatusCode -lt 500) { return $true }
    } catch { }
    Start-Sleep -Milliseconds $DelayMs
  }
  return $false
}

function Get-NgrokPublicUrl([int]$Retries = 30, [int]$DelayMs = 500) {
  for ($i=0; $i -lt $Retries; $i++) {
    try {
      $obj = Invoke-RestMethod -Uri "http://127.0.0.1:4040/api/tunnels" -Method Get
      $json = $obj | ConvertTo-Json -Depth 30
      $m = [regex]::Match($json, 'https:\/\/[a-zA-Z0-9\-\.]+ngrok[^\s"\\]+')
      if ($m.Success) { return $m.Value }
    } catch { }
    Start-Sleep -Milliseconds $DelayMs
  }
  return $null
}

Write-Host "==> Starting docker services (no-build)..."
docker compose -f $ComposeFile up -d --no-build --remove-orphans gateway keycloak_test | Out-Host

Write-Host "==> Waiting local gateway..."
if (-not (Wait-HttpGet "http://localhost:9001/ala/swagger-ui/index.html")) { throw "Local /ala not responding" }
if (-not (Wait-HttpGet "http://localhost:9001/kc/realms/$Realm/.well-known/openid-configuration")) {
  throw "Local Keycloak well-known not responding"
}

Write-Host "==> Stopping existing ngrok (if any)..."
Stop-Process -Name ngrok -Force -ErrorAction SilentlyContinue

Write-Host "==> Starting ngrok (gateway) in new window..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "ngrok start gateway --config=$NgrokConfig"

Write-Host "==> Waiting ngrok public URL..."
$pub = Get-NgrokPublicUrl
if (-not $pub) { throw "Cannot find ngrok public URL from http://127.0.0.1:4040/api/tunnels" }

Write-Host "==> ngrok public URL: $pub"

if (Test-Path $EnvTsPath) {
  $content = Get-Content $EnvTsPath -Raw
  $content = $content -replace '(GATEWAY_BASE_URL:\s*)".*?"', ('$1"' + $pub + '"')
  $content = $content -replace '(API_BASE_URL:\s*)".*?"', ('$1"' + $pub + '/ala"')
  $content = $content -replace '(KEYCLOAK_ISSUER:\s*)".*?"', ('$1"' + $pub + '/kc/realms/' + $Realm + '"')
  Set-Content -Path $EnvTsPath -Value $content -Encoding UTF8
  Write-Host "==> Updated $EnvTsPath"
} else {
  Write-Warning "env.ts not found: $EnvTsPath (skip update)"
}

Write-Host "==> Starting Expo in new window..."
if (Test-Path $MobileDir) {
  Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd `"$MobileDir`"; npx expo start"
} else {
  Write-Warning "MobileDir not found: $MobileDir (skip Expo start)"
}

Write-Host ""
Write-Host "DONE"
Write-Host "Test from phone:"
Write-Host " - $pub/ala/swagger-ui/index.html"
Write-Host " - $pub/kc/"
Write-Host " - $pub/kc/realms/$Realm/.well-known/openid-configuration"
