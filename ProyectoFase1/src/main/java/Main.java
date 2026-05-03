import conexion.Conexion;
import gui.GUI;

import java.util.List;
import java.util.Map;

public class Main {
    public static void initDB(Conexion conexion) {
        conexion.ejecutarInstruccionNoResult(
                "CREATE DATABASE IF NOT EXISTS biblioteca_amigos;"
        );
        conexion.ejecutarInstruccionNoResult("use biblioteca_amigos;");
        conexion.ejecutarInstruccionNoResult("CREATE TABLE IF NOT EXISTS usuarios(" +
                "carnet VARCHAR(8) PRIMARY KEY, " +
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
                    "carnet, nombre, rol, username, password) VALUES" +
                    "('ad123456', 'administrador', 'administrador', 'admin', " +
                    "'9245169e32b59d483d6486b0eb9eb7abb601764f5bad92eb97bbb0fa88f0529c');"
            );
        }
        //usuario = estudiante1
        //contrasena = 12345
        ans = conexion.select("usuarios", "WHERE username='estudiante1'", List.of("username"));
        if (!ans.containsKey("username")) {
            conexion.ejecutarInstruccionNoResult("INSERT INTO usuarios(" +
                    "carnet, nombre, rol, username, password) VALUES" +
                    "('jg123456', 'Josue Gomez', 'estudiante', 'estudiante1', " +
                    "'9245169e32b59d483d6486b0eb9eb7abb601764f5bad92eb97bbb0fa88f0529c');"
            );
        }
        //usuario = estudiante1
        //contrasena = 12345
        ans = conexion.select("usuarios", "WHERE username='profesor1'", List.of("username"));
        if (!ans.containsKey("username")) {
            conexion.ejecutarInstruccionNoResult("INSERT INTO usuarios(" +
                    "carnet, nombre, rol, username, password) VALUES" +
                    "('ae123456', 'Orlando Avalos', 'profesor', 'profesor1', " +
                    "'9245169e32b59d483d6486b0eb9eb7abb601764f5bad92eb97bbb0fa88f0529c');"
            );
        }

        conexion.ejecutarInstruccionNoResult("CREATE TABLE IF NOT EXISTS prestamo(" +
                "idPrestamo INT PRIMARY KEY, " +
                "fechaPrestamo DATE NOT NULL DEFAULT CURDATE(), " +
                "estado ENUM('prestado', 'devuelto'), " +
                "carnet VARCHAR(8) REFERENCES usuario(carnet), " +
                "idMaterial VARCHAR(8) REFERENCES material(idMaterial)" +
                ");"
        );

        conexion.ejecutarInstruccionNoResult(
                "CREATE TABLE IF NOT EXISTS material(" +
                        "idMaterial VARCHAR(8) PRIMARY KEY," +
                        "titulo VARCHAR(45) NOT NULL" +
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
    }

    public static void main(String[] args) {
        Conexion conexion = new Conexion();
        GUI gui = new GUI(conexion);
        initDB(conexion);
        gui.inicioDeSesion();
    }
}
