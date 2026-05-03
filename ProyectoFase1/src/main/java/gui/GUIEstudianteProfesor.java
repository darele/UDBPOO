package gui;

import javax.swing.*;
import java.util.List;

public class GUIEstudianteProfesor {
    private final GUI gui;

    public GUIEstudianteProfesor(GUI gui) {
        this.gui = gui;
    }

    public void menuPrincipal() {
        List<String> textoLabels = List.of("Seleccione una de las siguientes opciones");
        List<String> textoBotones =
                List.of("Salir");
        List<String> textoInputs = List.of();

        JButton[] botones = new JButton[textoBotones.size()];
        JLabel[] labels = new JLabel[textoLabels.size()];
        JTextField[] input = new JTextField[textoInputs.size()];

        gui.addElementos(botones, textoBotones, labels, textoLabels, textoInputs, input);

        botones[0].addActionListener(_ -> {
            gui.reset();
            gui.inicioDeSesion();
        });
    }
}
