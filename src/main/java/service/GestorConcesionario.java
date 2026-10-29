package service;

import utils.PropertiesReader;

import java.io.File;
import java.util.Properties;

public class GestorConcesionario {
    Properties config = PropertiesReader.cargarConfiguracion();
    String rutaXml = config.getProperty("ruta.xml");
    File archivoXML = new File(rutaXml);
}
