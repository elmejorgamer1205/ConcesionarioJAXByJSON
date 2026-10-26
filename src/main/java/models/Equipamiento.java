package models;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Equipamiento {

    @XmlValue
    private String nombre;

    public Equipamiento(String nombre) {
        this.nombre = nombre;
    }

    public Equipamiento() {
    }
}
