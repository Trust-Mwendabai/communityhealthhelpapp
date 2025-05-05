@echo off
echo Cleaning Android project to free up disk space...

echo Removing build directories...
rmdir /S /Q app\build
rmdir /S /Q build

echo Removing unnecessary cached data...
rmdir /S /Q "%USERPROFILE%\.gradle\caches\transforms-3"
rmdir /S /Q "%USERPROFILE%\.gradle\caches\transforms-2"
rmdir /S /Q "%USERPROFILE%\.gradle\caches\journal-1"

echo Removing Android Studio cache files...
rmdir /S /Q .idea\libraries
rmdir /S /Q .idea\modules
rmdir /S /Q .idea\caches

echo Done! You can now restart Android Studio and rebuild your project with optimized settings.
pause
