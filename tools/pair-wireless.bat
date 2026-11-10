@echo off
title Resellbook - Wireless Pair
chcp 437 >nul
setlocal

set "ADB=adb"
if exist "D:\Android\Sdk\platform-tools\adb.exe" set "ADB=D:\Android\Sdk\platform-tools\adb.exe"

echo ============================================================
echo   Resellbook : pair phone for wireless ADB (one-time)
echo ============================================================
echo   On phone: Settings - Developer options - Wireless debugging
echo             tap "Pair device with pairing code"
echo   It shows an IP:PORT and a 6-digit code. Enter them below.
echo.

if not exist "%ADB%" (
  echo [ERROR] adb not found. Edit the ADB= line at the top.
  pause
  exit /b 1
)

set /p PAIRADDR=Enter PAIRING IP:PORT :
set /p PAIRCODE=Enter 6-digit pairing code :
echo.
echo Pairing %PAIRADDR% ...
"%ADB%" pair %PAIRADDR% %PAIRCODE%
if errorlevel 1 (
  echo.
  echo [FAILED] Pairing failed. Code may be expired or wrong network.
  pause
  exit /b 1
)

echo.
echo [OK] Paired. Now run tools\deploy-debug.bat to build and install.
pause
