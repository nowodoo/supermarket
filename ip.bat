@echo off
color a
ver|find "5."
if %errorlevel% equ 1 (goto :win7) else (goto :win xp)
goto :%tz%
:win xp
cls
ipconfig|find /i "ip address">"%temp%\159357.txt"
set /p IP=<"%temp%\159357.txt"
set ip=%ip:. =%
set ip=%ip: =%
set ip=%ip::=%
set ip=%ip:: =%
set ip=%ip:IPADDress=%
echo %ip%
del /f /s /q "%temp%\159357.txt">nul 2>nul
pause
exit
:win7
cls
ipconfig|find /i "IPv4">"%temp%\159357.txt"
set /p ip=<"%temp%\159357.txt"
set ip=%ip:. =%
set ip=%ip: =%
set ip=%ip::=%
set ip=%ip:IPv4µØÖ·=%
echo %ip%
pause
del /f /s /q "%temp%\159357.txt">nul2>nul
exit