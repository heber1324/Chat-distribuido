package com.chat.distributed;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Cliente de consola.
 * Uso: java com.chat.distributed.Client <server-ip> <port> <name>
 */
public class Client {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java com.chat.distributed.Client <server-ip> <port> <name>");
            return;
        }
        String serverIp = args[0];
        int port = Integer.parseInt(args[1]);
        String name = args[2];

        try (Socket socket = new Socket(serverIp, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            // Enviar nombre
            out.write(name);
            out.write("\n");
            out.flush();

            // Thread que escucha mensajes del servidor
            Thread reader = new Thread(() -> {
                try {
                    String s;
                    while ((s = in.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (IOException e) {
                    System.err.println("Error leyendo del servidor: " + e.getMessage());
                }
            });
            reader.setDaemon(true);
            reader.start();

            // Leer desde consola y enviar
            Scanner sc = new Scanner(System.in, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line == null) break;
                out.write(line);
                out.write("\n");
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
