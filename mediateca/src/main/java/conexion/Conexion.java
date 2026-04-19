package conexion;

import java.sql.*;

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
        try {
            respuesta = comando.executeQuery(instruccion);
            return respuesta;
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al ejecutar la instrucción: " + instruccion, e
            );
        }
    }

    public void ejecutarInstruccionNoResult(String instruccion) {
        try {
            comando.executeUpdate(instruccion);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al ejecutar la instrucción: " + instruccion, e
            );
        }
    }

    public void closeResulset() {
        try{
            if (respuesta != null){
                respuesta.close();
            }
        }catch (SQLException e) {
            System.out.println("ERROR:Fallo en SQL: " + e.getMessage());
        }
    }
    public void closeStatement() {
        try{
            if (comando != null){
                comando.close();
            }
        } catch (SQLException e) {
            System.out.println("ERROR:Fallo en SQL: " + e.getMessage());
        }
    }
    public void closeConnection() {
        try {
            if (conexion != null){
                conexion.close();
            }
        } catch (SQLException e) {
            System.out.println("ERROR:Fallo en SQL: " + e.getMessage());
        }
    }
}
