package materiales;

import conexion.Conexion;

import java.util.List;

public abstract class MaterialEscrito extends Material {
    protected String editorial;

    public MaterialEscrito(String codigo, String titulo, String editorial) {
        super(codigo, titulo);
        this.editorial = editorial;
    }

    protected static boolean validarDatos(String codigo, String titulo, List<String> problems) {
        return Material.validarDatos(codigo, titulo, problems);
    }

    @Override
    public void writeSelfToDB(Conexion conexion) {
        super.writeSelfToDB(conexion);
    }
    
    @Override
    public void updateSelfToDB(Conexion conexion) {
        super.updateSelfToDB(conexion);
    }
}
