import conexion.Conexion;
import gui.GUI;

public class Main {
    public static void initDB(Conexion conexion) {
        conexion.ejecutarInstruccionNoResult(
                "CREATE DATABASE IF NOT EXISTS mediateca;"
        );
        conexion.ejecutarInstruccionNoResult("use mediateca;");
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
        gui.menuPrincipal();
    }
}
