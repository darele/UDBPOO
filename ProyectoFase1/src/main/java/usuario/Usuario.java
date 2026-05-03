package usuario;

import conexion.Conexion;
import gui.GUI;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class Usuario {
    private static final List<String> campos = List.of(
            "carnet",
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

    private final String carnet, nombre, rol, username, password;

    public Usuario(JTextField[] input) {
        this.carnet = input[0].getText();
        this.nombre = input[1].getText();
        this.rol = input[2].getText();
        this.username = input[3].getText();
        this.password = GUI.encriptar(input[4].getText());
    }
    
    public static boolean estaEnBD(String carnet, Conexion conexion) {
        Map<String, String> ans = conexion.select(
                "usuarios", 
                "WHERE carnet='" + carnet + "'",
                List.of("carnet")
            );
        return ans.containsKey("carnet");
    }

    public static List<String> getCampos() {
        return campos;
    }

    public static boolean validarDatos(JTextField[] input, List<String> problems, Conexion conexion, boolean strict) {
        boolean ans = true;
        String codigo = input[0].getText().trim();
        String nombre = input[1].getText().trim();
        String rol = input[2].getText().trim();
        String username = input[3].getText().trim();
        String contrasena = input[4].getText().trim();
        if (codigo.isEmpty()) {
            problems.add("El carnet no puede estar vacío");
            ans = false;
        } else if (codigo.length() != 8) {
            problems.add("El carnet debe tener 8 caracteres");
            ans = false;
        } else {
            try {
                Integer.parseInt(codigo.substring(2));
            } catch (NumberFormatException _) {
                problems.add("El carnet debe tener los ultimos 6 caracteres numericos");
                ans = false;
            }
            if (strict) {
                Map<String, String> ansSelect = conexion.select("usuarios",
                    "WHERE carnet='" + codigo + "'",
                    List.of("carnet"));
                if (ansSelect.containsKey("carnet")) {
                    ans = false;
                    problems.add("Carnet ya ingresado");
                }
            }
        }
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
        } else {
            Map<String, String> ansSelect = conexion.select("usuarios",
                    "WHERE username='" + username + "'",
                    List.of("username"));
            if (ansSelect.containsKey("username")) {
                ans = false;
                problems.add("nombre de usuario ocupado");
            }
        }
        if (contrasena.isEmpty()) {
            ans = false;
            problems.add("La contrasena no puede estar vacia");
        }
        return ans;
    }

    public void writeSelftoDB(Conexion conexion) {
        conexion.ejecutarInstruccionNoResult("INSERT INTO usuarios(" +
                "carnet, nombre, rol, username, password) VALUES" +
                "('" + carnet + "', '" + nombre + "', '" + rol +
                "', '" + username + "', " +
                "'" + password + "');"
        );
    }
    
    public void updateSelftoDB(Conexion conexion) {
        conexion.ejecutarInstruccionNoResult(
                "UPDATE usuarios "
                + "SET nombre = '" + nombre + "', "
                + "rol='" + rol + "', "
                + "username='" + username + "', "
                + "password='" + password + "'"
                + " WHERE carnet = '" + carnet + "';"
        );
    }
}
