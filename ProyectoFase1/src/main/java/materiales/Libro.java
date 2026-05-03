package materiales;

import conexion.Conexion;
import gui.GUI;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Libro extends MaterialEscrito {
    private final String autor, isbn;
    private final int numeroPaginas, anoPublicacion;
    private static final String nombreTabla = "libro";
    private static final List<String> campos =
            List.of("Código", "Título", "Autor", "Número de Páginas",
                    "editorial", "ISBN", "Año de Publicación");

    public Libro(String codigo, Conexion conexion) {
        ResultSet result;
        String titulo = "", editorial = "";
        String autor = "", isbn = "";
        int numeroPaginas = -1, anoPublicacion = -1;
        try {
            result = conexion.ejecutarInstruccion("SELECT * FROM " + nombreTabla + " WHERE idMaterial = \"" + codigo + "\";");
            if (result.next()) {
                autor = result.getString("autor");
                isbn = result.getString("isbn");
                numeroPaginas = result.getInt("numPaginas");
                anoPublicacion = result.getInt("anoPublicacion");
                editorial = result.getString("editorial");
            } else {
                GUI.logger.error("No se pudo acceder al libro con codigo: {}", codigo);
            }
        } catch (SQLException e) {
            GUI.logger.error("Error de acceso", e);
        }
        result = conexion.ejecutarInstruccion("SELECT * FROM material WHERE idMaterial = \"" + codigo + "\";");
        try {
            if (result.next()) {
                titulo = result.getString("titulo");
            } else {
                GUI.logger.error("No se pudo consultar la tabla material con el codigo: {}", codigo);
            }
        } catch (SQLException e) {
            GUI.logger.error("Error de acceso", e);
        }
        this.anoPublicacion = anoPublicacion;
        this.isbn = isbn;
        this.autor = autor;
        this.numeroPaginas = numeroPaginas;
        super(codigo, titulo, editorial);
    }

    public Libro(JTextField[] input) {
        String codigo = input[0].getText().trim();
        String titulo = input[1].getText().trim();
        String autor = input[2].getText().trim();
        int numeroPaginas = Integer.parseInt(input[3].getText());
        String editorial = input[4].getText().trim(), isbn = input[5].getText().trim();
        int anoPublicacion = Integer.parseInt(input[6].getText());
        super(codigo, titulo, editorial);
        this.autor = autor;
        this.numeroPaginas = numeroPaginas;
        this.isbn = isbn;
        this.anoPublicacion = anoPublicacion;
    }

    @Override
    public void writeSelfToDB(Conexion conexion) {
        int unidades = 0;
        ResultSet result;
        result = conexion.ejecutarInstruccion("SELECT numeroUnidades FROM unidad WHERE idMaterial = \"" + super.codigo + "\";");
        try {
            if (!result.next()) {
                unidades = 0;
            } else {
                unidades = result.getInt(1);
            }
        } catch (SQLException e) {
            GUI.logger.error("Error de acceso", e);
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
                "INSERT INTO " + nombreTabla + "(autor, numPaginas, editorial, isbn, anoPublicacion, idMaterial) " +
                        "VALUES (\"" + autor + "\",\"" + numeroPaginas + "\",\"" + super.editorial + "\",\"" + isbn +
                        "\"," + anoPublicacion + ",\"" + super.codigo + "\");"
        );
    }

    public static List<String> getCampos() {
        return campos;
    }

    public static boolean validarDatos(JTextField[] input, List<String> problems, Conexion conexion, boolean strict) {
        String codigo = input[0].getText().trim(), titulo = input[1].getText().trim();
        String autor = input[2].getText().trim();

        if (!strict) {
            if (hayUnidades(codigo, conexion)) {
                return true;
            }
        }

        boolean ans = MaterialEscrito.validarDatos(codigo, titulo, problems);

        try {
            Integer.parseInt(input[3].getText());
        } catch (NumberFormatException ignored) {
            ans = false;
            problems.add("El campo Número de Páginas debe ser un numero entero");
        }
        String editorial = input[4].getText().trim(), isbn = input[5].getText().trim();
        try {
            Integer.parseInt(input[6].getText());
        } catch (NumberFormatException ignored) {
            ans = false;
            problems.add("El campo Año de Publicación debe ser un numero entero");
        }

        if (!codigo.isEmpty() && !codigo.substring(0,3).equals("LIB")) {
            ans = false;
            problems.add("El código para un libro debe empezar con LIB");
        }
        if (autor.isEmpty()) {
            ans = false;
            problems.add("El campo autor no debe estar vacio");
        }
        if (titulo.isEmpty()) {
            ans = false;
            problems.add("El campo titulo no debe estar vacio");
        }
        if (editorial.isEmpty()) {
            ans = false;
            problems.add("El campo editorial no debe estar vacio");
        }
        if (isbn.length() != 13) {
            ans = false;
            problems.add("El campo ISBN espera 13 digitos");
        }
        try {
            Long.parseLong(isbn);
        } catch (NumberFormatException ignored) {
            ans = false;
            problems.add("El campo ISBN espera únicamente dígitos");
        }
        return ans;
    }
}
