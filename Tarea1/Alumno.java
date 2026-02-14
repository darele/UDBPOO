public class Alumno {
    private final String carnet, nombre;
    public Alumno(String carnet, String nombre) {
        this.carnet = carnet;
        this.nombre = nombre;
    }

    public String getCarnet() {
        return carnet;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return carnet + " " + nombre;
    }
}
