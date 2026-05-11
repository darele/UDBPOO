package materiales;

import conexion.Conexion;
import gui.GUI;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Revista extends MaterialEscrito {

    private final String fechaPublicacion;
    private final int periodicidad;
    private static final String nombreTabla = "revista";
    private static final String prefijo = "REV";
    private static final List<String> campos
            = List.of("Editorial", "Periodicidad",
                    "Fecha de Publicación");

    public Revista(String codigo, Conexion conexion) {
        ResultSet result;
        String editorial = "", titulo = "";
        int periodicidad = -1;
        String fechaPublicacion = "";
        int numero_clasicacion = -1, codigo_ubicacion = -1;
        try {
            result = conexion.ejecutarInstruccion("SELECT * FROM " + nombreTabla + " WHERE idMaterial = \"" + codigo + "\";");
            if (result.next()) {
                periodicidad = result.getInt("periodicidad");
                fechaPublicacion = result.getString("fechaPublicacion");
                editorial = result.getString("editorial");
                numero_clasicacion = result.getInt("numero_clasificacion");
                codigo_ubicacion = result.getInt("codigo_ubicacion");
            } else {
                GUI.logger.error("No se pudo acceder a la revista con codigo: {}", codigo);
            }
        } catch (SQLException e) {
            GUI.logger.error("Error de accesso: ", e);
        }
        result = conexion.ejecutarInstruccion("SELECT * FROM material WHERE idMaterial = \"" + codigo + "\";");
        try {
            if (result.next()) {
                titulo = result.getString("titulo");
            } else {
                GUI.logger.error("No se pudo consultar la tabla material con el codigo: {}", codigo);
            }
        } catch (SQLException e) {
            GUI.logger.error("Error de accesso: ", e);
        }
        this.fechaPublicacion = fechaPublicacion;
        this.periodicidad = periodicidad;
        super(codigo, titulo, editorial, numero_clasicacion, codigo_ubicacion);
    }

    public Revista(JTextField[] input) {
        String codigo = input[0].getText().trim();
        String titulo = input[1].getText().trim();
        int numero_clasificacion = Integer.parseInt(input[2].getText().trim());
        int codigo_ubicacion = Integer.parseInt(input[3].getText().trim());
        String editorial = input[4].getText().trim();
        int periodicidad = Integer.parseInt(input[5].getText());
        String fechaPublicacion = input[6].getText().trim();

        super(codigo, titulo, editorial, numero_clasificacion, codigo_ubicacion);
        this.periodicidad = periodicidad;
        this.fechaPublicacion = fechaPublicacion;
    }

    @Override
    public boolean writeSelfToDB(Conexion conexion) {
        if (super.writeSelfToDB(conexion)) {
            conexion.ejecutarInstruccionNoResult(
                    "INSERT INTO " + nombreTabla + "(editorial, periodicidad, fechaPublicacion, idMaterial) "
                    + "VALUES (\"" + super.editorial + "\"," + periodicidad + ",\""
                    + fechaPublicacion + "\",\"" + super.codigo + "\");"
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

        if (!strict) {
            if (hayUnidades(codigo, conexion)) {
                return true;
            }
        }

        String titulo = input[1].getText().trim();
        String numero_clasificacion = input[2].getText();
        String codigo_ubicacion = input[3].getText();

        boolean ans = MaterialEscrito.validarDatos(codigo, titulo, numero_clasificacion, codigo_ubicacion, problems);

        try {
            Integer.parseInt(input[4].getText());
        } catch (NumberFormatException ignored) {
            ans = false;
            problems.add("El campo Periodicidad debe ser un numero entero");
        }
        String editorial = input[5].getText().trim();

        if (!codigo.isEmpty() && !codigo.substring(0, 3).equals(prefijo)) {
            ans = false;
            problems.add("El código para una revista debe empezar con " + prefijo);
        }

        if (editorial.isEmpty()) {
            ans = false;
            problems.add("El campo editorial no debe estar vacio");
        }
        if (input[6].getText().trim().isEmpty()) {
            ans = false;
            problems.add("Se debe especificar una fecha");
        }
        String fechaPublicacion = input[6].getText();
        try {
            LocalDate.parse(fechaPublicacion);
        } catch (DateTimeParseException e) {
            ans = false;
            problems.add("La fecha de publicacion se debe establecer en formato aaaa-mm-dd");
        }
        return ans;
    }

    @Override
    public void updateSelfToDB(Conexion conexion) {
        super.updateSelfToDB(conexion);
        conexion.ejecutarInstruccionNoResult(
                "UPDATE revista SET editorial = '" + editorial + "', periodicidad = " + periodicidad
                + ", fechaPublicacion = '" + fechaPublicacion + "' WHERE idMaterial = '" + codigo + "';"
        );
    }
}
