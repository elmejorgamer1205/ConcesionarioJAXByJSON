package models;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "concesionario-de-coches")
@XmlAccessorType(XmlAccessType.FIELD)
public class Concesionario {

    String nombre;

    @XmlElementWrapper(name = "listado-coches")
    @XmlElement(name = "coche")
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

