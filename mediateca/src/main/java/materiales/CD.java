package materiales;

import conexion.Conexion;

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
        String genero, titulo;
        int duracion = 0;
        try {
            result = conexion.ejecutarInstruccion("SELECT * FROM " + nombreTabla + " WHERE idMaterial = \"" + codigo + "\";");
            if (result.next()) {
                artista = result.getString("artista");
                genero = result.getString("genero");
                duracion = result.getInt("duracion");
                numeroCanciones = result.getInt("numeroCanciones");
            } else {
                throw new RuntimeException("No se pudo acceder a la revista con codigo: " + codigo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        result = conexion.ejecutarInstruccion("SELECT * FROM material WHERE idMaterial = \"" + codigo + "\";");
        try {
            if (result.next()) {
                titulo = result.getString("titulo");
            } else {
                throw new RuntimeException("No se pudo consultar la tabla material con el codigo: " + codigo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        int unidades;
        ResultSet result;
        result = conexion.ejecutarInstruccion("SELECT numeroUnidades FROM unidad WHERE idMaterial = \"" + super.codigo + "\";");
        try {
            if (!result.next()) {
                unidades = 0;
            } else {
                unidades = result.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

    public static boolean validarDatos(JTextField[] input, List<String> problems, Conexion conexion) {
        String codigo = input[0].getText().trim();

        int unidades;
        ResultSet result;
        result = conexion.ejecutarInstruccion("SELECT numeroUnidades FROM unidad WHERE idMaterial = \"" + codigo + "\";");
        try {
            if (!result.next()) {
                unidades = 0;
            } else {
                unidades = result.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (unidades > 0) {
            return true;
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
