package gui;

import conexion.Conexion;
import java.awt.event.ActionEvent;
import materiales.*;

import usuario.Usuario;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GUIAdministrador {

    private final Conexion conexion;
    private final JLabel[] errores;
    private final JFrame mainFrame;
    private final JPanel inputPanel;

    protected enum tipoDato {
        LIBRO,
        REVISTA,
        CD,
        DVD,
        USUARIO
    }

    GUI gui;

    public GUIAdministrador(GUI gui) {
        this.gui = gui;
        conexion = gui.conexion;
        errores = gui.errores;
        mainFrame = gui.mainFrame;
        inputPanel = gui.inputPanel;
    }

    private void addElementos(JButton[] botones, List<String> textoBotones, JLabel[] labels, List<String> textoLabels, List<String> campos, JTextField[] input) {
        gui.addElementos(botones, textoBotones, labels, textoLabels, campos, input);
    }

    private void reset() {
        gui.reset();
    }

    private void inicioDeSesion() {
        gui.inicioDeSesion();
    }

    private void insercionDatos(List<String> campos, tipoDato tipo) {
        List<String> textoLabels = List.of("");
        List<String> textoBotones = List.of("Agregar", "Cancelar");

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[campos.size()];

        addElementos(botones, textoBotones, labels, textoLabels, campos, input);
        botones[1].addActionListener(_ -> {
            reset();
            menuPrincipal();
        });
        ActionListener agregarListener = _ -> {
            List<String> problems = new ArrayList<>();
            boolean writeOk = true;
            switch (tipo) {
                case LIBRO:
                    writeOk = Libro.validarDatos(input, problems, conexion, false);
                    break;
                case REVISTA:
                    writeOk = Revista.validarDatos(input, problems, conexion, false);
                    break;
                case CD:
                    writeOk = CD.validarDatos(input, problems, conexion, false);
                    break;
                case DVD:
                    writeOk = DVD.validarDatos(input, problems, conexion, false);
                    break;
                case USUARIO:
                    writeOk = Usuario.validarDatos(input, problems, conexion, false);
                    break;
                default:
                    break;
            }
            if (writeOk) {
                reset();
                if (tipo == tipoDato.USUARIO) {
                    Usuario usuario = new Usuario(input);
                    usuario.writeSelftoDB(conexion);
                    menuPrincipal();
                    return;
                }
                String codigo = input[0].getText().trim();
                Material material;
                if (Material.estaEnBD(codigo, conexion)) {
                    switch (tipo) {
                        case REVISTA:
                            material = new Revista(codigo, conexion);
                            break;
                        case CD:
                            material = new CD(codigo, conexion);
                            break;
                        case DVD:
                            material = new DVD(codigo, conexion);
                            break;
                        default:
                            material = new Libro(codigo, conexion);
                            break;
                    }
                } else {
                    switch (tipo) {
                        case REVISTA:
                            material = new Revista(input);
                            break;
                        case CD:
                            material = new CD(input);
                            break;
                        case DVD:
                            material = new DVD(input);
                            break;
                        default:
                            material = new Libro(input);
                            break;
                    }
                }
                material.writeSelfToDB(conexion);
                menuPrincipal();
            } else {
                for (JLabel error : errores) {
                    error.setText("");
                }
                for (int i = 0; i < problems.size(); i++) {
                    errores[i].setText(problems.get(i));
                }
            }
        };
        botones[0].addActionListener(agregarListener);
        mainFrame.setVisible(true);
    }

    private void agregar() {
        List<String> textoLabels = List.of("Seleccione el material que desea agregar");
        List<String> textoBotones = List.of("Agregar libro", "Agregar revista", "Agregar CD",
                "Agregar DVD", "Salir");
        List<String> textoInputs = List.of();

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[textoInputs.size()];

        addElementos(botones, textoBotones, labels, textoLabels, textoInputs, input);

        botones[0].addActionListener(_ -> {
            reset();
            insercionDatos(Libro.getCampos(), tipoDato.LIBRO);
        });
        botones[1].addActionListener(_ -> {
            reset();
            insercionDatos(Revista.getCampos(), tipoDato.REVISTA);
        });
        botones[2].addActionListener(_ -> {
            reset();
            insercionDatos(CD.getCampos(), tipoDato.CD);
        });
        botones[3].addActionListener(_ -> {
            reset();
            insercionDatos(DVD.getCampos(), tipoDato.DVD);
        });
        botones[4].addActionListener(_ -> {
            reset();
            menuPrincipal();
        });

        mainFrame.setVisible(true);
    }

    private void modificar(tipoDato tipo) {
        List<String> textoLabels, textoInputs;
        if (tipo == tipoDato.USUARIO) {
            textoLabels = List.of("Ingrese el carnet del usuario a modificar:");
            textoInputs = List.of("Carnet (Ej. xy123456):");
        } else {
            textoLabels = List.of("Ingrese el código del material a modificar:");
            textoInputs = List.of("Código (Ej. LIB00001):");
        }
        List<String> textoBotones = List.of("Buscar", "Cancelar");

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[textoInputs.size()];

        addElementos(botones, textoBotones, labels, textoLabels, textoInputs, input);

        botones[0].addActionListener(_ -> {
            String codigo = input[0].getText().trim();
            if (codigo.isEmpty()) {
                errores[0].setText("Por favor, ingrese un código.");
                return;
            }

            if (!(tipo == tipoDato.USUARIO)) {
                if (Material.estaEnBD(codigo, conexion)) {
                    reset();
                    String prefijo = codigo.length() >= 3 ? codigo.substring(0, 3).toUpperCase() : "";
                    switch (prefijo) {
                        case "LIB":
                            actualizacionDatos(Libro.getCampos(), tipoDato.LIBRO, codigo);
                            break;
                        case "REV":
                            actualizacionDatos(Revista.getCampos(), tipoDato.REVISTA, codigo);
                            break;
                        case "CDA":
                            actualizacionDatos(CD.getCampos(), tipoDato.CD, codigo);
                            break;
                        case "DVD":
                            actualizacionDatos(DVD.getCampos(), tipoDato.DVD, codigo);
                            break;
                        default:
                            reset();
                            modificar(tipoDato.LIBRO);
                            errores[0].setText("Prefijo de material desconocido.");
                            break;
                    }
                } else {
                    errores[0].setText("El código no existe en la base de datos.");
                }
            } else {
                if (Usuario.estaEnBD(codigo, conexion)) {
                    reset();
                    actualizacionDatos(Usuario.getCampos(), tipoDato.USUARIO, codigo);
                } else {
                    errores[0].setText("Usuario no encontrado");
                }
            }
        });

        botones[1].addActionListener(_ -> {
            reset();
            menuPrincipal();
        });

        mainFrame.setVisible(true);
    }

    private void actualizacionDatos(List<String> campos, tipoDato tipo, String codigoABuscar) {
        List<String> textoLabels;
        if (tipo == tipoDato.USUARIO) {
            textoLabels = List.of("Modificando usuario: " + codigoABuscar);
        } else {
            textoLabels = List.of("Modificando material: " + codigoABuscar);
        }
        List<String> textoBotones = List.of("Guardar Cambios", "Cancelar");

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[campos.size()];

        addElementos(botones, textoBotones, labels, textoLabels, campos, input);

        input[0].setText(codigoABuscar);
        input[0].setEditable(false);

        botones[1].addActionListener(_ -> {
            reset();
            menuPrincipal();
        });

        botones[0].addActionListener(_ -> {
            List<String> problems = new ArrayList<>();
            boolean writeOk = true;

            switch (tipo) {
                case LIBRO:
                    writeOk = Libro.validarDatos(input, problems, conexion, true);
                    break;
                case REVISTA:
                    writeOk = Revista.validarDatos(input, problems, conexion, true);
                    break;
                case CD:
                    writeOk = CD.validarDatos(input, problems, conexion, true);
                    break;
                case DVD:
                    writeOk = DVD.validarDatos(input, problems, conexion, true);
                    break;
                case USUARIO:
                    writeOk = Usuario.validarDatos(input, problems, conexion, false);
                    break;
            }

            if (writeOk) {
                if (tipo == tipoDato.USUARIO) {
                    Usuario usuario = new Usuario(input);
                    usuario.updateSelftoDB(conexion);
                } else {
                    Material material;
                    switch (tipo) {
                        case LIBRO:
                            material = new Libro(input);
                            break;
                        case REVISTA:
                            material = new Revista(input);
                            break;
                        case CD:
                            material = new CD(input);
                            break;
                        case DVD:
                            material = new DVD(input);
                            break;
                        default:
                            GUI.logger.warn(
                                    "No se categorizo correctamente el material a modificar: {}", tipo);
                            return;
                    }
                    material.updateSelfToDB(conexion);
                }
                reset();
                menuPrincipal();

            } else {
                for (JLabel error : errores) {
                    error.setText("");
                }
                for (int i = 0; i < problems.size(); i++) {
                    errores[i].setText(problems.get(i));
                }
            }

        });

        mainFrame.setVisible(true);
    }

    private void listarMaterialesDispobles() {
        List<String> textoLabels = List.of("Listado de Materiales Disponibles");
        List<String> textoBotones = List.of("Volver al Menú");
        List<String> textoInputs = List.of();

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[textoInputs.size()];

        addElementos(botones, textoBotones, labels, textoLabels, textoInputs, input);

        JTextArea textArea = new JTextArea(15, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        StringBuilder sb = new StringBuilder();
        sb.append("CÓDIGO\t|\tUNIDADES\t|\tTÍTULO\n");
        sb.append("----------------------------------------------------------------------------------\n");

        try {
            java.sql.ResultSet rs = conexion.ejecutarInstruccion(
                    "SELECT m.idMaterial, m.titulo, u.numeroUnidades "
                    + "FROM material m LEFT JOIN unidad u ON m.idMaterial = u.idMaterial"
            );
            while (rs.next()) {
                sb.append(rs.getString("idMaterial")).append("\t|\t")
                        .append(rs.getInt("numeroUnidades")).append("\t|\t")
                        .append(rs.getString("titulo")).append("\n");
            }
        } catch (java.sql.SQLException ex) {
            sb.append("Error al cargar los datos desde la BD.");
        }

        textArea.setText(sb.toString());
        inputPanel.add(scrollPane);

        botones[0].addActionListener(_ -> {
            reset();
            menuPrincipal();
        });

        mainFrame.setVisible(true);
    }

    private void borrarMaterial(tipoDato tipo) {
        reset();

        List<String> textoLabels = List.of("");
        List<String> textoBotones = List.of("Eliminar", "Volver");
        List<String> textoInputs;
        if (tipo == tipoDato.USUARIO) {
            textoInputs = List.of("Ingrese el carnet del usuario a eliminar (ej: xy123456)");
        } else {
            textoInputs = List.of("Ingrese el código del material a eliminar (ej: LIB00001)");
        }

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[textoInputs.size()];

        addElementos(botones, textoBotones, labels, textoLabels, textoInputs, input);

        // Boton volver
        botones[1].addActionListener(_ -> {
            reset();
            menuPrincipal();
        });

        // Boton eliminar
        botones[0].addActionListener((ActionEvent _) -> {
            String codigo = input[0].getText().trim();

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Por favor ingrese un código",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String codigoTexto = "codigo";
            String materialTexto = "material";

            if (tipo == tipoDato.USUARIO) {
                codigoTexto = "carnet";
                materialTexto = "usuario";
            }
            if (!Usuario.estaEnBD(codigo, conexion) && !Material.estaEnBD(codigo, conexion)) {
                JOptionPane.showMessageDialog(mainFrame,
                        "No existe ningún " + materialTexto + " con el " + codigoTexto + ":\n" + codigo,
                        materialTexto + " no encontrado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Alerta antes de eliminar el material
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "¿Esta completamente seguro de eliminar el " + materialTexto + "?\n\n"
                    + "Codigo: " + codigo + "\n\n"
                    + "Esta acción NO se puede deshacer.",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Para eliminar material en bd
                    conexion.ejecutarInstruccionNoResult("DELETE FROM libro WHERE idMaterial = '" + codigo + "'");
                    conexion.ejecutarInstruccionNoResult("DELETE FROM revista WHERE idMaterial = '" + codigo + "'");
                    conexion.ejecutarInstruccionNoResult("DELETE FROM cd WHERE idMaterial = '" + codigo + "'");
                    conexion.ejecutarInstruccionNoResult("DELETE FROM dvd WHERE idMaterial = '" + codigo + "'");
                    conexion.ejecutarInstruccionNoResult("DELETE FROM unidad WHERE idMaterial = '" + codigo + "'");
                    conexion.ejecutarInstruccionNoResult("DELETE FROM material WHERE idMaterial = '" + codigo + "'");
                    conexion.ejecutarInstruccionNoResult("DELETE FROM usuarios WHERE carnet = '" + codigo + "'");

                    //mensaje de eliminacion
                    JOptionPane.showMessageDialog(mainFrame,
                            materialTexto + " eliminado correctamente.\n\n" + codigoTexto + ": " + codigo,
                            "Eliminacion Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);

                    reset();
                    // Volver al menú principal
                    menuPrincipal();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame,
                            "Error al eliminar el material:\n" + ex.getMessage(),
                            "Error en la eliminación",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainFrame.setVisible(true);
    }

    private void buscarMaterial() {
        reset();

        List<String> textoLabels = List.of("");
        List<String> textoBotones = List.of("Buscar", "Volver");
        List<String> textoInputs = List.of("Ingrese el codigo del material (ej: LIB00001)");

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[textoInputs.size()];

        addElementos(botones, textoBotones, labels, textoLabels, textoInputs, input);

        // Boton volver
        botones[1].addActionListener(_ -> {
            reset();
            menuPrincipal();
        });

        // Boton Buscar
        botones[0].addActionListener(_ -> {
            String codigo = input[0].getText().trim();

            if (codigo.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,
                        "Por favor ingrese un código",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Material.estaEnBD(codigo, conexion)) {
                JOptionPane.showMessageDialog(mainFrame,
                        "No se encontro ningun material con el codigo: " + codigo,
                        "Material no encontrado", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Obtener y mostrar toda la info del material
            String informacion = obtenerInformacionCompleta(codigo);

            JOptionPane.showMessageDialog(mainFrame,
                    informacion,
                    "Información del Material",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        mainFrame.setVisible(true);
    }

    // obterner la información del material
    private String obtenerInformacionCompleta(String codigo) {
        StringBuilder sb = new StringBuilder();
        sb.append("MATERIAL ENCONTRADO\n\n");
        sb.append("Codigo : ").append(codigo).append("\n");

        try {
            // Consultar título
            var rsTitulo = conexion.ejecutarInstruccion(
                    "SELECT titulo FROM material WHERE idMaterial = '" + codigo + "'"
            );
            if (rsTitulo.next()) {
                sb.append("Titulo : ").append(rsTitulo.getString("titulo")).append("\n");
            }
            conexion.closeResulset();

            String prefijo = codigo.substring(0, 3).toUpperCase();

            switch (prefijo) {
                case "LIB" -> {
                    var rs = conexion.ejecutarInstruccion("SELECT * FROM libro WHERE idMaterial = '" + codigo + "'");
                    if (rs.next()) {
                        sb.append("\nTipo          : Libro\n");
                        sb.append("Autor         : ").append(rs.getString("autor")).append("\n");
                        sb.append("Editorial     : ").append(rs.getString("editorial")).append("\n");
                        sb.append("ISBN          : ").append(rs.getString("isbn")).append("\n");
                        sb.append("Páginas       : ").append(rs.getInt("numPaginas")).append("\n");
                        sb.append("Año Publicación: ").append(rs.getInt("anoPublicacion")).append("\n");
                    }
                    conexion.closeResulset();
                }

                case "REV" -> {
                    var rs = conexion.ejecutarInstruccion("SELECT * FROM revista WHERE idMaterial = '" + codigo + "'");
                    if (rs.next()) {
                        sb.append("\nTipo             : Revista\n");
                        sb.append("Editorial        : ").append(rs.getString("editorial")).append("\n");
                        sb.append("Periodicidad     : ").append(rs.getInt("periodicidad")).append("\n");
                        sb.append("Fecha Publicación: ").append(rs.getString("fechaPublicacion")).append("\n");
                    }
                    conexion.closeResulset();
                }

                case "CDA" -> {
                    var rs = conexion.ejecutarInstruccion("SELECT * FROM cd WHERE idMaterial = '" + codigo + "'");
                    if (rs.next()) {
                        sb.append("\nTipo               : CD\n");
                        sb.append("Artista            : ").append(rs.getString("artista")).append("\n");
                        sb.append("Género             : ").append(rs.getString("genero")).append("\n");
                        sb.append("Duración           : ").append(rs.getInt("duracion")).append(" minutos\n");
                        sb.append("Número de Canciones: ").append(rs.getInt("numeroCanciones")).append("\n");
                    }
                    conexion.closeResulset();
                }

                case "DVD" -> {
                    var rs = conexion.ejecutarInstruccion("SELECT * FROM dvd WHERE idMaterial = '" + codigo + "'");
                    if (rs.next()) {
                        sb.append("\nTipo     : DVD\n");
                        sb.append("Director : ").append(rs.getString("director")).append("\n");
                        sb.append("Género   : ").append(rs.getString("genero")).append("\n");
                        sb.append("Duración : ").append(rs.getInt("duracion")).append(" minutos\n");
                    }
                    conexion.closeResulset();
                }

                default ->
                    sb.append("\nTipo: Desconocido\n");
            }

        } catch (Exception ex) {
            sb.append("\n\nNota: No se pudieron cargar algunos detalles.");
        }

        sb.append("\n\nPuedes modificarlo o eliminarlo desde el menú principal.");
        return sb.toString();
    }

    private void listarUsuarios() {
        List<String> textoLabels = List.of("Listado de usuarios");
        List<String> textoBotones = List.of("Volver al Menú");
        List<String> textoInputs = List.of();

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[textoInputs.size()];

        addElementos(botones, textoBotones, labels, textoLabels, textoInputs, input);

        List<List<String>> datos = new ArrayList<>();
        try {
            java.sql.ResultSet rs = conexion.ejecutarInstruccion(
                    "SELECT carnet, nombre, rol, username "
                    + "FROM usuarios"
            );
            while (rs.next()) {
                datos.add(new ArrayList<>());
                datos.get(0).add(rs.getString("carnet"));
                datos.add(new ArrayList<>());
                datos.get(1).add(rs.getString("nombre"));
                datos.add(new ArrayList<>());
                datos.get(2).add(rs.getString("rol"));
                datos.add(new ArrayList<>());
                datos.get(3).add(rs.getString("username"));
            }
        } catch (java.sql.SQLException ex) {
            GUI.logger.error("error al cargar los datos de la bd", ex);
        }

        String[] columnas = new String[]{"Carnet", "Nombre", "Rol", "Usuario"};

        int m = columnas.length;
        int n = datos.get(0).size();

        String[][] data = new String[n][m];

        for (int fila = 0; fila < n; fila++) {
            for (int columna = 0; columna < m; columna++) {
                data[fila][columna] = datos.get(columna).get(fila);
            }
        }

        JTable tabla = new JTable(data, columnas);

        JScrollPane scrollPane = new JScrollPane(tabla);

        inputPanel.add(scrollPane);

        botones[0].addActionListener(_ -> {
            reset();
            menuPrincipal();
        });

        mainFrame.setVisible(true);
    }

    public void menuPrincipal() {
        List<String> textoLabels = List.of("Seleccione una de las siguientes opciones");
        List<String> textoBotones
                = List.of("Agregar material",
                        "Modificar material",
                        "Listar material disponible",
                        "Borrar material",
                        "Buscar Material",
                        "Agregar Usuario",
                        "Modificar usuario",
                        "Borrar usuario",
                        "Listar usuarios",
                        "Salir");
        List<String> textoInputs = List.of();

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[textoInputs.size()];

        addElementos(botones, textoBotones, labels, textoLabels, textoInputs, input);
        botones[0].addActionListener(_ -> {
            reset();
            agregar();
        });
        botones[1].addActionListener(_ -> {
            reset();
            modificar(tipoDato.LIBRO);
        });
        botones[2].addActionListener(_ -> {
            reset();
            listarMaterialesDispobles();
        });
        botones[3].addActionListener(_ -> {
            reset();
            borrarMaterial(tipoDato.LIBRO);
        });
        botones[4].addActionListener(_ -> {
            reset();
            buscarMaterial();
        });

        botones[5].addActionListener(_ -> {
            reset();
            insercionDatos(Usuario.getCampos(), tipoDato.USUARIO);
        });

        botones[6].addActionListener(_ -> {
            reset();
            modificar(tipoDato.USUARIO);
        });

        botones[7].addActionListener(_ -> {
            reset();
            borrarMaterial(tipoDato.USUARIO);
        });

        botones[8].addActionListener(_ -> {
            reset();
            listarUsuarios();
        });

        botones[9].addActionListener(_ -> {
            reset();
            inicioDeSesion();
        });
        mainFrame.setVisible(true);
    }
}
