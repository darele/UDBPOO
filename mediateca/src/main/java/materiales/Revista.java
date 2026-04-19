package materiales;

import conexion.Conexion;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class Revista extends MaterialEscrito {
    private final String fechaPublicacion;
    private final int periodicidad;
    private static final String nombreTabla = "revista";
    private static final String prefijo = "REV";
    private static final List<String> campos =
            List.of("Código", "Título", "Editorial", "Periodicidad",
                    "Fecha de Publicación");

    public Revista(String codigo, Conexion conexion) {
        ResultSet result;
        String editorial, titulo;
        try {
            result = conexion.ejecutarInstruccion("SELECT * FROM " + nombreTabla + " WHERE idMaterial = \"" + codigo + "\";");
            if (result.next()) {
                periodicidad = result.getInt("periodicidad");
                fechaPublicacion = result.getString("fechaPublicacion");
                editorial = result.getString("editorial");
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
        super(codigo, titulo, editorial);
    }

    public Revista(JTextField[] input) {
        String codigo = input[0].getText().trim();
        String titulo = input[1].getText().trim();
        String editorial = input[2].getText().trim();
        int periodicidad = Integer.parseInt(input[3].getText());
        String fechaPublicacion = input[4].getText().trim();

        super(codigo, titulo, editorial);
        this.periodicidad = periodicidad;
        this.fechaPublicacion = fechaPublicacion;
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
                "INSERT INTO " + nombreTabla + "(editorial, periodicidad, fechaPublicacion, idMaterial) " +
                        "VALUES (\"" + super.editorial + "\"," + periodicidad + ",\"" +
                        fechaPublicacion + "\",\"" + super.codigo + "\");"
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

        boolean ans = MaterialEscrito.validarDatos(codigo, titulo, problems);

        try {
            Integer.parseInt(input[3].getText());
        } catch (NumberFormatException ignored) {
            ans = false;
            problems.add("El campo Periodicidad debe ser un numero entero");
        }
        String editorial = input[2].getText().trim();

        if (!codigo.isEmpty() && !codigo.substring(0,3).equals(prefijo)) {
            ans = false;
            problems.add("El código para una revista debe empezar con " + prefijo);
        }


        if (editorial.isEmpty()) {
            ans = false;
            problems.add("El campo editorial no debe estar vacio");
        }
        if (input[4].getText().trim().isEmpty()) {
            ans = false;
            problems.add("Se debe especificar una fecha");
        }
        String fechaPublicacion = input[4].getText();
        try {
            LocalDate.parse(fechaPublicacion);
        } catch (DateTimeParseException e) {
            ans = false;
            problems.add("La fecha de publicacion se debe establecer en formato aaaa-mm-dd");
        }
        return ans;
    }
}
