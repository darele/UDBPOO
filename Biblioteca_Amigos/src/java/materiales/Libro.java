package materiales;

import conexion.Conexion;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import utils.Init;

public class Libro extends MaterialEscrito {

    private static final String PREFIJO = "LIB";
    
    private final String autor, isbn;
    private final int numeroPaginas, anoPublicacion;
    private static final String nombreTabla = "libro";
    protected static final List<String> campos = List.of(
            "Autor", "Numero de Paginas", "editorial", "ISBN", "Ano de Publicacion");
    protected static final List<TipoCampo> tipoCampos = 
            List.of(
                    TipoCampo.OPCION, 
                    TipoCampo.NUMERO, 
                    TipoCampo.OPCION,
                    TipoCampo.TEXTO,
                    TipoCampo.NUMERO
            );
    protected static final List<String> dbFields = List.of(
            "autor", "numPaginas", "editorial", "isbn", "anoPublicacion"
    );

//    public Libro(String codigo, Conexion conexion) {
//        ResultSet result;
//        String titulo = "", editorial = "";
//        String autor = "", isbn = "";
//        int numeroPaginas = -1, anoPublicacion = -1;
//        int numero_clasificacion = -1, codigo_ubicacion = -1;
//        try {
//            result = conexion.ejecutarInstruccion("SELECT * FROM " + nombreTabla + " WHERE idMaterial = \"" + codigo + "\";");
//            if (result.next()) {
//                autor = result.getString("autor");
//                isbn = result.getString("isbn");
//                numeroPaginas = result.getInt("numPaginas");
//                anoPublicacion = result.getInt("anoPublicacion");
//                editorial = result.getString("editorial");
//                numero_clasificacion = result.getInt("numero_clasificacion");
//                codigo_ubicacion = result.getInt("codigo_ubicacion");
//            } else {
//                Init.logger.log(Level.WARNING, "No se pudo acceder al libro con codigo: {}", codigo);
//            }
//        } catch (SQLException e) {
//            Init.logger.log(Level.WARNING, "Error de acceso", e);
//        }
//        result = conexion.ejecutarInstruccion("SELECT * FROM material WHERE idMaterial = \"" + codigo + "\";");
//        try {
//            if (result.next()) {
//                titulo = result.getString("titulo");
//            } else {
//                Init.logger.log(Level.WARNING, "No se pudo consultar la tabla material con el codigo: {}", codigo);
//            }
//        } catch (SQLException e) {
//            Init.logger.log(Level.WARNING, "Error de acceso", e);
//        }
//        this.anoPublicacion = anoPublicacion;
//        this.isbn = isbn;
//        this.autor = autor;
//        this.numeroPaginas = numeroPaginas;
//        super(codigo, titulo, editorial, numero_clasificacion, codigo_ubicacion);
//    }

    public Libro(String[] input) {
        String titulo = input[0].trim();
        int numero_clasificacion = Integer.parseInt(input[1]);
        int codigo_ubicacion = Integer.parseInt(input[2]);
        String autor = input[3].trim();
        int numeroPaginas = Integer.parseInt(input[4]);
        String editorial = input[5].trim(), isbn = input[6].trim();
        int anoPublicacion = Integer.parseInt(input[7]);
        super(titulo, editorial, numero_clasificacion, codigo_ubicacion, PREFIJO);
        this.autor = autor;
        this.numeroPaginas = numeroPaginas;
        this.isbn = isbn;
        this.anoPublicacion = anoPublicacion;
    }

    @Override
    public boolean writeSelfToDB(Conexion conexion) {
        
        if (super.writeSelfToDB(conexion)) {
            conexion.ejecutarInstruccionNoResult(
                    "INSERT INTO " + nombreTabla + "(autor, numPaginas, editorial, isbn, anoPublicacion, idMaterial) "
                    + "VALUES (\"" + autor + "\",\"" + numeroPaginas + "\",\"" + super.editorial + "\",\"" + isbn
                    + "\"," + anoPublicacion + ",\"" + super.codigo + "\");"
            );
        }
        return true;
    }

    public static List<String> getCampos() {
        List<String> ans = new ArrayList<>(Material.getCampos());
        ans.addAll(campos);
        return ans;
    }
    
    public static List<TipoCampo> getTipoCampos() {
        List<TipoCampo> ans = new ArrayList<>(Material.getTipoCampos());
        ans.addAll(tipoCampos);
        return ans;
    }
    
    public static List<String> getDBFields() {
        List<String> ans = new ArrayList<>(Material.dbFields);
        ans.addAll(dbFields);
        return ans;
    }

    public static boolean validarDatos(String[] input, List<String> problems, Conexion conexion) {
        String titulo = input[0].trim();
        String numero_clasificacion = input[1].trim();
        String codigo_ubicacion = input[2].trim();
        String autor = input[3].trim();
        String numeroPaginas = input[4];
        String editorial = input[5].trim(), isbn = input[6].trim();
        String anoPublicacion = input[7];

        boolean ans = MaterialEscrito.validarDatos(titulo, numero_clasificacion, codigo_ubicacion, problems);

        try {
            Integer.parseInt(numeroPaginas);
        } catch (NumberFormatException ignored) {
            ans = false;
            problems.add("El campo Número de Páginas debe ser un numero entero");
        }
       
        try {
            Integer.parseInt(anoPublicacion);
        } catch (NumberFormatException ignored) {
            ans = false;
            problems.add("El campo Año de Publicación debe ser un numero entero");
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

    @Override
    public void updateSelfToDB(Conexion conexion) {
        super.updateSelfToDB(conexion);
        conexion.ejecutarInstruccionNoResult(
                "UPDATE libro SET autor = '" + autor + "', numPaginas = " + numeroPaginas
                + ", editorial = '" + editorial + "', isbn = '" + isbn
                + "', anoPublicacion = " + anoPublicacion + " WHERE idMaterial = '" + codigo + "';"
        );
    }
}
