package service;

import exceptions.BBDDException;
import models.Concesionario;
import utils.JAXBUtil;
import utils.PropertiesReader;

import java.io.File;
import java.util.Properties;

public class GestorConcesionario {
    private Concesionario concesionario;
    private final File archivoXML;

    public GestorConcesionario() throws BBDDException {
        Properties config = PropertiesReader.cargarConfiguracion();
        String rutaXml = config.getProperty("concesionario_plantilla.xml");

        if(rutaXml == null || rutaXml.trim().isEmpty()){
            throw new BBDDException("La propiedad 'concesionario_plantilla.xml' no está definidad en config.properties");
        }

        this.archivoXML = new File(rutaXml);
        cargarXML();
    }

    public Concesionario getConcesionario() {
        return concesionario;
    }

    /**
     * Carga el estado del concesionario desde el archivo XML en memoria.
     *
     * @throws BBDDException Si ocurre un error de lectura o parseo del XML.
     */
    public void cargarXML() throws BBDDException {
        try {
            if (archivoXML.exists()) {
                this.concesionario = JAXBUtil.cargarXML(archivoXML);
            } else {
                this.concesionario = new Concesionario();
            }
        } catch (Exception e) {
            throw new BBDDException("Error al cargar la base de datos XML: " + e.getMessage());
        }
    }

    public void guardarXML() throws BBDDException {
        try {
            JAXBUtil.guardarXML(this.concesionario, this.archivoXML);
        } catch (Exception e) {
            throw new BBDDException("Error al guardar los datos en el XML: " + e.getMessage());
        }
    }
}
