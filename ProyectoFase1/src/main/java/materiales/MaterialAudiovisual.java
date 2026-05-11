package materiales;

import conexion.Conexion;
import java.util.List;



public class MaterialAudiovisual extends Material{
    protected String genero;
    protected int duracion;

    public MaterialAudiovisual(String codigo, String titulo, 
            String genero, int duracion, int numero_clasificacion, int codigo_ubicacion) {
        super(codigo, titulo, numero_clasificacion, codigo_ubicacion);
        this.genero = genero;
        this.duracion = duracion;
    }

    protected static boolean validarDatos(String codigo, String titulo, String genero, 
            String duracion, String numero_clasificacion, String codigo_ubicacion, List<String> problems) {
        boolean ans = Material.validarDatos(codigo, titulo, numero_clasificacion, codigo_ubicacion, problems);
        int duracionInt = 0;
        try {
            duracionInt = Integer.parseInt(duracion);
            if (duracionInt < 0) {
                ans = false;
                problems.add("El campo duracion debe ser un entero positivo");
            }
        } catch (NumberFormatException e) {
            ans = false;
            problems.add("El campo duracion debe ser un numero");
        }
        if (genero.isEmpty()) {
            ans = false;
            problems.add("El campo genero no puede estar vacio");
        }
        return ans;
    }

    @Override
    public boolean writeSelfToDB(Conexion conexion) {
        return super.writeSelfToDB(conexion);
    }
    
    @Override
    public void updateSelfToDB(Conexion conexion) {
        super.updateSelfToDB(conexion);
    }
}
