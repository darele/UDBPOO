package materiales;

import conexion.Conexion;

import java.util.List;

public abstract class MaterialEscrito extends Material {
    protected String editorial;

    public MaterialEscrito(String titulo, 
            String editorial, int numero_clasificacion, int codigo_ubicacion, String prefijo) {
        super(titulo, numero_clasificacion, codigo_ubicacion, prefijo);
        this.editorial = editorial;
    }

    protected static boolean validarDatos(String titulo,
            String numero_clasificacion, String codigo_ubicacion, List<String> problems) {
        return Material.validarDatos(titulo, numero_clasificacion, codigo_ubicacion, problems);
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
