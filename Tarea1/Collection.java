/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package Main;

import java.util.ArrayList;
import java.util.Collection;
public class Proyecto1 {
 public static void main(String[] args) {
        Collection<String> ListaAlumnos;
     ListaAlumnos = new ArrayList<>();
        
     ListaAlumnos.add("Jose Sanchez");
     ListaAlumnos.add("Maria Hernandez");
     ListaAlumnos.add("Carlos Alvarado");
     ListaAlumnos.add("Carlos Alvarado");
     
     System.out.println("Numero de alumnos antes de eliminar:" + 
             ListaAlumnos.size()) ;
     System.out.println ("Alumnos:" +ListaAlumnos.toString()) ;
     ListaAlumnos.remove ("Jose Sanchez") ;
     System.out.println("Numero de alumnos despues de eliminar a Jose Sanchez:" + 
             ListaAlumnos.size()) ;
     System.out.println ("Alumnos:" +ListaAlumnos.toString()); 
            
            
             
    
     
        
                
    }
}
