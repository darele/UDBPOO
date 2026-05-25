/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import conexion.Conexion;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pdarw
 */
public class Init {
    public static final Logger logger = Logger.getLogger("MyJSPLogger");
    public static final Conexion conexion = new Conexion();
    
    private static final String salt = "Rafael Torres ";
    public static String encriptar(String contrasena) {
        byte[] contrasenaCifradaBytes = null;
        try {
            contrasenaCifradaBytes = MessageDigest.getInstance("SHA-256").digest(
                    (salt + contrasena).getBytes(StandardCharsets.UTF_8)
            );
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.WARNING, "Error al encriptar la contrasena ", e);
            return "";
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : contrasenaCifradaBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static Map<String, String> validarUserPassword(String username, String password) {
        Map<String, String> usuario = conexion.select("usuarios",
                "WHERE username='" + username + "'",
                List.of("username", "carnet", "password", "rol"));
        Map<String, String> ans = new HashMap<>();
        if (usuario.isEmpty()) {
            ans.put("errores1", "Usuario o contrasena incorrectos");
            return ans;
        }
        if (usuario.containsKey("errores")) {
            ans.put("errores1", usuario.get("errores"));
            return ans;
        }

        String contrasena = password;
        String contrasenaCifrada = encriptar(contrasena);
        String contrasenaReal = usuario.get("password");
        if (contrasenaCifrada.equals(contrasenaReal)) {
            ans = usuario;
            return ans;
        }
        ans.put("errores1", "Usuario o contrasena incorrectos");
        return ans;
    }
    
    public static void initDB() {
        conexion.ejecutarInstruccionNoResult(
                "CREATE DATABASE IF NOT EXISTS biblioteca_amigos;"
        );
        conexion.ejecutarInstruccionNoResult("use biblioteca_amigos;");
        conexion.ejecutarInstruccionNoResult("CREATE TABLE IF NOT EXISTS usuarios(" +
                "carnet INT PRIMARY KEY AUTO_INCREMENT, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "rol ENUM('administrador', 'estudiante', 'profesor') " +
                "NOT NULL DEFAULT 'estudiante', " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(64) NOT NULL" +
                ");"
        );
        Map<String, String> ans = conexion.select("usuarios", "WHERE username='admin'", List.of("username"));
        // Esto en la vida real no lo hariamos, solo es para que le compile
        // al momento de revisarlo
        // usuario admin
        // contrasena 12345
        if (!ans.containsKey("username")) {
            conexion.ejecutarInstruccionNoResult("INSERT INTO usuarios(" +
                    "nombre, rol, username, password) VALUES" +
                    "('administrador', 'administrador', 'admin', " +
                    "'9245169e32b59d483d6486b0eb9eb7abb601764f5bad92eb97bbb0fa88f0529c');"
            );
        }
        //usuario = estudiante1
        //contrasena = 12345
        ans = conexion.select("usuarios", "WHERE username='estudiante1'", List.of("username"));
        if (!ans.containsKey("username")) {
            conexion.ejecutarInstruccionNoResult("INSERT INTO usuarios(" +
                    "nombre, rol, username, password) VALUES" +
                    "('Josue Gomez', 'estudiante', 'estudiante1', " +
                    "'9245169e32b59d483d6486b0eb9eb7abb601764f5bad92eb97bbb0fa88f0529c');"
            );
        }
        //usuario = estudiante1
        //contrasena = 12345
        ans = conexion.select("usuarios", "WHERE username='profesor1'", List.of("username"));
        if (!ans.containsKey("username")) {
            conexion.ejecutarInstruccionNoResult("INSERT INTO usuarios(" +
                    "nombre, rol, username, password) VALUES" +
                    "('Orlando Avalos', 'profesor', 'profesor1', " +
                    "'9245169e32b59d483d6486b0eb9eb7abb601764f5bad92eb97bbb0fa88f0529c');"
            );
        }

        conexion.ejecutarInstruccionNoResult("CREATE TABLE IF NOT EXISTS prestamo(" +
                "idPrestamo INT PRIMARY KEY, " +
                "fechaPrestamo DATE NOT NULL DEFAULT (CURDATE()), " +
                "fechaDevolucion DATE DEFAULT NULL, " +
                "estado ENUM('prestado', 'devuelto') NOT NULL DEFAULT 'prestado', " +
                "carnet VARCHAR(8) REFERENCES usuarios(carnet), " +
                "idMaterial VARCHAR(8) REFERENCES material(idMaterial)" +
                ");"
        );

        conexion.ejecutarInstruccionNoResult(
                "CREATE TABLE IF NOT EXISTS material(" +
                        "idMaterial VARCHAR(8) PRIMARY KEY," +
                        "titulo VARCHAR(45) NOT NULL,"
                        + "numero_clasificacion INT NOT NULL,"
                        + "codigo_ubicacion INT NOT NULL" +
                        ");"
        );
        
        conexion.ejecutarInstruccionNoResult(
                "CREATE TABLE IF NOT EXISTS unidad(" +
                        "numeroUnidades INT," +
                        "idMaterial VARCHAR(8) REFERENCES material(idMaterial)" +
                        ");"
        );
        conexion.ejecutarInstruccionNoResult(
                "CREATE TABLE IF NOT EXISTS libro(" +
                        "autor VARCHAR(45) NOT NULL," +
                        "numPaginas INT," +
                        "editorial VARCHAR(45) NOT NULL," +
                        "isbn VARCHAR(13) NOT NULL," +
                        "anoPublicacion INT," +
                        "idMaterial VARCHAR(8) REFERENCES material(idMaterial)" +
                        ");"
        );
        conexion.ejecutarInstruccionNoResult(
                "CREATE TABLE IF NOT EXISTS revista(" +
                        "editorial VARCHAR(45) NOT NULL," +
                        "periodicidad INT," +
                        "fechaPublicacion DATE," +
                        "idMaterial VARCHAR(8) REFERENCES material(idMaterial)" +
                        ");"
        );
        conexion.ejecutarInstruccionNoResult(
                "CREATE TABLE IF NOT EXISTS cd(" +
                        "artista VARCHAR(45) NOT NULL," +
                        "genero VARCHAR(45) NOT NULL," +
                        "duracion INT," +
                        "numeroCanciones INT," +
                        "idMaterial VARCHAR(8) REFERENCES material(idMaterial)" +
                        ");"
        );
        conexion.ejecutarInstruccionNoResult(
                "CREATE TABLE IF NOT EXISTS dvd(" +
                        "director VARCHAR(45) NOT NULL," +
                        "duracion INT," +
                        "genero VARCHAR(45)," +
                        "idMaterial VARCHAR(8) REFERENCES material(idMaterial)" +
                        ");"
        );
        conexion.ejecutarInstruccionNoResult(
                "CREATE TABLE IF NOT EXISTS tesis(" +
                        "autor VARCHAR(45) NOT NULL," +
                        "carrera VARCHAR(45) NOT NULL," +
                        "ano_publicacion INT," +
                        "idMaterial VARCHAR(8) REFERENCES material(idMaterial)" +
                        ");"
        );
    }
}
