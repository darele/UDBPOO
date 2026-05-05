package materiales;

import conexion.Conexion;
import gui.GUI;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class Material {
    protected final String codigo, titulo;
    public Material(String codigo, String titulo) {
        this.codigo = codigo;
        this.titulo = titulo;
    }

    protected static boolean validarDatos(String codigo, String titulo, List<String> problems) {
        boolean ans = true;
        if (codigo.isEmpty()) {
            problems.add("Codigo no puede estar vacio");
            ans = false;
        } else if (codigo.length() != 8) {
            problems.add("Se espera un codigo de 8 caracteres");
            ans = false;
        } else {
            try {
                Integer.parseInt(codigo.substring(3));
            } catch (NumberFormatException ignored) {
                problems.add("Los ultimos 5 caracteres del codigo deben ser numericos");
                ans = false;
            }
        }
        if (titulo.trim().isEmpty()) {
            problems.add("Titulo no puede estar vacio");
            ans = false;
        }
        return ans;
    }

    public void writeSelfToDB(Conexion conexion) {
        conexion.ejecutarInstruccionNoResult(
                "INSERT INTO material(idMaterial, titulo) " +
                        "VALUES (\"" + codigo + "\",\"" + titulo + "\");"
        );
    }

    public static boolean hayUnidades(String codigo, Conexion conexion) {
        int unidades = 0;
        ResultSet result;
        result = conexion.ejecutarInstruccion("SELECT numeroUnidades FROM unidad WHERE idMaterial = \"" + codigo + "\";");
        try {
            if (!result.next()) {
                unidades = 0;
            } else {
                unidades = result.getInt(1);
            }
        } catch (SQLException e) {
            GUI.logger.error("Error de acceso:", e);
        }
        if (unidades > 0) {
            return true;
        }
        return false;
    }

    public static boolean estaEnBD(String codigo, Conexion conexion) {
        ResultSet result;
        result = conexion.ejecutarInstruccion("SELECT * FROM material WHERE idMaterial = \"" + codigo + "\";");
        try {
            return result.next();
        } catch (SQLException e) {
            GUI.logger.error("Error de acceso", e);
            return false;
        }
    }
    
    public void updateSelfToDB(Conexion conexion) {
        conexion.ejecutarInstruccionNoResult(
                "UPDATE material " +
                "SET titulo='" + titulo + "';"
        );
    }
}
