package gui;

import conexion.Conexion;
import materiales.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class GUI {

    enum tipoMaterial {
        LIBRO,
        REVISTA,
        CD,
        DVD
    }

    private final Conexion conexion;

    private final JFrame mainFrame;
    private final JPanel mainPanel;
    private final JPanel labelPanel;
    private final JPanel botonPanel;
    private final JPanel inputPanel;
    private final JPanel errorPanel;
    private final JLabel[] errores;

    public GUI(Conexion conexion) {
        this.conexion = conexion;

        mainFrame = new JFrame("Mediateca");
        mainFrame.setLayout(new GridBagLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(700, 600);

        mainPanel = new JPanel();
        botonPanel = new JPanel();
        labelPanel = new JPanel();
        inputPanel = new JPanel();
        errorPanel = new JPanel();
        errores = new JLabel[20];

        mainFrame.add(mainPanel);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                conexion.closeResulset();
                conexion.closeStatement();
                conexion.closeConnection();
                mainFrame.dispose();
                System.exit(0);
            }
        });
    }

    private void reset() {
        mainPanel.removeAll();
        botonPanel.removeAll();
        labelPanel.removeAll();
        inputPanel.removeAll();
        errorPanel.removeAll();
    }

    private void addElementos(JButton[] botones,
                              List<String> textoBotones,
                              JLabel[] labels,
                              List<String> textoLabels,
                              List<String> textoInputs,
                              JTextField[] input) {

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        errorPanel.setLayout(new BoxLayout(errorPanel, BoxLayout.Y_AXIS));
        botonPanel.setLayout(new BoxLayout(botonPanel, BoxLayout.Y_AXIS));

        // margen general
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        //margen para botones
        botonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel(textoLabels.get(i));
            labelPanel.add(labels[i]);
        }

        for (int i = 0; i < textoInputs.size(); i++) {
            input[i] = new JTextField(20);
            inputPanel.add(new JLabel(textoInputs.get(i)));
            inputPanel.add(input[i]);
            inputPanel.add(Box.createVerticalStrut(8));
        }

        // Botones con separación
        for (int i = 0; i < botones.length; i++) {
            botones[i] = new JButton(textoBotones.get(i));

            // damos el mismo tamaño a los botones
            botones[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, botones[i].getPreferredSize().height + 8));

            botonPanel.add(botones[i]);

            // Espacio entre botones (excepto después del último)
            if (i < botones.length - 1) {
                botonPanel.add(Box.createVerticalStrut(12));  // ← Este es el espacio que te faltaba
            }
        }

        for (int i = 0; i < errores.length; i++) {
            errores[i] = new JLabel();
            errorPanel.add(errores[i]);
        }

        mainPanel.add(labelPanel);
        mainPanel.add(inputPanel);
        mainPanel.add(errorPanel);
        mainPanel.add(botonPanel);

        mainFrame.setVisible(true);
    }

    private void insercionDatos(List<String> campos, tipoMaterial tipo) {
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
                    writeOk = DVD.validarDatos(input, problems, conexion);
                default:
                    break;
            }
            if (writeOk) {
                reset();
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
            insercionDatos(Libro.getCampos(), tipoMaterial.LIBRO);
        });
        botones[1].addActionListener(_ -> {
            reset();
            insercionDatos(Revista.getCampos(), tipoMaterial.REVISTA);
        });
        botones[2].addActionListener(_ -> {
            reset();
            insercionDatos(CD.getCampos(), tipoMaterial.CD);
        });
        botones[3].addActionListener(_ -> {
            reset();
            insercionDatos(DVD.getCampos(), tipoMaterial.DVD);
        });
        botones[4].addActionListener(_ -> {
            reset();
            menuPrincipal();
        });

        mainFrame.setVisible(true);
    }

    private void modificarMaterial() {
        List<String> textoLabels = List.of("Ingrese el código del material a modificar:");
        List<String> textoBotones = List.of("Buscar", "Cancelar");
        List<String> textoInputs = List.of("Código (Ej. LIB00001):");

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


            if (Material.estaEnBD(codigo, conexion)) {
                reset();
                String prefijo = codigo.length() >= 3 ? codigo.substring(0, 3).toUpperCase() : "";

                switch (prefijo) {
                    case "LIB":
                        actualizacionDatos(Libro.getCampos(), tipoMaterial.LIBRO, codigo);
                        break;
                    case "REV":
                        actualizacionDatos(Revista.getCampos(), tipoMaterial.REVISTA, codigo);
                        break;
                    case "CDA":
                        actualizacionDatos(CD.getCampos(), tipoMaterial.CD, codigo);
                        break;
                    case "DVD":
                        actualizacionDatos(DVD.getCampos(), tipoMaterial.DVD, codigo);
                        break;
                    default:
                        reset();
                        modificarMaterial();
                        errores[0].setText("Prefijo de material desconocido.");
                        break;
                }
            } else {
                errores[0].setText("El código no existe en la base de datos.");
            }
        });

        botones[1].addActionListener(_ -> {
            reset();
            menuPrincipal();
        });

        mainFrame.setVisible(true);
    }

    private void actualizacionDatos(List<String> campos, tipoMaterial tipo, String codigoABuscar) {
        List<String> textoLabels = List.of("Modificando material: " + codigoABuscar);
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
                    writeOk = DVD.validarDatos(input, problems, conexion);
                    break;
            }

            if (writeOk) {
                String codigo = input[0].getText().trim();
                String titulo = input[1].getText().trim();


                conexion.ejecutarInstruccionNoResult("UPDATE material SET titulo = '" + titulo + "' WHERE idMaterial = '" + codigo + "';");


                switch (tipo) {
                    case LIBRO:
                        String autor = input[2].getText().trim();
                        int numPaginas = Integer.parseInt(input[3].getText().trim());
                        String editorialLib = input[4].getText().trim();
                        String isbn = input[5].getText().trim();
                        int anoPub = Integer.parseInt(input[6].getText().trim());
                        conexion.ejecutarInstruccionNoResult(
                                "UPDATE libro SET autor = '" + autor + "', numPaginas = " + numPaginas +
                                        ", editorial = '" + editorialLib + "', isbn = '" + isbn +
                                        "', anoPublicacion = " + anoPub + " WHERE idMaterial = '" + codigo + "';"
                        );
                        break;
                    case REVISTA:
                        String editorialRev = input[2].getText().trim();
                        int periodicidad = Integer.parseInt(input[3].getText().trim());
                        String fechaPub = input[4].getText().trim();
                        conexion.ejecutarInstruccionNoResult(
                                "UPDATE revista SET editorial = '" + editorialRev + "', periodicidad = " + periodicidad +
                                        ", fechaPublicacion = '" + fechaPub + "' WHERE idMaterial = '" + codigo + "';"
                        );
                        break;
                    case CD:
                        String artista = input[2].getText().trim();
                        String generoCD = input[3].getText().trim();
                        int duracionCD = Integer.parseInt(input[4].getText().trim());
                        int numCanciones = Integer.parseInt(input[5].getText().trim());
                        conexion.ejecutarInstruccionNoResult(
                                "UPDATE cd SET artista = '" + artista + "', genero = '" + generoCD +
                                        "', duracion = " + duracionCD + ", numeroCanciones = " + numCanciones +
                                        " WHERE idMaterial = '" + codigo + "';"
                        );
                        break;
                    case DVD:
                        String director = input[2].getText().trim();
                        int duracionDVD = Integer.parseInt(input[3].getText().trim());
                        String generoDVD = input[4].getText().trim();
                        conexion.ejecutarInstruccionNoResult(
                                "UPDATE dvd SET director = '" + director + "', duracion = " + duracionDVD +
                                        ", genero = '" + generoDVD + "' WHERE idMaterial = '" + codigo + "';"
                        );
                        break;
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
                    "SELECT m.idMaterial, m.titulo, u.numeroUnidades " +
                            "FROM material m LEFT JOIN unidad u ON m.idMaterial = u.idMaterial"
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

    private void borrarMaterial() {
        reset();

        List<String> textoLabels = List.of("");
        List<String> textoBotones = List.of("Eliminar", "Volver");
        List<String> textoInputs = List.of("Ingrese el código del material a eliminar (ej: LIB00001)");

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
                        "No existe ningún material con el codigo:\n" + codigo,
                        "Material no encontrado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Alerta antes de eliminar el material
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "¿Esta completamente seguro de eliminar el material?\n\n" +
                            "Codigo: " + codigo + "\n\n" +
                            "Esta acción NO se puede deshacer.",
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

                    //mensaje de eliminacion
                    JOptionPane.showMessageDialog(mainFrame,
                            "Material eliminado correctamente.\n\nCodigo: " + codigo,
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

                default -> sb.append("\nTipo: Desconocido\n");
            }

        } catch (Exception ex) {
            sb.append("\n\nNota: No se pudieron cargar algunos detalles.");
        }

        sb.append("\n\nPuedes modificarlo o eliminarlo desde el menú principal.");
        return sb.toString();
    }


    public void menuPrincipal() {
        List<String> textoLabels = List.of("Seleccione una de las siguientes opciones");
        List<String> textoBotones =
                List.of("Agregar material", "Modificar material", "Listar material disponible",
                        "Borrar material", "Buscar Material", "Salir");
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
            modificarMaterial();
        });
        botones[2].addActionListener(_ -> {
            reset();
            listarMaterialesDispobles();
        });
        botones[3].addActionListener(_ -> {
            reset();
            borrarMaterial();
        });
        botones[4].addActionListener(_ -> {
            reset();
            buscarMaterial();
        });

        botones[5].addActionListener(
                _ -> mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING))
        );
        mainFrame.setVisible(true);
    }
}
