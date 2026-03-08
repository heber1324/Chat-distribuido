@echo off
REM Compilar todas las clases Java con MySQL Connector incluido

set BASE=C:\Users\heber\OneDrive\Documentos\Riact\ChatDistributed

REM Asegurarse de que la carpeta bin exista
if not exist "%BASE%\bin" mkdir "%BASE%\bin"

REM Compilar todos los archivos .java del paquete
for %%f in ("%BASE%\src\com\chat\distributed\*.java") do (
    echo Compilando %%f
    javac -cp "%BASE%\lib\*;%BASE%\bin" -d "%BASE%\bin" "%%f"
)

echo ==============================
echo ✅ Compilación finalizada
echo ==============================
pause