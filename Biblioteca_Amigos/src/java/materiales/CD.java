//package materiales;
//
//import conexion.Conexion;
//import javax.swing.*;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import utils.Init;
//
//public class CD extends MaterialAudiovisual {
//
//    private final String artista;
//    private final int numeroCanciones;
//    private static final String nombreTabla = "cd";
//    private static final String prefijo = "CDA";
//    private static final List<String> campos
//            = List.of("Artista", "Genero",
//                    "Duracion", "Numero de Canciones");
//
//    public CD(String codigo, Conexion conexion) {
//        ResultSet result;
//        String genero = "", titulo = "";
//        String artista = "";
//        int numeroCanciones = -1;
//        int duracion = 0;
//        int numero_clasificacion = -1, codigo_ubicacion = -1;
//        String instruccion = "SELECT * FROM " + nombreTabla + " WHERE idMaterial = \"" + codigo + "\";";
//        try {
//            result = conexion.ejecutarInstruccion(instruccion);
//            if (result.next()) {
//                artista = result.getString("artista");
//                genero = result.getString("genero");
//                duracion = result.getInt("duracion");
//                numeroCanciones = result.getInt("numeroCanciones");
//                numero_clasificacion = result.getInt("numero_clasificacion");
//                codigo_ubicacion = result.getInt("codigo_ubicacion");
//            } else {
//                Init.logger.log(Level.WARNING, "No se pudo acceder a la revista con codigo: {}", codigo);
//            }
//        } catch (SQLException e) {
//            Init.logger.log(Level.WARNING, "error al ejecutar la instruccion: " + instruccion, e);
//        }
//        instruccion = "SELECT * FROM material WHERE idMaterial = \"" + codigo + "\";";
//        result = conexion.ejecutarInstruccion(instruccion);
//        try {
//            if (result.next()) {
//                titulo = result.getString("titulo");
//            } else {
//                Init.logger.log(Level.WARNING, "No se pudo consultar la tabla material con el codigo: {}", codigo);
//            }
//        } catch (SQLException e) {
//            Init.logger.log(Level.WARNING, "Error al ejecutar la instruccion: " + instruccion, e);
//        }
//        this.artista = artista;
//        this.numeroCanciones = numeroCanciones;
//        super(codigo, titulo, genero, duracion, numero_clasificacion, codigo_ubicacion);
//    }
//
//    public CD(JTextField[] input) {
//        String codigo = input[0].getText().trim();
//        String titulo = input[1].getText().trim();
//        int numero_clasificacion = Integer.parseInt(input[2].getText());
//        int codigo_ubicacion = Integer.parseInt(input[3].getText());
//        String artista = input[4].getText().trim();
//        String genero = input[5].getText().trim();
//        int duracion = Integer.parseInt(input[6].getText().trim());
//        int numeroCanciones = Integer.parseInt(input[7].getText().trim());
//
//        super(codigo, titulo, genero, duracion, numero_clasificacion, codigo_ubicacion);
//        this.artista = artista;
//        this.numeroCanciones = numeroCanciones;
//    }
//
//    @Override
//    public boolean writeSelfToDB(Conexion conexion) {
//        if (super.writeSelfToDB(conexion)) {
//            conexion.ejecutarInstruccionNoResult(
//                    "INSERT INTO " + nombreTabla + "(artista, genero, duracion, numeroCanciones, idMaterial) "
//                    + "VALUES (\"" + artista + "\",\"" + super.genero + "\"," + super.duracion
//                    + "," + numeroCanciones + ",\"" + super.codigo + "\");"
//            );
//        }
//        return true;
//    }
//
//    
//
//    public static List<String> getCampos() {
//        List<String> ans = new ArrayList<>(Material.getCampos());
//        ans.addAll(campos);
//        return ans;
//    }
//
//    public static boolean validarDatos(JTextField[] input, List<String> problems, Conexion conexion, boolean strict) {
//        String codigo = input[0].getText().trim();
//
//        if (!strict) {
//            if (hayUnidades(codigo, conexion)) {
//                return true;
//            }
//        }
//
//        String titulo = input[1].getText().trim();
//        String numero_clasificacion = input[2].getText().trim();
//        String codigo_ubicacion = input[3].getText().trim();
//        String genero = input[5].getText().trim();
//        String duracion = input[6].getText().trim();
//
//        boolean ans = MaterialAudiovisual.validarDatos(codigo, titulo,
//                genero, duracion, numero_clasificacion, codigo_ubicacion, problems);
//
//        String artista = input[4].getText().trim();
//        String numeroCanciones = input[7].getText().trim();
//
//        if (!codigo.isEmpty() && !codigo.substring(0, 3).equals(prefijo)) {
//            ans = false;
//            problems.add("El código para un CD debe empezar con " + prefijo);
//        }
//
//        if (artista.isEmpty()) {
//            ans = false;
//            problems.add("El campo editorial no debe estar vacio");
//        }
//        try {
//            int canciones = Integer.parseInt(numeroCanciones);
//            if (canciones < 1) {
//                ans = false;
//                problems.add("El campo canciones espera un entero positivo");
//            }
//        } catch (NumberFormatException e) {
//            ans = false;
//            problems.add("El campo Numero de Canciones debe ser un numero");
//        }
//        return ans;
//    }
//
//    @Override
//    public void updateSelfToDB(Conexion conexion) {
//        super.updateSelfToDB(conexion);
//        conexion.ejecutarInstruccionNoResult(
//                "UPDATE cd SET artista = '" + artista + "', genero = '" + genero
//                + "', duracion = " + duracion + ", numeroCanciones = " + numeroCanciones
//                + " WHERE idMaterial = '" + codigo + "';"
//        );
//    }
//}
