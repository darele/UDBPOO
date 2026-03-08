package equipos;

import java.util.Map;

public class Laptop extends Equipo{
    private final double memoria, pantalla, disco;

    public Laptop(String fabricante, String modelo, String microprocesador,
                  double memoria, double pantalla, double disco) {
        super(fabricante, modelo, microprocesador);
        this.memoria = memoria;
        this.pantalla = pantalla;
        this.disco = disco;
    }

    @Override
    public Map<String, String> mostrar() {
        super.mostrar();
        caracteristicas.put("Memoria", Double.toString(memoria));
        caracteristicas.put("Tamaño pantalla", Double.toString(pantalla));
        caracteristicas.put("Capacidad de disco", Double.toString(disco));
        return super.caracteristicas;
    }
}
