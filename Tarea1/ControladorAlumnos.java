import java.util.*;

public class ControladorAlumnos {
    static void main(String[] args) {
        int op;
        String carnet, nombre;
        Map<String, Alumno> alumnos = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println();
            System.out.println("¿Qué desea hacer?");
            System.out.println("1. Ingresar un nuevo alumno.");
            System.out.println("2. Buscar un alumno por carnet.");
            System.out.println("3. Eliminar un alumno por carnet.");
            System.out.println("4. Mostrar todos los alumnos.");
            System.out.println("5. Salir.");
            op = scanner.nextInt();
            scanner.nextLine();
            switch (op) {
                case 1:
                    System.out.print("Ingrese el carnet del alumno: ");
                    carnet = scanner.nextLine();
                    System.out.print("Ingrese el nombre completo del alumno: ");
                    nombre = scanner.nextLine();
                    alumnos.put(carnet, new Alumno(carnet, nombre));
                    System.out.println("Alumno ingresado exitosamente");
                    break;
                case 2:
                    System.out.println("Ingrese el carnet del alumno: ");
                    carnet = scanner.nextLine();
                    if (alumnos.containsKey(carnet)) {
                        Alumno alumno = alumnos.get(carnet);
                        System.out.println("Alumno encontrado: " + alumno);
                    } else {
                        System.out.println("Alumno no encontrado, no se puede mostrar");
                    }
                    break;
                case 3:
                    System.out.print("Ingrese el carnet del alumno: ");
                    carnet = scanner.nextLine();
                    if (alumnos.containsKey(carnet)) {
                        Alumno alumno = alumnos.get(carnet);
                        alumnos.remove(carnet);
                        System.out.println("Alumno eliminado " + alumno);
                    } else {
                        System.out.println("Alumno no encontrado, no se puede eliminar");
                    }
                    break;
                case 4:
                    for (Map.Entry<String, Alumno> it : alumnos.entrySet()) {
                        System.out.println(it.getValue());
                    }
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        } while(op != 5);
    }
}
