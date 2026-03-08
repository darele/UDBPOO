import equipos.*;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Principal {
    // Almacenamiento en ArrayList como requiere la tarea
    private static final List<Equipo> listaEquipos = new ArrayList<>();

    public static void main(String[] args) {
        String menuPrincipal = "1. Registrar equipo\n2. Ver equipos\n3. Salir\n\nElija una opción:";
        String menuTipos = "1. Desktops\n2. Laptops\n3. Tablets\n4. Volver\n\nElija el tipo de equipo:";

        boolean salir = false;
        while (!salir) {
            String opcStr = JOptionPane.showInputDialog(null, menuPrincipal, "Menú Principal", JOptionPane.QUESTION_MESSAGE);

            // Si el usuario presiona "Cancelar" o la "X" de la ventana
            if (opcStr == null) break;

            switch (opcStr.trim()) {
                case "1":
                    registrarEquipo(menuTipos);
                    break;
                case "2":
                    verEquipos(menuTipos);
                    break;
                case "3":
                    salir = true;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción no válida. Ingrese 1, 2 o 3.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void registrarEquipo(String menuTipos) {
        String opcStr = JOptionPane.showInputDialog(null, "REGISTRAR EQUIPO\n\n" + menuTipos, "Registrar", JOptionPane.QUESTION_MESSAGE);

        // Regresar al menú principal si cancela o elige volver
        if (opcStr == null || opcStr.trim().equals("4")) return;

        if (!opcStr.equals("1") && !opcStr.equals("2") && !opcStr.equals("3")) {
            JOptionPane.showMessageDialog(null, "Opción no válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Captura de datos comunes con validación de no vacíos
        String fab = leerCadena("Ingrese el Fabricante:");
        if (fab == null) return; // Termina el registro si el usuario cancela

        String mod = leerCadena("Ingrese el Modelo:");
        if (mod == null) return;

        String proc = leerCadena("Ingrese el Microprocesador:");
        if (proc == null) return;

        switch (opcStr.trim()) {
            case "1": // Desktops
                double memDesk = leerDouble("Ingrese Memoria (GB):");
                if (memDesk < 0) return;

                String tarj = leerCadena("Ingrese Tarjeta Gráfica:");
                if (tarj == null) return;

                double torre = leerDouble("Ingrese Tamaño de torre (pulgadas):");
                if (torre < 0) return;

                double discoDesk = leerDouble("Ingrese Capacidad de disco duro (GB):");
                if (discoDesk < 0) return;

                listaEquipos.add(new Desktop(fab, mod, proc, memDesk, tarj, torre, discoDesk));
                JOptionPane.showMessageDialog(null, "¡Desktop registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                break;

            case "2": // Laptops
                double memLap = leerDouble("Ingrese Memoria (GB):");
                if (memLap < 0) return;

                double pantLap = leerDouble("Ingrese Tamaño de pantalla (pulgadas):");
                if (pantLap < 0) return;

                double discoLap = leerDouble("Ingrese Capacidad de disco duro (GB):");
                if (discoLap < 0) return;

                listaEquipos.add(new Laptop(fab, mod, proc, memLap, pantLap, discoLap));
                JOptionPane.showMessageDialog(null, "¡Laptop registrada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                break;

            case "3": // Tablets
                double pantTab = leerDouble("Ingrese Tamaño diagonal de pantalla (pulgadas):");
                if (pantTab < 0) return;

                // Opción con botones para asegurar que no quede vacío y sea una opción válida
                String[] opciones = {"Capacitiva", "Resistiva"};
                int seleccionTipo = JOptionPane.showOptionDialog(null, "Seleccione el tipo de pantalla", "Tipo de Pantalla",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
                if (seleccionTipo == -1) return; // Si cancela la ventana
                Tipo tipo = (seleccionTipo == 0) ? Tipo.Capacitiva : Tipo.Resistiva;

                double memTab = leerDouble("Ingrese Tamaño de memoria NAND (GB):");
                if (memTab < 0) return;

                String so = leerCadena("Ingrese Sistema Operativo:");
                if (so == null) return;

                listaEquipos.add(new Tablet(fab, mod, proc, pantTab, tipo, memTab, so));
                JOptionPane.showMessageDialog(null, "¡Tablet registrada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    private static void verEquipos(String menuTipos) {
        String opcStr = JOptionPane.showInputDialog(null, "VER EQUIPOS\n\n" + menuTipos, "Ver Equipos", JOptionPane.QUESTION_MESSAGE);
        if (opcStr == null || opcStr.trim().equals("4")) return;

        StringBuilder sb = new StringBuilder();

        // Filtrar y mostrar según el tipo seleccionado usando instanceof
        for (Equipo e : listaEquipos) {
            boolean coincide = false;
            if (opcStr.equals("1") && e instanceof Desktop) coincide = true;
            else if (opcStr.equals("2") && e instanceof Laptop) coincide = true;
            else if (opcStr.equals("3") && e instanceof Tablet) coincide = true;

            if (coincide) {
                Map<String, String> datos = e.mostrar();
                sb.append("------------------------------------------\n");
                for (Map.Entry<String, String> entry : datos.entrySet()) {
                    sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
            }
        }

        if (sb.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay equipos registrados de este tipo.", "Lista Vacía", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, sb.toString(), "Resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // --- MÉTODOS DE VALIDACIÓN SOLICITADOS EN LA TAREA ---

    private static String leerCadena(String mensaje) {
        String input;
        do {
            input = JOptionPane.showInputDialog(mensaje);
            if (input == null) return null; // Permite al usuario cancelar el registro
            if (input.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El dato no puede quedar vacío. Por favor, ingrese un valor.", "Validación", JOptionPane.WARNING_MESSAGE);
            }
        } while (input.trim().isEmpty());
        return input.trim();
    }

    private static double leerDouble(String mensaje) {
        double valor = -1;
        boolean valido = false;
        do {
            String input = leerCadena(mensaje);
            if (input == null) return -1; // Permite al usuario cancelar
            try {
                valor = Double.parseDouble(input);
                valido = true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Debe ingresar un valor numérico válido (ejemplo: 4.0 o 500).", "Validación Numérica", JOptionPane.ERROR_MESSAGE);
            }
        } while (!valido);
        return valor;
    }
}