/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Usuario;

/**
 *
 * @author pdarw
 */


import conexion.Conexion;
import java.util.List;
import java.util.Map;
import utils.Init;

public class Usuario {
    private static final List<String> campos = List.of(
            "nombre",
            "rol",
            "username",
            "password"
    );

    private static final List<String> posiblesRoles = List.of(
            "administrador",
            "estudiante",
            "profesor"
    );

    private int carnet = 0;
    private final String nombre, rol, username, password;

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }

    public String getUsername() {
        return username;
    }
    private static final Conexion conexion = Init.conexion;

    public Usuario(String[] input) {
        this.nombre = input[0];
        this.rol = input[1];
        this.username = input[2];
        this.password = Init.encriptar(input[3]);
    }
    
    public static boolean estaEnBD(String username) {
        Map<String, String> ans = conexion.select(
                "usuarios", 
                "WHERE username='" + username + "'",
                List.of("username")
            );
        return ans.containsKey("username");
    }

    public static List<String> getCampos() {
        return campos;
    }

    public static boolean validarDatos(String[] input, List<String> problems, Conexion conexion) {
        boolean ans = true;
        String nombre = input[0].trim();
        String rol = input[1].trim();
        String username = input[2].trim();
        String contrasena = input[3].trim();
        
        if (nombre.isEmpty()) {
            ans = false;
            problems.add("El campo nombre no debe estar vacio");
        }
        boolean rolPosible = false;
        for (String posRol : posiblesRoles) {
            if (rol.equals(posRol)) {
                rolPosible = true;
            }
        }
        if (!rolPosible) {
            ans = false;
            problems.add("El rol puede ser unicamente administrador, estudiante o profesor");
        }
        if (username.isEmpty()) {
            ans = false;
            problems.add("El nombre de usuario no puede estar vacio");
        }
        if (contrasena.isEmpty()) {
            ans = false;
            problems.add("La contrasena no puede estar vacia");
        }
        return ans;
    }

    public void writeSelftoDB(Conexion conexion) {
        conexion.ejecutarInstruccionNoResult("INSERT INTO usuarios(" +
                "nombre, rol, username, password) VALUES" +
                "('" + nombre + "', '" + rol +
                "', '" + username + "', " +
                "'" + password + "');"
        );
        Map<String, String> ans = conexion.select("usuarios", "WHERE username = '" + username + "'", List.of("carnet"));
        carnet = Integer.parseInt(ans.get("carnet"));
    }
    
    public void updateSelftoDB(Conexion conexion) {
        conexion.ejecutarInstruccionNoResult(
                "UPDATE usuarios "
                + "SET nombre = '" + nombre + "', "
                + "rol='" + rol + "', "
                + "username='" + username + "', "
                + "password='" + password + "'"
                + " WHERE username = '" + username + "';"
        );
    }
    
    public void deleteSelfFromDB(Conexion conexion) {
        conexion.ejecutarInstruccionNoResult("DELETE FROM usuarios WHERE username = '" + username + "';");
    }
}

