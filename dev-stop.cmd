@echo off
powershell -ExecutionPolicy Bypass -File .\services\scripts\stop-dev.ps1 -ComposeFile ".\docker-compose-test.yml"
