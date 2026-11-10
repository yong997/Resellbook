@echo off
title Resellbook - Build and Install
chcp 437 >nul
setlocal enabledelayedexpansion

set "ADB=adb"
if exist "D:\Android\Sdk\platform-tools\adb.exe" set "ADB=D:\Android\Sdk\platform-tools\adb.exe"
if "%JAVA_HOME%"=="" if exist "D:\Android\Android Studio\jbr" set "JAVA_HOME=D:\Android\Android Studio\jbr"

cd /d "%~dp0.."

if not exist "%ADB%" (
  echo [ERROR] adb not found. Edit the ADB= line at the top.
  pause
  exit /b 1
)

echo [1/4] Building debug APK...
call gradlew.bat :app:assembleDebug
if errorlevel 1 (
  echo [FAILED] Build failed.
  pause
  exit /b 1
)

echo [2/4] Checking connected device...
set "FOUND="
for /f "skip=1 tokens=1,2" %%a in ('"%ADB%" devices') do (
  if "%%b"=="device" set "FOUND=1"
)
if not defined FOUND (
  echo   No device connected.
  echo   On phone open Developer options - Wireless debugging; it shows IP address and port.
  set /p TARGET=Enter IP:PORT :
  echo   Connecting !TARGET! ...
  "%ADB%" connect !TARGET!
  if errorlevel 1 (
     echo [FAILED] adb connect failed.
     pause
     exit /b 1
  )
)

echo [3/4] Installing APK...
"%ADB%" install -r "app\build\outputs\apk\debug\app-debug.apk"
if errorlevel 1 (
  echo [FAILED] Install failed.
  pause
  exit /b 1
)

echo [4/4] Launching app...
"%ADB%" shell monkey -p com.xiaofan.ledger -c android.intent.category.LAUNCHER 1 >nul 2>&1

echo.
echo [DONE] Latest version installed and launched.
pause
