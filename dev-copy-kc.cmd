@echo off
setlocal enabledelayedexpansion

REM === Config ===
set CONTAINER=user_auth-kc-TEST
set KC_DATA_LOCAL=%CD%\kc-data
set KC_EXPORT_LOCAL=%CD%\services\keycloak\kc-export
set IMAGE=my-keycloak-cieid:26.5.2
set REALM=user-auth

echo.
echo [1/2] Copio /opt/keycloak/data dal container "%CONTAINER%" in "%KC_DATA_LOCAL%"
echo.

REM Pulizia deterministica di kc-data
if exist "%KC_DATA_LOCAL%" (
  echo - Rimuovo cartella esistente: "%KC_DATA_LOCAL%"
  rmdir /s /q "%KC_DATA_LOCAL%"
)

REM Copia dal container
docker cp "%CONTAINER%:/opt/keycloak/data" "%KC_DATA_LOCAL%"
if errorlevel 1 (
  echo ERRORE: docker cp fallito.
  exit /b 1
)

echo.
echo [2/2] Eseguo export del realm "%REALM%" in "%KC_EXPORT_LOCAL%"
echo.

REM Assicura cartella export e pulizia deterministica
if not exist "%KC_EXPORT_LOCAL%" (
  echo - Creo cartella export: "%KC_EXPORT_LOCAL%"
  mkdir "%KC_EXPORT_LOCAL%"
) else (
  echo - Svuoto cartella export: "%KC_EXPORT_LOCAL%"
  del /q "%KC_EXPORT_LOCAL%\*" 2>nul
  for /d %%D in ("%KC_EXPORT_LOCAL%\*") do rmdir /s /q "%%D"
)

REM Avvio container temporaneo per export
docker run --rm ^
  -v "%KC_DATA_LOCAL%:/opt/keycloak/data" ^
  -v "%KC_EXPORT_LOCAL%:/opt/keycloak/data/export" ^
  "%IMAGE%" export --dir /opt/keycloak/data/export --realm "%REALM%" --optimized

if errorlevel 1 (
  echo ERRORE: export fallito.
  exit /b 1
)

echo.
echo OK: export completati. Controlla "%KC_EXPORT_LOCAL%"
exit /b 0
