batch@echo off
cd /d "%~dp0"

REM Set paths
set LIB_PATH=lib
set JAVAFX_DLL_PATH=javafx-dlls

set PATH=%JAVAFX_DLL_PATH%;%PATH%

java --module-path "%LIB_PATH%;bin" --add-modules ShoppingCartFX --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -m ShoppingCartFX/application.Main

batch@echo off
cd /d "%~dp0"

REM Set paths
set LIB_PATH=lib
set JAVAFX_DLL_PATH=javafx-dlls

set PATH=%JAVAFX_DLL_PATH%;%PATH%

java --module-path "%LIB_PATH%;bin" --add-modules ShoppingCartFX --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED -m ShoppingCartFX/application.Main

pause