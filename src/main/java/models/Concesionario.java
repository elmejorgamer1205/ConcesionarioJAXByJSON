package models;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/** Representa el concesionario y la colección de coches almacenada en XML. */
@XmlRootElement(name = "concesionario-de-coches")
@XmlAccessorType(XmlAccessType.FIELD)
public class Concesionario {

    String nombre;

    @XmlElementWrapper(name = "listado-coches")
    @XmlElement(name = "coche")
    private List<Coche> listaCoches;

    /** Inicializa el concesionario con una lista vacía de coches. */
    public Concesionario() {
        this.listaCoches = new ArrayList<>();
    }

    /** Devuelve el nombre del concesionario. */
    public String getNombre() {
        return nombre;
    }

    /** Asigna el nombre del concesionario. */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    /** Devuelve la lista de coches del concesionario. */
    public List<Coche> getListaCoches(){
        return listaCoches;
    }

    /** Reemplaza la lista de coches del concesionario. */
    public void setListaCoches(List<Coche> listaCoches){
        this.listaCoches = listaCoches;
    }
}

