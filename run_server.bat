@echo off
REM Ejecutar el servidor
REM Uso: run_server.bat <puerto> <cantidad_mensajes_historial>

set BASE=C:\Users\heber\OneDrive\Documentos\Riact\ChatDistributed

java -cp "%BASE%\bin;%BASE%\lib\*" com.chat.distributed.Server %1 %2
pause
