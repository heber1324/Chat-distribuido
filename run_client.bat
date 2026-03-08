@echo off
REM Ejecutar un cliente
REM Uso: run_client.bat <ip_servidor> <puerto> <nombre_usuario>

set BASE=C:\Users\heber\OneDrive\Documentos\Riact\ChatDistributed

java -cp "%BASE%\bin;%BASE%\lib\*" com.chat.distributed.Client %1 %2 %3
pause
