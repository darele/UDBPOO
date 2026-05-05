package materiales;

import conexion.Conexion;
import gui.GUI;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DVD extends MaterialAudiovisual {

    private final String director;
    private static final String nombreTabla = "dvd";
    private static final String prefijo = "DVD";
    private static final List<String> campos
            = List.of("Código", "Título", "Director", "Duracion",
                    "Genero");

    public DVD(String codigo, Conexion conexion) {
        ResultSet result;
        String genero = "", titulo = "";
        String director = "";
        int duracion = 0;
        String instruccion = "SELECT * FROM " + nombreTabla + " WHERE idMaterial = \"" + codigo + "\";";
        try {
            result = conexion.ejecutarInstruccion(instruccion);
            if (result.next()) {
                director = result.getString("director");
                genero = result.getString("genero");
                duracion = result.getInt("duracion");
            } else {
                GUI.logger.error("No se pudo acceder a la revista con codigo: {}", codigo);
            }
        } catch (SQLException e) {
            GUI.logger.error("Error al ejecutar la instruccion: {}", instruccion, e);
        }
        instruccion = "SELECT * FROM material WHERE idMaterial = \"" + codigo + "\";";
        result = conexion.ejecutarInstruccion(instruccion);
        try {
            if (result.next()) {
                titulo = result.getString("titulo");
            } else {
                GUI.logger.error("No se pudo consultar la tabla material con el codigo: {}", codigo);
            }
        } catch (SQLException e) {
            GUI.logger.error("Error de acceso: ", e);
        }
        this.director = director;
        super(codigo, titulo, genero, duracion);
    }

    public DVD(JTextField[] input) {
        String codigo = input[0].getText().trim();
        String titulo = input[1].getText().trim();
        String director = input[2].getText().trim();
        int duracion = Integer.parseInt(input[3].getText().trim());
        String genero = input[4].getText().trim();

        super(codigo, titulo, genero, duracion);
        this.director = director;
    }

    @Override
    public void writeSelfToDB(Conexion conexion) {
        super.writeSelfToDB(conexion);
        conexion.ejecutarInstruccionNoResult(
                "INSERT INTO " + nombreTabla + "(director, duracion, genero, idMaterial) "
                + "VALUES (\"" + director + "\"," + super.duracion + ",\"" + super.genero
                + "\",\"" + super.codigo + "\");"
        );
    }

    public static List<String> getCampos() {
        return campos;
    }

    public static boolean validarDatos(JTextField[] input, List<String> problems, Conexion conexion, boolean strict) {
        String codigo = input[0].getText().trim();
        String titulo = input[1].getText().trim();
        String director = input[2].getText().trim();
        String duracion = input[3].getText().trim();
        String genero = input[4].getText().trim();

        if (!strict && Material.estaEnBD(codigo, conexion)) {
            problems.add("Material con codigo" + codigo + " ya existe en la base de datos");
            return false;
        }

        boolean ans = MaterialAudiovisual.validarDatos(codigo, titulo, genero, duracion, problems);

        if (!codigo.isEmpty() && !codigo.substring(0, 3).equals(prefijo)) {
            ans = false;
            problems.add("El código para un DVD debe empezar con " + prefijo);
        }

        if (director.isEmpty()) {
            ans = false;
            problems.add("El campo Director no debe estar vacio");
        }
        return ans;
    }

    @Override
    public void updateSelfToDB(Conexion conexion) {
        super.updateSelfToDB(conexion);
        conexion.ejecutarInstruccionNoResult(
                "UPDATE dvd SET director = '" + director + "', duracion = " + duracion
                + ", genero = '" + genero + "' WHERE idMaterial = '" + codigo + "';"
        );
    }
}
