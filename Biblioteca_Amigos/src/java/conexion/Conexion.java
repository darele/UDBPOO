/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

/**
 *
 * @author pdarw
 */
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexion {
    private final Connection conexion;
    private ResultSet respuesta;
    private final Statement comando;
    private static final Logger logger = Logger.getLogger("MyJSPLogger");
    
    public Conexion() {
        
        Statement tempComando = null;
        Connection tempConexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            tempConexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblioteca_amigos", "root", "");
            tempComando = tempConexion.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error al inicializar la conexion: ", e);
            throw new RuntimeException("Error crítico de conexión a la base de datos", e);
        }
        comando = tempComando;
        conexion = tempConexion;
    }

    public ResultSet ejecutarInstruccion(String instruccion) {
        try {
            respuesta = comando.executeQuery(instruccion);
            return respuesta;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error al ejecutar la instrucción: " + instruccion, e);
        }
        return null;
    }

    public void ejecutarInstruccionNoResult(String instruccion) {
        try {
            comando.executeUpdate(instruccion);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error al ejecutar la instrucción: " + instruccion, e);
        }
    }

    public Map<String, String> select(String tabla, String condicion, List<String> columnas) {
        String cols = "*";
        if (columnas != null && columnas.size() > 0) {
            cols = String.join(", ", columnas);
        }
        String sentencia = "SELECT " + cols + " FROM " + tabla + " " + condicion;

        Map<String, String> ans = new HashMap<>();
        try {
            ResultSet respuesta = ejecutarInstruccion(sentencia);
            if (respuesta == null) {
                return ans;
            }
            ResultSetMetaData metadatos = respuesta.getMetaData();
            int columnCount = metadatos.getColumnCount();
            while(respuesta.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String nombreColumna = metadatos.getColumnName(i);
                    String valor = respuesta.getString(nombreColumna);
                    ans.put(nombreColumna, valor);
                }
            }
            return ans;
        } catch (SQLException | RuntimeException e) {
            logger.log(Level.SEVERE,"Algo salio mal al ejecutar la consulta en la base de datos", e);
            ans.put("errores", "Algo salio mal al ejecutar la consulta en la base de datos");
            return ans;
        }
    }

    public void closeResulset() {
        try{
            if (respuesta != null){
                respuesta.close();
            }
        }catch (SQLException e) {
            logger.log(Level.WARNING, "Error: fallo al cerrar ResultSet", e);
        }
    }
    public void closeStatement() {
        try{
            if (comando != null){
                comando.close();
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error: fallo al cerrar Statement", e);
        }
    }
    public void closeConnection() {
        try {
            if (conexion != null){
                conexion.close();
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error: fallo al cerrar la conexion", e);
        }
    }
}
