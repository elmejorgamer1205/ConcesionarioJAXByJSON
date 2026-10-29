package utils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import models.Concesionario;

import java.io.File;

public class JAXBUtil {

    public static Concesionario cargarXML(File archivoXML) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Concesionario.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.unmarshal(archivoXML);
        return (Concesionario) unmarshaller.unmarshal(archivoXML);
    }

    public static void guardarXML(Concesionario concesionario, File archivoXML) throws JAXBException{
        JAXBContext context = JAXBContext.newInstance(Concesionario.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(concesionario, archivoXML);
    }
}
