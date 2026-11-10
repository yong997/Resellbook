@echo off
chcp 65001 >nul
setlocal

rem ===== 首次无线配对（每台手机/每次重置无线调试后做一次）=====
rem 步骤：手机 开发者选项 -> 无线调试 -> “使用配对码配对设备”，会显示 IP:端口 与 6 位配对码。

set "ADB=adb"
if exist "D:\Android\Sdk\platform-tools\adb.exe" set "ADB=D:\Android\Sdk\platform-tools\adb.exe"

echo.
echo 请先在手机上进入：设置 -> 开发者选项 -> 无线调试 -> 使用配对码配对设备
echo 屏幕上会显示：IP 地址和端口（配对用）、以及一个 6 位配对码。
echo.
set /p PAIRADDR=请输入“配对用”的 IP:端口（例如 192.168.43.1:41234）:
set /p PAIRCODE=请输入 6 位配对码:

echo.
echo 正在配对 %PAIRADDR% ...
"%ADB%" pair %PAIRADDR% %PAIRCODE%
if errorlevel 1 (
    echo.
    echo ❌ 配对失败：请确认手机与电脑在同一网络、配对端口与配对码正确且未过期。
    pause
    exit /b 1
)

echo.
echo ✅ 配对成功。以后直接运行 tools\deploy-debug.bat 即可编译并安装。
echo    安装时如提示输入 IP:端口，请填“无线调试”主页面上显示的 IP 和端口（与配对端口不同）。
pause
