package materiales;

import conexion.Conexion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import utils.Init;

public abstract class Material {
    protected static final List<String> campos = List.of(
            "Titulo", 
             "Numero de clasificacion", 
             "Codigo de ubicacion"
    );
    protected static final List<TipoCampo> tipoCampos = List.of(
            TipoCampo.TEXTO,
            TipoCampo.TEXTO,
            TipoCampo.TEXTO
    );
    
    protected static final List<String> dbFields = List.of(
            "titulo",
            "numero_clasificacion",
            "codigo_ubicacion"
    );
    
    
    protected final String titulo, prefijo;
    protected final int numero_clasificacion, codigo_ubicacion;
    protected int codigo;
    
    protected static List<String> getCampos() {
        return campos;
    }
    
    protected static List<TipoCampo> getTipoCampos() {
        return tipoCampos;
    }
    
    public Material(String titulo, int numero_clasificacion, int codigo_ubicacion, String prefijo) {
        this.titulo = titulo;
        this.numero_clasificacion = numero_clasificacion;
        this.codigo_ubicacion = codigo_ubicacion;
        this.prefijo = prefijo;
    }

    protected static boolean validarDatos(String titulo, 
            String numero_clasificacion, String codigo_ubicacion, List<String> problems) {
        boolean ans = true;
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
        if (estaEnBD(titulo, conexion)) {
            Map<String, String> ans = conexion.select("material", 
                    "WHERE titulo='" + titulo + "'", List.of("idMaterial"));
            codigo = Integer.parseInt(ans.get("codigo"));
            
            ResultSet result;
            result = conexion.ejecutarInstruccion("SELECT numeroUnidades FROM unidad WHERE idMaterial = \"" + codigo + "\";");
            try {
                if (!result.next()) {
                    unidades = 0;
                } else {
                    unidades = result.getInt(1);
                }
            } catch (SQLException e) {
                Init.logger.log(Level.WARNING, "Error de acceso", e);
            }
            if (unidades > 0) {
                conexion.ejecutarInstruccionNoResult("UPDATE unidad SET "
                        + "numeroUnidades=" + (unidades + 1)
                        + " WHERE idMaterial=" + codigo);
                return false;
            }
        }
        
        conexion.ejecutarInstruccionNoResult(
                "INSERT INTO material(titulo, numero_clasificacion, codigo_ubicacion, prefijo) " +
                        "VALUES (\"" + titulo + "\", " 
                        + numero_clasificacion + ", " + codigo_ubicacion +", '"
                                + prefijo + "');"
        );
        
        Map<String, String> ans = conexion.select("material", 
                    "WHERE titulo='" + titulo + "'", List.of("idMaterial"));
        System.out.println(ans);
            codigo = Integer.parseInt(ans.get("idMaterial"));
        
        conexion.ejecutarInstruccionNoResult(
                "INSERT INTO unidad(numeroUnidades, idMaterial) "
                + "VALUES (" + unidades + 1 + ",\"" + codigo + "\");"
        );
        
        return true;
    }

//    public static boolean hayUnidades(String codigo, Conexion conexion) {
//        int unidades = 0;
//        if (isInDB(titulo, conexion)) {
//            ResultSet result;
//            result = conexion.ejecutarInstruccion("SELECT numeroUnidades FROM unidad WHERE idMaterial = \"" + codigo + "\";");
//            try {
//                if (!result.next()) {
//                    unidades = 0;
//                } else {
//                    unidades = result.getInt(1);
//                }
//            } catch (SQLException e) {
//                Init.logger.log(Level.WARNING, "Error de acceso:", e);
//            }
//        }
//        
//        return unidades > 0;
//    }
//    
//    private boolean isInDB(String titulo, Conexion conexion) {
//        ResultSet result;
//        result = conexion.ejecutarInstruccion("SELECT * FROM material WHERE titulo = \"" + titulo + "\";");
//        try {
//            return result.next();
//        } catch (SQLException e) {
//            Init.logger.log(Level.WARNING, "Error de acceso", e);
//            return false;
//        }
//    }

    public static boolean estaEnBD(String titulo, Conexion conexion) {
        ResultSet result;
        result = conexion.ejecutarInstruccion("SELECT * FROM material WHERE titulo = \"" + titulo + "\";");
        try {
            return result.next();
        } catch (SQLException e) {
            Init.logger.log(Level.WARNING, "Error de acceso", e);
            return false;
        }
    }
    
    public void updateSelfToDB(Conexion conexion) {
        conexion.ejecutarInstruccionNoResult(
                "UPDATE material " +
                "SET titulo='" + titulo + "',"
                + "numero_clasificacion=" + numero_clasificacion + ","
                + "codigo_ubicacion=" + codigo_ubicacion + " "
                + "WHERE titulo='" + titulo + "';"
        );
    }
    
    public int getUnidades(String titulo, Conexion conexion) {
        int unidades;
        Map<String, String> ans = conexion.select("material", 
                    "WHERE titulo='" + titulo + "'", List.of("idMaterial"));
        int codigo = Integer.parseInt(ans.get("idMaterial"));
        
        ans = conexion.select("unidades", 
                "WHERE idMaterial=" + codigo, List.of("numeroUnidades"));
        unidades = Integer.parseInt(ans.get("numeroUnidades"));
        return unidades;
    }
    
    public void setUnidades(int unidades, Conexion conexion) {
        conexion.ejecutarInstruccionNoResult(
                "UPDATE unidades SET numeroUnidades = " + unidades + " WHERE idMaterial = '" + codigo + "';"
        );
    }
}
