package materiales;

import conexion.Conexion;
import gui.GUI;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class Material {
    protected static final List<String> campos = List.of(
            "Código", "Título", "Numero de clasificacion", "Codigo de ubicacion"
    );
    protected final String codigo, titulo;
    protected final int numero_clasificacion, codigo_ubicacion;
    
    protected static List<String> getCampos() {
           return campos;
    }
    
    public Material(String codigo, String titulo, int numero_clasificacion, int codigo_ubicacion) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.numero_clasificacion = numero_clasificacion;
        this.codigo_ubicacion = codigo_ubicacion;
    }

    protected static boolean validarDatos(String codigo, String titulo, 
            String numero_clasificacion, String codigo_ubicacion, List<String> problems) {
        boolean ans = true;
        if (codigo.isEmpty()) {
            problems.add("Codigo no puede estar vacio");
            ans = false;
        } else if (codigo.length() != 8) {
            problems.add("Se espera un codigo de 8 caracteres");
            ans = false;
        } else {
            try {
                Integer.valueOf(codigo.substring(3));
            } catch (NumberFormatException ignored) {
                problems.add("Los ultimos 5 caracteres del codigo deben ser numericos");
                ans = false;
            }
        }
        if (titulo.trim().isEmpty()) {
            problems.add("Titulo no puede estar vacio");
            ans = false;
        }
        
        if (numero_clasificacion.isEmpty()) {
            ans = false;
            problems.add("El campo numero de clasificacion no puede estar vacio");
        } else {
            try {
                Integer.valueOf(numero_clasificacion);
            } catch (NumberFormatException ignored) {
                ans = false;
                problems.add("El campo Numero de clasificacion espera únicamente dígitos");
            }
        }
        
        if (codigo_ubicacion.isEmpty()) {
            ans = false;
            problems.add("El campo codigo de ubicacion no puede estar vacio");
        } else {
            try {
                Integer.valueOf(codigo_ubicacion);
            } catch (NumberFormatException ignored) {
                ans = false;
                problems.add("El campo Codigo de ubicacion espera únicamente dígitos");
            }
        }
        
        return ans;
    }

    public boolean writeSelfToDB(Conexion conexion) {
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
            GUI.logger.error("Error de acceso", e);
        }
        if (unidades > 0) {
            conexion.ejecutarInstruccionNoResult("UPDATE unidad SET "
                    + "numeroUnidades=" + (unidades + 1)
                    + " WHERE idMaterial=\"" + codigo + "\"");
            return false;
        }
        
        conexion.ejecutarInstruccionNoResult(
                "INSERT INTO unidad(numeroUnidades, idMaterial) "
                + "VALUES (" + (unidades + 1) + ",\"" + codigo + "\");"
        );
        
        conexion.ejecutarInstruccionNoResult(
                "INSERT INTO material(idMaterial, titulo, numero_clasificacion, codigo_ubicacion) " +
                        "VALUES (\"" + codigo + "\",\"" + titulo + "\", " 
                        + numero_clasificacion + ", " + codigo_ubicacion +");"
        );
        return true;
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
        return unidades > 0;
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
                "SET titulo='" + titulo + "',"
                + "numero_clasificacion=" + numero_clasificacion + ","
                + "codigo_ubicacion=" + codigo_ubicacion + " "
                + "WHERE idMaterial='" + codigo + "';"
        );
    }
}
