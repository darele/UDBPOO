import java.util.ArrayList;
import java.util.List;

public class UsoDeList {
    public static void main(String[] args) {
        // Declaracion
        List<String> cadenas = new ArrayList<>();

        //Agregar elementos
        cadenas.add(0, "hola");
        cadenas.add("Mundo");
        List<String> tempList = List.of("soy", "Ariel");
        List<String> templist2 = List.of("y", "esto", "es");
        cadenas.addAll(1, tempList);
        cadenas.addAll(templist2);
        cadenas.addFirst("Hey!");
        cadenas.addLast("Progra UDB");

        //Eliminar elementos
        cadenas.remove(2);
        cadenas.removeFirst();
        cadenas.removeLast();
        cadenas.clear();

        //Mostrar resultados
        System.out.println(cadenas);
    }
}
