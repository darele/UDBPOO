package equipos;

import java.util.Map;

public class Desktop extends Equipo{
    private final String tarjeta;
    private final double memoria, torre, disco;

    public Desktop(String fabricante, String modelo, String microprocesador,
                   double memoria, String tarjeta, double torre, double disco) {
        super(fabricante, modelo, microprocesador);
        this.memoria = memoria;
        this.tarjeta = tarjeta;
        this.torre = torre;
        this.disco = disco;
    }

    @Override
    public Map<String, String> mostrar() {
        super.mostrar();
        caracteristicas.put("Memoria", Double.toString(memoria));
        caracteristicas.put("Tarjeta Grafica", tarjeta);
        caracteristicas.put("Tamaño de torre", Double.toString(torre));
        caracteristicas.put("Capacidad de Disco Duro", Double.toString(disco));
        return super.caracteristicas;
    }
}
