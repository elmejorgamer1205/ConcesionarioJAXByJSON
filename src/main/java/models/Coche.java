package models;

import exceptions.CocheException;
import jakarta.xml.bind.annotation.*;

import java.util.List;

/** Representa un coche y sus datos mapeados al XML del concesionario. */
@XmlAccessorType(XmlAccessType.FIELD)
public class Coche {
    @XmlElement(name = "matricula-coche")
    private String matricula;
    @XmlAttribute
    private int id;
    @XmlTransient
    private static int idTotal = 1;
    private String marca;
    private String modelo;
    @XmlElementWrapper(name = "equipamiento")
    @XmlElement(name = "extra")
    private List<Equipamiento> extras;

    /** Crea un coche con todos sus datos. */
    public Coche(String matricula, String marca, String modelo, List<Equipamiento> extras) throws CocheException {
        setMatricula(matricula);
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.id = idTotal++;
    }

    /** Constructor vacío requerido por JAXB y Jackson. */
    public Coche() {
    }

    /** Devuelve la matrícula, clave del coche en el concesionario. */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Cambia la matrícula tras validar su longitud.
     * @param matricula matrícula que se asignará
     * @throws CocheException si supera los límites admitidos de letras o números
     */
    public void setMatricula(String matricula) throws CocheException {
        StringBuilder letras = new StringBuilder();
        StringBuilder numeros = new StringBuilder();
        for(char c : matricula.toCharArray())
            if(Character.isLetter(c)){
                letras.append(c);
            }else if(Character.isDigit(c)){
                numeros.append(c);
            }

        if(numeros.length() <= 4 && letras.length() <= 3){
            this.matricula = matricula;
        }else {
            throw new CocheException("No se puede añadir una matricula que tenga más de 3 letras o 4 números");
        }
    }

    /** Devuelve la marca del coche. */
    public String getMarca() {
        return marca;
    }

    /** Asigna la marca del coche. */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /** Devuelve el modelo del coche. */
    public String getModelo() {
        return modelo;
    }

    /** Asigna el modelo del coche. */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /** Devuelve la lista de extras del coche. */
    public List<Equipamiento> getExtras() {
        return extras;
    }

    /** Sustituye la lista de extras del coche. */
    public void setExtras(List<Equipamiento> extras) {
        this.extras = extras;
    }

    /** Devuelve el identificador interno del coche. */
    public int getId() {
        return id;
    }

    /** Actualiza el identificador interno del vehículo. */
    public void setId(int id) {
        this.id = id;
    }
}
