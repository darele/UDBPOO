package conexion;

import gui.GUI;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conexion {
    private final Connection conexion;
    private ResultSet respuesta;
    private final Statement comando;
    public Conexion() {
        Statement tempComando = null;
        Connection tempConexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            tempConexion = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "");
            tempComando = tempConexion.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            GUI.logger.fatal("Error al inicializar la conexion: ", e);
            System.exit(1);
        }
        comando = tempComando;
        conexion = tempConexion;
    }

    public ResultSet ejecutarInstruccion(String instruccion) {
        try {
            respuesta = comando.executeQuery(instruccion);
            return respuesta;
        } catch (SQLException e) {
            GUI.logger.error("Error al ejecutar la instrucción: {}", instruccion, e);
        }
        return null;
    }

    public void ejecutarInstruccionNoResult(String instruccion) {
        try {
            comando.executeUpdate(instruccion);
        } catch (SQLException e) {
            GUI.logger.error("Error al ejecutar la instrucción: {}", instruccion, e);
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
            GUI.logger.error("Algo salio mal al ejecutar la consulta en la base de datos", e);
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
            GUI.logger.error("Error: fallo al cerrar ResultSet", e);
        }
    }
    public void closeStatement() {
        try{
            if (comando != null){
                comando.close();
            }
        } catch (SQLException e) {
            GUI.logger.error("Error: fallo al cerrar Statement", e);
        }
    }
    public void closeConnection() {
        try {
            if (conexion != null){
                conexion.close();
            }
        } catch (SQLException e) {
            GUI.logger.error("Error: fallo al cerrar la conexion", e);
        }
    }
}
