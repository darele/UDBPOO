package gui;

import conexion.Conexion;
import materiales.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class GUI {

    enum tipoMaterial{
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
        mainFrame.setSize(700, 500);

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

        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel(textoLabels.get(i));
            labelPanel.add(labels[i], BorderLayout.NORTH);
        }
        for (int i = 0; i < textoInputs.size(); i++) {
            input[i] = new JTextField();
            inputPanel.add(new JLabel(textoInputs.get(i)));
            inputPanel.add(input[i]);
        }
        for (int i = 0; i < botones.length; i++) {
            botones[i] = new JButton(textoBotones.get(i));
            botonPanel.add(botones[i]);
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
        botones[1].addActionListener(e -> {
            reset();
            menuPrincipal();
        });
        ActionListener agregarListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> problems = new ArrayList<>();
                boolean writeOk = true;
                switch (tipo) {
                    case LIBRO:
                        writeOk = Libro.validarDatos(input, problems, conexion);
                        break;
                    case REVISTA:
                        writeOk = Revista.validarDatos(input, problems, conexion);
                        break;
                    case CD:
                        writeOk = CD.validarDatos(input, problems, conexion);
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
                    for (int i = 0; i < errores.length; i++) {
                        errores[i].setText("");
                    }
                    for(int i = 0; i < problems.size(); i++) {
                        errores[i].setText(problems.get(i));
                    }
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

        botones[0].addActionListener(e -> {
            reset();
            insercionDatos(Libro.getCampos(), tipoMaterial.LIBRO);
        });
        botones[1].addActionListener(e -> {
            reset();
            insercionDatos(Revista.getCampos(), tipoMaterial.REVISTA);
        });
        botones[2].addActionListener(e ->  {
            reset();
            insercionDatos(CD.getCampos(), tipoMaterial.CD);
        });
        botones[3].addActionListener(e -> {
            reset();
            insercionDatos(DVD.getCampos(), tipoMaterial.DVD);
        });
        botones[4].addActionListener(e -> {
            reset();
            menuPrincipal();
        });

        mainFrame.setVisible(true);
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
        botones[0].addActionListener(e -> {
            reset();
            agregar();
        });


        botones[5].addActionListener(e -> {
            mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        });

        mainFrame.setVisible(true);
    }
}
