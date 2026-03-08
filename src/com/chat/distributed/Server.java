package com.chat.distributed;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

/**

* Server multicliente. Reenvía mensajes y guarda en MySQL.
  */
  public class Server {
  public static final int DEFAULT_PORT = 8080;
  private final ServerSocket serverSocket;
  private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

  public Server(int port) throws IOException {
  serverSocket = new ServerSocket();
  serverSocket.bind(new InetSocketAddress("0.0.0.0", port));
  System.out.println("Servidor escuchando en 0.0.0.0:" + port);
  }

  public void start() {
  while (!Thread.currentThread().isInterrupted()) {
  try {
  Socket socket = serverSocket.accept();
  ClientHandler h = new ClientHandler(socket);
  clients.add(h);
  new Thread(h).start();
  } catch (IOException e) {
  System.err.println("Error aceptando conexión: " + e.getMessage());
  }
  }
  }

  public void broadcast(String msg, ClientHandler exclude) {
    for (ClientHandler c : clients) {
        c.send(msg); // siempre a todos
  }
  }


  public void remove(ClientHandler c) {
  clients.remove(c);
  }

  private static void saveInDB(String sender, String message) {
  String sql = "INSERT INTO messages (sender, message) VALUES (?, ?)";
  try (Connection conn = java.sql.DriverManager.getConnection(DBConfig.url, DBConfig.user, DBConfig.password);
  PreparedStatement ps = conn.prepareStatement(sql)) {
  ps.setString(1, sender);
  ps.setString(2, message);
  ps.executeUpdate();
  } catch (SQLException e) {
  System.err.println("Error al guardar mensaje en BD: " + e.getMessage());
  }
  }

  public static void main(String[] args) {
  int port = DEFAULT_PORT;
  if (args.length >= 1) {
  try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
  }
  try {
  Server s = new Server(port);
  s.start();
  } catch (IOException e) {
  System.err.println("No se pudo iniciar servidor: " + e.getMessage());
  }
  }

  // --------- ClientHandler ----------
  private class ClientHandler implements Runnable {
  private final Socket socket;
  private String name = "UNKNOWN";
  private BufferedReader in;
  private BufferedWriter out;

 
   ClientHandler(Socket socket) {
       this.socket = socket;
   }

   void send(String msg) {
       try {
           out.write(msg);
           out.write("\n");
           out.flush();
       } catch (IOException e) {
           System.err.println("Error enviando a " + name + ": " + e.getMessage());
       }
   }

   @Override
   public void run() {
       try {
           in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
           out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

           String line = in.readLine();
           if (line != null && !line.trim().isEmpty()) name = line.trim();
           System.out.println("Conexión entrante: " + name + " desde " + socket.getRemoteSocketAddress());

           broadcast("[sistema] " + name + " se ha conectado.", this);

           String msg;
           while ((msg = in.readLine()) != null) {
               if (msg.trim().isEmpty()) continue;
               System.out.println("Recibido de " + name + ": " + msg);

               // persistir en BD
               saveInDB(name, msg);

               String outMsg = String.format("[%1$tF %1$tT] %2$s: %3$s", new Date(), name, msg);
               broadcast(outMsg, null); // incluir también al emisor

           }
       } catch (IOException e) {
           System.err.println("I/O error con cliente " + name + ": " + e.getMessage());
       } finally {
           try { socket.close(); } catch (IOException ignored) {}
           remove(this);
           broadcast("[sistema] " + name + " se ha desconectado.", this);
           System.out.println("Cliente desconectado: " + name);
       }
   }
  

  }
  }
