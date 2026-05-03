package gui;

import conexion.Conexion;
import materiales.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

public class GUI {

    private static final String salt = "Rafael Torres ";
    public static final Logger logger = LogManager.getLogger();

    protected final Conexion conexion;

    protected final JFrame mainFrame;
    protected final JPanel mainPanel;
    protected final JPanel labelPanel;
    protected final JPanel botonPanel;
    protected final JPanel inputPanel;
    protected final JPanel errorPanel;
    protected final JLabel[] errores;

    public GUI(GUI gui) {
        this.conexion = gui.conexion;
        this.mainFrame = gui.mainFrame;
        this.mainPanel = gui.mainPanel;
        this.labelPanel = gui.labelPanel;
        this.botonPanel = gui.botonPanel;
        this.inputPanel = gui.inputPanel;
        this.errorPanel = gui.errorPanel;
        this.errores = gui.errores;
    }

    public GUI(Conexion conexion) {
        this.conexion = conexion;

        mainFrame = new JFrame("Biblioteca Amigos de Don Bosco");
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

    protected void reset() {
        mainPanel.removeAll();
        botonPanel.removeAll();
        labelPanel.removeAll();
        inputPanel.removeAll();
        errorPanel.removeAll();
    }

    protected void addElementos(JButton[] botones,
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

    public static String encriptar(String contrasena) {
        byte[] contrasenaCifradaBytes = null;
        try {
            contrasenaCifradaBytes = MessageDigest.getInstance("SHA-256").digest(
                    (salt + contrasena).getBytes(StandardCharsets.UTF_8)
            );
        } catch (NoSuchAlgorithmException e) {
            GUI.logger.error("Error al encriptar la contrasena ", e);
            return "";
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : contrasenaCifradaBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    protected Map<String, String> validarUserPassword(JTextField[] input) {
        Map<String, String> usuario = conexion.select("usuarios",
                "WHERE username='" + input[0].getText() + "'",
                List.of("username", "password", "rol"));
        Map<String, String> ans = new HashMap<>();
        if (usuario.isEmpty()) {
            ans.put("errores1", "Usuario o contrasena incorrectos");
            return ans;
        }
        if (usuario.containsKey("errores")) {
            ans.put("errores2", usuario.get("errores"));
            return ans;
        }

        String contrasena = input[1].getText();
        String contrasenaCifrada = encriptar(contrasena);
        String contrasenaReal = usuario.get("password");
        if (contrasenaCifrada.equals(contrasenaReal)) {
            ans = usuario;
            return ans;
        }
        ans.put("errores1", "Usuario o contrasena incorrectos");
        return ans;
    }

    public void inicioDeSesion() {
        List<String> textoLabels = List.of("Introduzca sus datos de inicio de sesión");
        List<String> textoBotones =
                List.of("Iniciar Sesión", "Salir");
        List<String> textoInputs = List.of("Nombre de usuario", "Contraseña");

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[textoInputs.size()];

        addElementos(botones, textoBotones, labels, textoLabels, textoInputs, input);
        GUIAdministrador adminGUI = new GUIAdministrador(this);
        GUIEstudianteProfesor estudianteGUI = new GUIEstudianteProfesor(this);

        botones[0].addActionListener(_ -> {
            Map<String, String> valido = validarUserPassword(input);
            if (valido.containsKey("rol")) {
                reset();
                switch (valido.get("rol")) {
                    case "administrador":
                        reset();
                        adminGUI.menuPrincipal();
                        break;
                    default:
                        reset();
                        estudianteGUI.menuPrincipal();
                        break;
                }
            } else {
                errores[0].setText(valido.get("errores1"));
                errores[1].setText(valido.getOrDefault("errores2", ""));
            }
        });

        botones[1].addActionListener(
                _ -> mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING))
        );
        mainFrame.setVisible(true);
    }
}
