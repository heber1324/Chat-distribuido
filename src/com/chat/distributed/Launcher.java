package com.chat.distributed;

import javax.swing.*;
import java.awt.*;

/**
 * Launcher corregido: evita el error de "local variables ... must be final".
 */
public class Launcher extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JButton startBtn;
    private final JButton openBtn;
    private final JTextField portField;

    public Launcher() {
        super("Launcher - ChatDistributed");
        startBtn = new JButton("Iniciar servidor (background)");
        openBtn = new JButton("Abrir cliente");
        portField = new JTextField("8080", 6);

        createLayout();
        attachListeners();

        setSize(460, 140);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createLayout() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.add(new JLabel("Puerto:"));
        panel.add(portField);
        panel.add(startBtn);
        panel.add(openBtn);
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void attachListeners() {
        // Iniciar servidor
        startBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int port;
                try {
                    port = Integer.parseInt(portField.getText().trim());
                } catch (NumberFormatException ex) {
                    port = 12345;
                }

                // Hacerlo final para poder usarlo dentro de la clase anónima del Runnable
                final int runPort = port;

                Thread serverThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Server.main(new String[]{ String.valueOf(runPort) });
                    }
                }, "ServerThread");
                serverThread.setDaemon(true);
                serverThread.start();

                JOptionPane.showMessageDialog(Launcher.this, "Servidor iniciado en puerto " + runPort);
            }
        });

        // Abrir cliente
        openBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String ip = JOptionPane.showInputDialog(Launcher.this, "IP del servidor:", "127.0.0.1");
                if (ip == null || ip.trim().isEmpty()) return;

                String p = JOptionPane.showInputDialog(Launcher.this, "Puerto:", portField.getText().trim());
                if (p == null || p.trim().isEmpty()) return;

                int tmpPort;
                try {
                    tmpPort = Integer.parseInt(p.trim());
                } catch (NumberFormatException ex) {
                    tmpPort = 12345;
                }

                String name = JOptionPane.showInputDialog(Launcher.this, "Nombre del cliente:");
                if (name == null || name.trim().isEmpty()) return;

                final String serverIp   = ip.trim();
                final int    serverPort = tmpPort;   // final para usar en la clase anónima
                final String clientName = name.trim();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new ClientGUI(serverIp, serverPort, clientName);
                    }
                });
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Launcher();
            }
        });
    }
}
