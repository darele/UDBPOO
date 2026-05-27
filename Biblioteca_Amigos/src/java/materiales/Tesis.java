//package materiales;
//
//import conexion.Conexion;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import javax.swing.JTextField;
//import utils.Init;
//
//public class Tesis extends Material {
//
//    private static final List<String> campos = List.of(
//            "Autor",
//            "Carrera",
//            "Ano de Publicacion"
//    );
//
//    public static List<String> getCampos() {
//        List<String> ans = new ArrayList<>(Material.getCampos());
//        ans.addAll(campos);
//        return ans;
//    }
//
//    private final String autor;
//    private final int anoPublicacion;
//    private final String carrera;
//    private static final String nombreTabla = "tesis";
//    private static final String prefijo = "TES";
//    
//    public Tesis(String codigo, Conexion conexion) {
//        ResultSet result;
//        String titulo = "";
//        String autor = "";
//        String carrera = "";
//        int anoPublicacion = -1;
//        int numero_clasicacion = -1, codigo_ubicacion = -1;
//        try {
//            result = conexion.ejecutarInstruccion("SELECT * FROM " + nombreTabla + " WHERE idMaterial = \"" + codigo + "\";");
//            if (result.next()) {
//                autor = result.getString("autor");
//                carrera = result.getString("carrera");
//                anoPublicacion = result.getInt("ano_publicacion");
//            } else {
//                Init.logger.log(Level.WARNING, "No se pudo acceder a la revista con codigo: {}", codigo);
//            }
//        } catch (SQLException e) {
//            Init.logger.log(Level.WARNING, "Error de accesso: ", e);
//        }
//        result = conexion.ejecutarInstruccion("SELECT * FROM material WHERE idMaterial = \"" + codigo + "\";");
//        try {
//            if (result.next()) {
//                titulo = result.getString("titulo");
//            } else {
//                Init.logger.log(Level.WARNING, "No se pudo consultar la tabla material con el codigo: {}", codigo);
//            }
//        } catch (SQLException e) {
//            Init.logger.log(Level.WARNING, "Error de accesso: ", e);
//        }
//        this.autor = autor;
//        this.anoPublicacion = anoPublicacion;
//        this.carrera = carrera;
//        super(codigo, titulo, numero_clasicacion, codigo_ubicacion);
//    }
//
//    public Tesis(JTextField[] input) {
//        String codigo = input[0].getText().trim();
//        String titulo = input[1].getText().trim();
//        int numero_clasificacion = Integer.parseInt(input[2].getText());
//        int codigo_ubicacion = Integer.parseInt(input[3].getText());
//        String autor = input[4].getText().trim();
//        String carrera = input[5].getText().trim();
//        int anoPublicacion = Integer.parseInt(input[6].getText().trim());
//        super(codigo, titulo, numero_clasificacion, codigo_ubicacion);
//        this.autor = autor;
//        this.carrera = carrera;
//        this.anoPublicacion = anoPublicacion;
//    }
//
//    @Override
//    public boolean writeSelfToDB(Conexion conexion) {
//        if (super.writeSelfToDB(conexion)) {
//            conexion.ejecutarInstruccionNoResult(
//                    "INSERT INTO " + nombreTabla + "(autor, carrera, ano_publicacion, idMaterial) "
//                    + "VALUES (\"" + autor + "\", \"" + carrera + "\", " + anoPublicacion + ", "
//                    + "'" + super.codigo + "');"
//            );
//        }
//        return true;
//    }
//    
//    @Override
//    public void updateSelfToDB(Conexion conexion) {
//        conexion.ejecutarInstruccionNoResult(
//                "UPDATE tesis " +
//                "SET autor='" + autor + "',"
//                + "carrera='" + carrera + "',"
//                + "ano_publicacion=" + anoPublicacion + " "
//                + "WHERE idMaterial='" + codigo + "';"
//            );
//    }
//    
//    public static boolean validarDatos(JTextField[] input, 
//            List<String> problems, Conexion conexion, boolean strict) {
//        String codigo = input[0].getText().trim(), titulo = input[1].getText().trim();
//        String numero_clasificacion = input[2].getText();
//        String codigo_ubicacion = input[3].getText();
//        String autor = input[4].getText().trim();
//        String carrera = input[5].getText().trim();
//        String anoPublicacion = input[6].getText().trim();
//        
//        boolean ans = true;
//
//        if (!strict) {
//            if (hayUnidades(codigo, conexion)) {
//                return true;
//            }
//        }
//        
//        Material.validarDatos(codigo, titulo, numero_clasificacion, codigo_ubicacion, problems);
//        
//        if (!codigo.isEmpty() && !codigo.substring(0, 3).equals(prefijo)) {
//            ans = false;
//            problems.add("El código para una revista debe empezar con " + prefijo);
//        }
//        
//        if (autor.isEmpty()) {
//            ans = false;
//            problems.add("Introduzca el nombre del autor");
//        }
//        
//        if (carrera.isEmpty()) {
//            ans = false;
//            problems.add("El campo carrera no puede estar vacio");
//        }
//        
//        if (anoPublicacion.isEmpty()) {
//            ans = false;
//            problems.add("El campo Año de Publicación no puede estar vacío");
//        } else {
//            try {
//                Integer.valueOf(anoPublicacion);
//            } catch (NumberFormatException _) {
//                problems.add("El campo Año de Publicación espera un entero");
//                ans = false;
//            }
//        }
//        
//        return ans;
//    }
//}
