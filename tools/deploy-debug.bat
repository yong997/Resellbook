@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

rem ===== 小贩账本：一键编译 + 无线安装 =====
rem 首次使用请先运行 tools\pair-wireless.bat 完成配对。

set "ADB=adb"
if exist "D:\Android\Sdk\platform-tools\adb.exe" set "ADB=D:\Android\Sdk\platform-tools\adb.exe"
if "%JAVA_HOME%"=="" if exist "D:\Android\Android Studio\jbr" set "JAVA_HOME=D:\Android\Android Studio\jbr"

cd /d "%~dp0.."

echo [1/4] 编译 Debug APK（首次可能较慢）...
call gradlew.bat :app:assembleDebug
if errorlevel 1 goto :err

echo [2/4] 检查已连接的手机...
set "FOUND="
for /f "skip=1 tokens=1,2" %%a in ('"%ADB%" devices') do (
    if "%%b"=="device" set "FOUND=1"
)

if not defined FOUND (
    echo   未发现已连接设备。
    echo   请在手机打开：开发者选项 -> 无线调试，界面上会显示 “IP 地址和端口”。
    set /p TARGET=  请输入该 IP:端口（例如 192.168.43.1:37123）:
    echo   正在连接 !TARGET! ...
    "%ADB%" connect !TARGET!
    if errorlevel 1 goto :err
)

echo [3/4] 安装 APK...
"%ADB%" install -r "app\build\outputs\apk\debug\app-debug.apk"
if errorlevel 1 goto :err

echo [4/4] 启动应用...
"%ADB%" shell monkey -p com.xiaofan.ledger -c android.intent.category.LAUNCHER 1 >nul 2>&1

echo.
echo ✅ 已完成：最新版本已安装并启动。
goto :eof

:err
echo.
echo ❌ 失败，请根据上面的提示排查（常见：手机与电脑不在同一网络、无线调试端口变了、未配对）。
exit /b 1
