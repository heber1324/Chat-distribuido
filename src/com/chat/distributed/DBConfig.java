package com.chat.distributed;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * DBConfig: carga db.properties (desde classpath o archivo local)
 * y fuerza la carga del driver MySQL.
 */
public class DBConfig {
    private static final Properties props = new Properties();
    public static final String url;
    public static final String user;
    public static final String password;

    static {
        // Intentar cargar db.properties desde el classpath (ej. src/resources/db.properties)
        try (InputStream in = DBConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                System.out.println("db.properties cargado desde classpath.");
            } else {
                // Fallback: intentar leer un archivo db.properties en la raíz del proyecto
                try (InputStream fis = new FileInputStream("db.properties")) {
                    props.load(fis);
                    System.out.println("db.properties cargado desde archivo local.");
                } catch (Exception ex) {
                    System.err.println("No se pudo cargar db.properties: " + ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al leer db.properties: " + e.getMessage());
        }

        url = props.getProperty(
                "db.url",
                "jdbc:mysql://localhost:3306/chat_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
        );
        user = props.getProperty("db.user", "chatuser");
        password = props.getProperty("db.password", "chatpass");

        // Forzar carga del driver JDBC de MySQL (Connector/J 8/9)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL cargado correctamente.");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL no encontrado en classpath: " + e.getMessage());
        }
    }

    private DBConfig() {
        // evitar instancias
    }
}
