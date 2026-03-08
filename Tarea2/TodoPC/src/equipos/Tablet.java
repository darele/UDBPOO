package equipos;

import java.util.Map;

public class Tablet extends Equipo{
    private final double pantalla, memoria;
    private final String sistema;
    private final Tipo tipo;

    public Tablet(String fabricante, String modelo, String microprocesador,
                  double pantalla, Tipo tipo, double memoria, String sistema) {
        super(fabricante, modelo, microprocesador);
        this.pantalla = pantalla;
        this.memoria = memoria;
        this.sistema = sistema;
        this.tipo = tipo;
    }

    @Override
    public Map<String, String> mostrar() {
        super.mostrar();
        caracteristicas.put("Tamaño diagonal de pantalla", Double.toString(pantalla));
        caracteristicas.put("¿Capacitiva/Resistiva?", tipo.name());
        caracteristicas.put("Tamaño de memoria", Double.toString(memoria));
        caracteristicas.put("Sistema Operativo", sistema);
        return super.caracteristicas;
    }
}
