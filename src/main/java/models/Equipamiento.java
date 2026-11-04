package models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlValue;

/** Representa un elemento de equipamiento asociado a un coche. */
@XmlAccessorType(XmlAccessType.FIELD)
public class Equipamiento {

    @XmlValue
    private String nombre;

    /** Crea un extra con el nombre indicado. */
    public Equipamiento(String nombre) {
        this.nombre = nombre;
    }

    /** Constructor vacío requerido por JAXB y Jackson. */
    public Equipamiento() {
    }

    /** Devuelve el nombre del extra. */
    public String getNombre() { return nombre; }

    /** Actualiza el nombre del extra. */
    public void setNombre(String nombre) { this.nombre = nombre; }
}
