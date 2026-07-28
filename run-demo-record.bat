@echo off
echo ============================================
echo  NutriCare - Demo Recording
echo  Output: %USERPROFILE%\nutricare-demo\
echo ============================================
echo.

REM Pulisce i frame precedenti se esistono
if exist "%USERPROFILE%\nutricare-demo\frames\" (
    echo Removing previous frames...
    rmdir /s /q "%USERPROFILE%\nutricare-demo\frames"
)
if exist "%USERPROFILE%\nutricare-demo\nutricare-demo.mp4" (
    del /q "%USERPROFILE%\nutricare-demo\nutricare-demo.mp4"
)

echo Starting app in demo-record mode...
echo (The app will close automatically when the video is ready)
echo.

set NUTRICARE_DEMO_RECORD=true
mvn javafx:run

echo.
echo Done. Check: %USERPROFILE%\nutricare-demo\nutricare-demo.mp4
pause
