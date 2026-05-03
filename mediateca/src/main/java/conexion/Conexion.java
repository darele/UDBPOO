package conexion;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conexion {
    private final Connection conexion;
    private ResultSet respuesta;
    private final Statement comando;
    public Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "");
            comando = conexion.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet ejecutarInstruccion(String instruccion) {
//        Map<String, String> ans = new HashMap<>();
        try {
            respuesta = comando.executeQuery(instruccion);
//            ResultSetMetaData metadatos = respuesta.getMetaData();
//            while(respuesta.next()) {
//                for (int i = 0; i < metadatos.getColumnCount(); i++) {
//                    String nombreColumna = metadatos.getColumnName(i);
//                    ans.put(nombreColumna, respuesta.getString(nombreColumna));
//                }
//            }
            return respuesta;
        } catch (SQLException e) {
            throw new RuntimeException(
                "Error al ejecutar la instrucción: " + instruccion, e
            );
//            ans.put("errores", "Error al ejecutar la instrucción: " + instruccion);
        }
    }

    public void ejecutarInstruccionNoResult(String instruccion) {
        Map<String, String> ans = new HashMap<>();
        try {
            comando.executeUpdate(instruccion);
//            return null;
        } catch (SQLException e) {
//            ans.put("errores", "Error al ejecutar la instruccion " + instruccion);
//            return ans;
            throw new RuntimeException(
                    "Error al ejecutar la instrucción: " + instruccion, e
            );
        }
    }

//    public Map<String, String> select(String tabla, String id, List<String> columnas) {
//        String cols = "*";
//        if (columnas != null && columnas.size() > 0) {
//            cols = String.join(", ", columnas);
//        }
//        String sentencia = "SELECT " + cols + " FROM " + tabla + " WHERE idMaterial=\"" + id + "\"";
//        return ejecutarInstruccion(sentencia);
//    }
//
//    public Map<String, String> update(String tabla, String id, List<String> columnas, List<String> valores) {
//        Map<String, String> ans = new HashMap<>();
//        if (columnas == null || valores == null) {
//            ans.put("errores", "Lista de valores o columnas null");
//        }
//        if (columnas.size() != valores.size()) {
//            ans.put("errores", "El numero de valores y el numero de columnas no coinciden para hacer la actualizacion");
//            return ans;
//        }
//        if (columnas.size() < 1 || valores.size() < 1) {
//            ans.put("errores", "nombres de columnas o de valores vacios");
//            return ans;
//        }
//        StringBuilder columnaIgualValor = new StringBuilder();
//        columnaIgualValor.append(columnas.get(0)).append("=").append(valores.get(0));
//        for (int i = 0; i < columnas.size(); i++) {
//            columnaIgualValor.append(columnas.get(i)).append("=").append(valores.get(i));
//        }
//        String sentencia = "UPDATE " + tabla + " SET " + columnaIgualValor;
//        return ejecutarInstruccionNoResult(sentencia);
//    }

    public void closeResulset() {
        try{
            if (respuesta != null){
                respuesta.close();
            }
        }catch (SQLException e) {
            System.out.println("ERROR:Fallo al cerrar ResultSet: " + e.getMessage());
        }
    }
    public void closeStatement() {
        try{
            if (comando != null){
                comando.close();
            }
        } catch (SQLException e) {
            System.out.println("ERROR:Fallo al cerrar Statement: " + e.getMessage());
        }
    }
    public void closeConnection() {
        try {
            if (conexion != null){
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("ERROR:Fallo al cerrar conexion: " + e.getMessage());
        }
    }
}
