param(
  [string]$ComposeFile = ".\docker-compose-test.yml"
)

$ErrorActionPreference = "SilentlyContinue"

Stop-Process -Name ngrok -Force
docker compose -f $ComposeFile down
