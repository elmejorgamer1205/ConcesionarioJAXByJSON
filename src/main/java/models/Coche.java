package models;

import exceptions.CocheException;
import jakarta.xml.bind.annotation.*;

import java.util.List;

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

    public Coche(String matricula, String marca, String modelo, List<Equipamiento> extras) throws CocheException {
        setMatricula(matricula);
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
        this.id = idTotal++;
    }

    public Coche() {
    }

    public String getMatricula() {
        return matricula;
    }

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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public List<Equipamiento> getExtras() {
        return extras;
    }

    public void setExtras(List<Equipamiento> extras) {
        this.extras = extras;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }
}
