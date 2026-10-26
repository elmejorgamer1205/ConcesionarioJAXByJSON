package models;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "concesionario-de-coches")
@XmlAccessorType(XmlAccessType.FIELD)
public class Concesionario {

    String nombre;

    @XmlElementWrapper(name = "listado-coches>")
    private List<Coche> listaCoches;

    public Concesionario() {
        this.listaCoches = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Coche> getListaCoches(){
        return listaCoches;
    }

    public void setListaCoches(List<Coche> listaCoches){
        this.listaCoches = listaCoches;
    }
}

