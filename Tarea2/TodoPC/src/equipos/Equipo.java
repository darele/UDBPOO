package equipos;

import java.util.HashMap;
import java.util.Map;

public abstract class Equipo {
    protected final String fabricante, modelo, microprocesador;
    protected final Map<String, String> caracteristicas;

    protected Equipo(String fabricante, String modelo, String microprocesador) {
        this.fabricante = fabricante;
        this.modelo = modelo;
        this.microprocesador = microprocesador;
        caracteristicas = new HashMap<>();
    }

    public Map<String, String> mostrar() {
        caracteristicas.put("Fabricante", fabricante);
        caracteristicas.put("Modelo", modelo);
        caracteristicas.put("microprocesador", microprocesador);
        return caracteristicas;
    }
}
