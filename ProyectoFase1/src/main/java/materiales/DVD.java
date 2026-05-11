package materiales;

import conexion.Conexion;
import gui.GUI;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DVD extends MaterialAudiovisual {

    private final String director;
    private static final String nombreTabla = "dvd";
    private static final String prefijo = "DVD";
    private static final List<String> campos
            = List.of("Director", "Duracion",
                    "Genero");

    public DVD(String codigo, Conexion conexion) {
        ResultSet result;
        String genero = "", titulo = "";
        String director = "";
        int duracion = 0;
        int numero_clasificacion = -1, codigo_ubicacion = -1;
        String instruccion = "SELECT * FROM " + nombreTabla + " WHERE idMaterial = \"" + codigo + "\";";
        try {
            result = conexion.ejecutarInstruccion(instruccion);
            if (result.next()) {
                director = result.getString("director");
                genero = result.getString("genero");
                duracion = result.getInt("duracion");
                numero_clasificacion = result.getInt("numero_clasificacion");
                codigo_ubicacion = result.getInt("codigo_ubicacion");
            } else {
                GUI.logger.error("No se pudo acceder al DVD con codigo: {}", codigo);
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
        super(codigo, titulo, genero, duracion, numero_clasificacion, codigo_ubicacion);
    }

    public DVD(JTextField[] input) {
        String codigo = input[0].getText().trim();
        String titulo = input[1].getText().trim();
        int numero_clasificacion = Integer.parseInt(input[2].getText());
        int codigo_ubicacion = Integer.parseInt(input[3].getText());
        String director = input[4].getText().trim();
        int duracion = Integer.parseInt(input[5].getText().trim());
        String genero = input[6].getText().trim();

        super(codigo, titulo, genero, duracion, numero_clasificacion, codigo_ubicacion);
        this.director = director;
    }

    @Override
    public boolean writeSelfToDB(Conexion conexion) {
        if (super.writeSelfToDB(conexion)) {
            conexion.ejecutarInstruccionNoResult(
                    "INSERT INTO " + nombreTabla + "(director, duracion, genero, idMaterial) "
                    + "VALUES (\"" + director + "\"," + super.duracion + ",\"" + super.genero
                    + "\",\"" + super.codigo + "\");"
            );
        }
        return true;
    }

    public static List<String> getCampos() {
        List<String> ans = new ArrayList<>(Material.getCampos());
        ans.addAll(campos);
        return ans;
    }

    public static boolean validarDatos(JTextField[] input, List<String> problems, Conexion conexion, boolean strict) {
        String codigo = input[0].getText().trim();
        String titulo = input[1].getText().trim();
        String numero_clasificacion = input[2].getText().trim();
        String codigo_ubicacion = input[3].getText().trim();
        String director = input[4].getText().trim();
        String duracion = input[5].getText().trim();
        String genero = input[6].getText().trim();

        if (!strict) {
            if (hayUnidades(codigo, conexion)) {
                return true;
            }
        }

        boolean ans = MaterialAudiovisual.validarDatos(codigo, titulo, 
                genero, duracion, numero_clasificacion, codigo_ubicacion, problems);

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
