package materiales;

import conexion.Conexion;
import gui.GUI;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CD extends MaterialAudiovisual{
    private final String artista;
    private final int numeroCanciones;
    private static final String nombreTabla = "cd";
    private static final String prefijo = "CDA";
    private static final List<String> campos =
            List.of("Código", "Título", "Artista", "Genero",
                    "Duracion", "Numero de Canciones");

    public CD(String codigo, Conexion conexion) {
        ResultSet result;
        String genero = "", titulo = "";
        String artista = "";
        int numeroCanciones = -1;
        int duracion = 0;
        String instruccion = "SELECT * FROM " + nombreTabla + " WHERE idMaterial = \"" + codigo + "\";";
        try {
            result = conexion.ejecutarInstruccion(instruccion);
            if (result.next()) {
                artista = result.getString("artista");
                genero = result.getString("genero");
                duracion = result.getInt("duracion");
                numeroCanciones = result.getInt("numeroCanciones");
            } else {
                GUI.logger.warn("No se pudo acceder a la revista con codigo: {}", codigo);
            }
        } catch (SQLException e) {
            GUI.logger.error("error al ejecutar la instruccion: {}", instruccion,e);
        }
        instruccion = "SELECT * FROM material WHERE idMaterial = \"" + codigo + "\";";
        result = conexion.ejecutarInstruccion(instruccion);
        try {
            if (result.next()) {
                titulo = result.getString("titulo");
            } else {
                GUI.logger.warn("No se pudo consultar la tabla material con el codigo: {}", codigo);
            }
        } catch (SQLException e) {
            GUI.logger.error("Error al ejecutar la instruccion: {}", instruccion, e);
        }
        this.artista = artista;
        this.numeroCanciones = numeroCanciones;
        super(codigo, titulo, genero, duracion);
    }

    public CD(JTextField[] input) {
        String codigo = input[0].getText().trim();
        String titulo = input[1].getText().trim();
        String artista = input[2].getText().trim();
        String genero = input[3].getText().trim();
        int duracion = Integer.parseInt(input[4].getText().trim());
        int numeroCanciones = Integer.parseInt(input[5].getText().trim());

        super(codigo, titulo, genero, duracion);
        this.artista = artista;
        this.numeroCanciones = numeroCanciones;
    }

    @Override
    public void writeSelfToDB(Conexion conexion) {
        int unidades = 0;
        ResultSet result;
        String instruccion = "SELECT numeroUnidades FROM unidad WHERE idMaterial = \"" + super.codigo + "\";";
        try {
            result = conexion.ejecutarInstruccion(instruccion);
            if (result.next()) {
                unidades = result.getInt(1);
            }
        } catch (SQLException e) {
            GUI.logger.error("Error al ejecutar la instruccion: {}", instruccion, e);
        }
        if (unidades > 0) {
            conexion.ejecutarInstruccionNoResult("UPDATE unidad SET " +
                    "numeroUnidades=" + (unidades + 1) +
                    " WHERE idMaterial=\"" + super.codigo + "\"");
            return;
        }
        super.writeSelfToDB(conexion);
        conexion.ejecutarInstruccionNoResult(
                "INSERT INTO unidad(numeroUnidades, idMaterial) " +
                        "VALUES (" + (unidades + 1) + ",\"" + super.codigo + "\");"
        );
        conexion.ejecutarInstruccionNoResult(
                "INSERT INTO " + nombreTabla + "(artista, genero, duracion, numeroCanciones, idMaterial) " +
                        "VALUES (\"" + artista + "\",\"" + super.genero + "\"," + super.duracion +
                        "," + numeroCanciones + ",\"" + super.codigo + "\");"
        );
    }

    public static List<String> getCampos() {
        return campos;
    }

    public static boolean validarDatos(JTextField[] input, List<String> problems, Conexion conexion, boolean strict) {
        String codigo = input[0].getText().trim();

        if (!strict) {
            if (hayUnidades(codigo, conexion)) {
                return true;
            }
        }

        String titulo = input[1].getText().trim();
        String genero = input[3].getText().trim();
        String duracion = input[4].getText().trim();

        boolean ans = MaterialAudiovisual.validarDatos(codigo, titulo, genero, duracion, problems);

        String artista = input[2].getText().trim();
        String numeroCanciones = input[5].getText().trim();

        if (!codigo.isEmpty() && !codigo.substring(0,3).equals(prefijo)) {
            ans = false;
            problems.add("El código para un CD debe empezar con " + prefijo);
        }

        if (artista.isEmpty()) {
            ans = false;
            problems.add("El campo editorial no debe estar vacio");
        }
        try {
            int canciones = Integer.parseInt(numeroCanciones);
            if (canciones < 1) {
                ans = false;
                problems.add("El campo canciones espera un entero positivo");
            }
        } catch (NumberFormatException e) {
            ans = false;
            problems.add("El campo Numero de Canciones debe ser un numero");
        }
        return ans;
    }
}
