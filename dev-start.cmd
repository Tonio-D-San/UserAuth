@echo off
powershell -ExecutionPolicy Bypass -File .\services\scripts\start-dev.ps1 -ComposeFile ".\docker-compose-test.yml"
