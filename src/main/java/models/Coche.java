package models;

import exceptions.CocheException;

import java.util.Collections;
import java.util.List;

public class Coche {
    private String matricula;
    private String marca;
    private String modelo;
    private List extras;
    StringBuilder letras = new StringBuilder();
    StringBuilder numeros = new StringBuilder();

    public Coche(String matricula, String marca, String modelo, List extras) throws CocheException {
        setMatricula(matricula);
        this.marca = marca;
        this.modelo = modelo;
        this.extras = extras;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) throws CocheException {
        for(char c : matricula.toCharArray())
            if(Character.isLetter(c)){
                letras.append(c);
            }else if(Character.isDigit(c)){
                numeros.append(c);
            }

        if(numeros.length() <= 4 && letras.length() <= 3){
            this.matricula = matricula;
        }else {
            throw new CocheException("No puedes añadir una matricula que tenga más de 3 letras o 4 números");
        }

        this.matricula = matricula;
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

    public String getExtras() {
        return extras.toString();
    }

    public void setExtras(String extras) {
        this.extras = Collections.singletonList(extras);
    }


}
