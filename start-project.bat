@echo off
setlocal
cd /d "%~dp0"
where powershell >nul 2>&1
if errorlevel 1 (
    echo No se encontro PowerShell.
    exit /b 1
)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start-project.ps1"
exit /b %errorlevel%
