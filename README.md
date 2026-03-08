💬 Chat Distribuido en Java

Aplicación de chat distribuido desarrollada en Java, basada en una arquitectura cliente-servidor, que permite la comunicación entre múltiples usuarios conectados a un servidor central.

El sistema utiliza sockets TCP para gestionar la comunicación entre clientes y servidor, permitiendo el envío y recepción de mensajes en tiempo real mediante una interfaz gráfica.

📌 Descripción del Proyecto

Este proyecto implementa un sistema de mensajería distribuido donde varios clientes pueden conectarse a un servidor central para intercambiar mensajes.

El servidor se encarga de:

Aceptar múltiples conexiones de clientes

Gestionar cada conexión mediante hilos

Recibir y distribuir mensajes entre los usuarios conectados

Los clientes cuentan con una interfaz gráfica (GUI) que permite:

Conectarse al servidor

Enviar mensajes

Visualizar mensajes recibidos en tiempo real

Este tipo de arquitectura es común en aplicaciones de comunicación como chats, sistemas colaborativos y plataformas de mensajería.

🧰 Tecnologías Utilizadas

Java

Java Swing (interfaz gráfica)

Sockets TCP

Multithreading

Arquitectura Cliente-Servidor

NetBeans

🏗️ Arquitectura del Sistema

El sistema sigue un modelo Cliente - Servidor.

Servidor

El servidor es el núcleo del sistema y tiene las siguientes responsabilidades:

Escuchar conexiones entrantes de clientes

Crear un hilo independiente por cada cliente

Recibir mensajes enviados por los clientes

Reenviar los mensajes a los demás usuarios conectados

Cada cliente conectado es gestionado por una clase:

ClientHandler

lo que permite que múltiples usuarios puedan comunicarse simultáneamente.

Cliente

El cliente proporciona una interfaz gráfica que permite al usuario interactuar con el sistema.

Funciones principales:

Conectarse al servidor

Enviar mensajes

Recibir mensajes de otros usuarios

Mostrar la conversación en tiempo real

La interfaz se encuentra implementada en:

ClientGUI.java
⚙️ Componentes Principales
Server.java

Clase principal del servidor encargada de:

Crear el socket del servidor

Escuchar conexiones

Crear manejadores de cliente

ClientHandler

Clase que maneja cada cliente conectado mediante hilos, permitiendo la comunicación simultánea entre múltiples usuarios.

Client.java

Implementa la lógica de conexión del cliente con el servidor.

Se encarga de:

Abrir el socket hacia el servidor

Enviar mensajes

Escuchar respuestas del servidor

ClientGUI.java

Interfaz gráfica que permite al usuario interactuar con el sistema de chat.

Incluye:

Campo para escribir mensajes

Área de visualización de conversación

Controles de conexión

DBConfig.java

Archivo encargado de gestionar la configuración de la conexión con base de datos (si se utiliza para almacenamiento de información).

📂 Estructura del Proyecto

<img width="332" height="392" alt="image" src="https://github.com/user-attachments/assets/d5c2daaf-5e16-4697-bc4c-7186e4c44f27" />


▶️ Cómo Ejecutar el Proyecto
1️⃣ Compilar el proyecto

Puedes usar el script incluido:

compile.bat

o compilar desde NetBeans.

2️⃣ Iniciar el servidor

Ejecutar:

run_server.bat

Esto iniciará el servidor que escuchará conexiones de clientes.

3️⃣ Iniciar el cliente

Ejecutar:

run_client.bat

Se abrirá la interfaz gráfica del chat.

4️⃣ Conectar múltiples clientes

Puedes abrir varios clientes para simular múltiples usuarios conectados al mismo servidor.

🚀 Características del Sistema

✔ Comunicación en tiempo real
✔ Soporte para múltiples clientes
✔ Arquitectura cliente-servidor
✔ Interfaz gráfica amigable
✔ Uso de multihilos para concurrencia

👨‍💻 Autor

Heber Gabriel Mancilla López

Estudiante de Ingeniería en Informática con interés en:

Desarrollo de software

Sistemas distribuidos

Análisis de datos
