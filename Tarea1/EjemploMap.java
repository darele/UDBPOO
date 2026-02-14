/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;

/**
 *
 * @author orlando
 */
public class EjemploMap {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        //Declarando un map
        Map<Integer, String> alumno = new HashMap();
        
        //Asignando claves y valores a cada elemento
        alumno.put(1, "Ariel");
        alumno.put(2, "Darwin");
        alumno.put(3, "Nathaly");
        alumno.put(4, "Orlando");
        alumno.put(5, "Josue");
        
        //Imprimiendo tosos los elementos del hashmap usando un ciclo for, 
        //entryset para obtener los pares clave-valor y declaramos una 
        //variable del tipo entry para representar cada par mientra se se recorre
        for(HashMap.Entry i:alumno.entrySet()){
            System.out.println(i.getKey()+" - "+i.getValue());
        }
                       
        //Espacio en blanco para separar impresiones
        System.out.println("");
        
        //eliminar elementos del mapa por medio de la clave
        alumno.remove(2);        
        
        //Imprimiendo los elementos que quedan en el hashmap        
        for(HashMap.Entry j:alumno.entrySet()){
            System.out.println(j.getKey()+" - "+j.getValue());
        }
    }
    
}
