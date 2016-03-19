package Comunicacion;

import java.io.Serializable;

public class Mensaje implements Serializable {

    private String tipo;
    private String nombre, contra;
    private String edad, carrera;
    static final long serialVersionUID = 42L;

    public  Mensaje(String tipo, String nombre, String contra, String carrera, String edad){
        this.tipo = tipo;
        this.contra = contra;
        this.nombre = nombre;
        this.carrera=carrera;
        this.edad=edad;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContra() {
        return contra;
    }

    public String getEdad() {
        return edad;
    }

    public String getCarrera() {
        return carrera;
    }
}
