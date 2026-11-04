package utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import models.Concesionario;

import java.io.File;

/** Funciones auxiliares para leer y escribir el modelo del concesionario en XML. */
public class JAXBUtil {

    /**
     * Deserializa un fichero XML y devuelve el concesionario contenido.
     * @param archivoXML fichero XML de entrada
     * @return concesionario leído
     * @throws JAXBException si el XML no puede convertirse al modelo
     */
    public static Concesionario cargarXML(File archivoXML) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Concesionario.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Concesionario) unmarshaller.unmarshal(archivoXML);
    }

    /**
     * Serializa el concesionario en el fichero XML indicado.
     * @param concesionario datos que se van a guardar
     * @param archivoXML destino XML
     * @throws JAXBException si no se puede serializar el modelo
     */
    public static void guardarXML(Concesionario concesionario, File archivoXML) throws JAXBException{
        JAXBContext context = JAXBContext.newInstance(Concesionario.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(concesionario, archivoXML);
    }
}
