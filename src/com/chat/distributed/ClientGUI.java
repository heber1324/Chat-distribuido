package com.chat.distributed;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**

* ClientGUI completo y robusto.
* Constructor: ClientGUI(String serverIp, int serverPort, String presetName)
* Si presetName no es nulo, intenta conectar automáticamente con ese nombre.
  */
  public class ClientGUI extends JFrame {
  private static final long serialVersionUID = 1L;

  // Componentes GUI como campos de instancia
  private final JTextField nameField;
  private final JButton connectButton;
  private final JButton clearButton;
  private final JTextArea chatArea;
  private final JTextField inputField;
  private final JButton sendButton;

  // Networking
  private Socket socket;
  private BufferedReader in;
  private BufferedWriter out;
  private volatile boolean connected = false;
  private Thread readerThread;

  // configuración
  private final String serverIp;
  private final int serverPort;
  private final String presetName;

  public ClientGUI(String serverIp, int serverPort, String presetName) {
  super("Cliente");
  this.serverIp = serverIp == null ? "127.0.0.1" : serverIp;
  this.serverPort = serverPort <= 0 ? 8080 : serverPort;
  this.presetName = presetName;


   // init look and feel (Nimbus si está)
   initLookAndFeel();

   // init components
   nameField = new JTextField(18);
   connectButton = new JButton("Conectar");
   clearButton = new JButton("Limpiar");
   chatArea = new JTextArea();
   inputField = new JTextField();
   sendButton = new JButton("Enviar");

   chatArea.setEditable(false);
   chatArea.setLineWrap(true);
   chatArea.setWrapStyleWord(true);
   chatArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

   sendButton.setEnabled(false);
   inputField.setEnabled(false);

   layoutComponents();
   attachListeners();

   setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
   setSize(620, 480);
   setLocationRelativeTo(null);
   setVisible(true);

   // si se proporcionó presetName, completar el campo y conectar
   if (presetName != null && !presetName.trim().isEmpty()) {
       nameField.setText(presetName.trim());
       // conectar en hilo para no bloquear EDT
       SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               connect();
           }
       });
   }


  }

  private void initLookAndFeel() {
  try {
  for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
  if ("Nimbus".equals(info.getName())) {
  UIManager.setLookAndFeel(info.getClassName());
  break;
  }
  }
  } catch (Exception ignored) { }
  }

  private void layoutComponents() {
  JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
  top.add(new JLabel("Digite su nombre:"));
  top.add(nameField);
  top.add(connectButton);
  top.add(clearButton);


   JScrollPane center = new JScrollPane(chatArea);
   center.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(6,6,6,6), center.getBorder()));

   JPanel bottom = new JPanel(new BorderLayout(8,8));
   bottom.setBorder(new EmptyBorder(6,6,6,6));
   bottom.add(inputField, BorderLayout.CENTER);
   bottom.add(sendButton, BorderLayout.EAST);

   getContentPane().setLayout(new BorderLayout(6,6));
   getContentPane().add(top, BorderLayout.NORTH);
   getContentPane().add(center, BorderLayout.CENTER);
   getContentPane().add(bottom, BorderLayout.SOUTH);
 

  }

  private void attachListeners() {
  // Conectar / Desconectar
  connectButton.addActionListener(new java.awt.event.ActionListener() {
  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
  if (!connected) {
  connect();
  } else {
  disconnect();
  }
  }
  });


   // Limpiar chat
   clearButton.addActionListener(new java.awt.event.ActionListener() {
       @Override
       public void actionPerformed(java.awt.event.ActionEvent e) {
           chatArea.setText("");
       }
   });

   // Envío desde botón o Enter en inputField
   sendButton.addActionListener(new java.awt.event.ActionListener() {
       @Override
       public void actionPerformed(java.awt.event.ActionEvent e) {
           sendMessage();
       }
   });
   inputField.addActionListener(new java.awt.event.ActionListener() {
       @Override
       public void actionPerformed(java.awt.event.ActionEvent e) {
           sendMessage();
       }
   });

   // Enter en nameField intenta conectar
   nameField.addActionListener(new java.awt.event.ActionListener() {
       @Override
       public void actionPerformed(java.awt.event.ActionEvent e) {
           connectButton.doClick();
       }
   });

   // Cerrar ventana -> desconectar limpiamente
   addWindowListener(new java.awt.event.WindowAdapter() {
       @Override
       public void windowClosing(java.awt.event.WindowEvent e) {
           disconnect();
       }
   });


  }

  private synchronized void connect() {
  if (connected) return;
  final String name = nameField.getText().trim();
  if (name.isEmpty()) {
  JOptionPane.showMessageDialog(this, "Ingrese su nombre antes de conectar.", "Nombre requerido", JOptionPane.WARNING_MESSAGE);
  return;
  }


   connectButton.setEnabled(false);
   // realizar conexión en hilo para no bloquear EDT
   new Thread(new Runnable() {
       @Override
       public void run() {
           try {
               socket = new Socket();
               socket.connect(new InetSocketAddress(serverIp, serverPort), 5000);
               in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
               out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

               // enviar nombre como primera línea
               out.write(name);
               out.write("\n");
               out.flush();

               connected = true;
               SwingUtilities.invokeLater(new Runnable() {
                   @Override
                   public void run() {
                       connectButton.setText("Desconectar");
                       nameField.setEnabled(false);
                       sendButton.setEnabled(true);
                       inputField.setEnabled(true);
                       chatArea.append("[sistema] Conectado a " + serverIp + ":" + serverPort + " como " + name + "\n");
                   }
               });

               // iniciar lector
               readerThread = new Thread(new Runnable() {
                   @Override
                   public void run() {
                       try {
                           String line;
                           while (connected && (line = in.readLine()) != null) {
                               final String msg = line;
                               SwingUtilities.invokeLater(new Runnable() {
                                   @Override
                                   public void run() {
                                       chatArea.append(msg + "\n");
                                       chatArea.setCaretPosition(chatArea.getDocument().getLength());
                                   }
                               });
                           }
                       } catch (IOException ex) {
                           if (connected) {
                               SwingUtilities.invokeLater(new Runnable() {
                                   @Override
                                   public void run() {
                                       chatArea.append("[sistema] Error de conexión: " + ex.getMessage() + "\n");
                                   }
                               });
                           }
                       } finally {
                           // asegurar desconexión
                           SwingUtilities.invokeLater(new Runnable() {
                               @Override
                               public void run() {
                                   disconnect();
                               }
                           });
                       }
                   }
               }, "Client-Reader");
               readerThread.setDaemon(true);
               readerThread.start();

           } catch (IOException ex) {
               SwingUtilities.invokeLater(new Runnable() {
                   @Override
                   public void run() {
                       JOptionPane.showMessageDialog(ClientGUI.this, "No se pudo conectar: " + ex.getMessage(), "Error conexión", JOptionPane.ERROR_MESSAGE);
                       connectButton.setEnabled(true);
                   }
               });
               cleanupSocketResources();
           }
       }
   }, "Client-Connector").start();
 
  }

  private synchronized void disconnect() {
  connected = false;
  try { if (out != null) out.close(); } catch (IOException ignored) {}
  try { if (in != null) in.close(); } catch (IOException ignored) {}
  try { if (socket != null && !socket.isClosed()) socket.close(); } catch (IOException ignored) {}
  out = null;
  in = null;
  socket = null;
  SwingUtilities.invokeLater(new Runnable() {
  @Override
  public void run() {
  sendButton.setEnabled(false);
  inputField.setEnabled(false);
  connectButton.setText("Conectar");
  nameField.setEnabled(true);
  chatArea.append("[sistema] Desconectado.\n");
  connectButton.setEnabled(true);
  }
  });
  }

  private void cleanupSocketResources() {
  try { if (socket != null) socket.close(); } catch (IOException ignored) {}
  try { if (in != null) in.close(); } catch (IOException ignored) {}
  try { if (out != null) out.close(); } catch (IOException ignored) {}
  out = null;
  in = null;
  socket = null;
  }

  private synchronized void sendMessage() {
  if (!connected || out == null) {
  JOptionPane.showMessageDialog(this, "No estás conectado al servidor.", "No conectado", JOptionPane.WARNING_MESSAGE);
  return;
  }
  String text = inputField.getText();
  if (text == null) text = "";
  text = text.trim();
  if (text.isEmpty()) return;


   try {
       out.write(text);
       out.write("\n");
       out.flush();
       inputField.setText("");
   } catch (IOException ex) {
       SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               chatArea.append("[sistema] Error enviando mensaje: " + ex.getMessage() + "\n");
           }
       });
       disconnect();
   }


  }

  // main alternativo para ejecutar directamente
  public static void main(String[] args) {
  String server = null;
  int port = 8080;
  String name = "";

 
   if (args.length >= 3) {
       server = args[0];
       try { port = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
       name = args[2];
   } else {
       server = JOptionPane.showInputDialog(null, "IP del servidor:", "127.0.0.1");
       String p = JOptionPane.showInputDialog(null, "Puerto (por defecto 8080):", "8080");
       if (p != null && !p.trim().isEmpty()) {
           try { port = Integer.parseInt(p.trim()); } catch (NumberFormatException ignored) {}
       }
       name = JOptionPane.showInputDialog(null, "Nombre (opcional):");
   }

   final String s = server;
   final int pr = port;
   final String n = name;
   SwingUtilities.invokeLater(new Runnable() {
       @Override
       public void run() {
           new ClientGUI(s, pr, n);
       }
   });
 

  }
  }

