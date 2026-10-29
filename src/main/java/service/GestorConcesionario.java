package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.BBDDException;
import exceptions.CocheException;
import exceptions.JAXBException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import models.Coche;
import models.Concesionario;
import models.Equipamiento;
import utils.JAXBUtil;
import utils.PropertiesReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static jakarta.xml.bind.JAXB.unmarshal;

public class GestorConcesionario {
    private Concesionario concesionario;
    private Path ruta;
    private JAXBContext context;
    private final File archivoXML;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private Gson gson;

    public GestorConcesionario(Path ruta) throws BBDDException, jakarta.xml.bind.JAXBException {
        this.ruta = ruta;
        context = JAXBContext.newInstance(Concesionario.class);
        Properties config = PropertiesReader.cargarConfiguracion();
        gson = new GsonBuilder().setPrettyPrinting().create();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
        unmarshaller = context.createUnmarshaller();
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

    private void insertar(Coche c){
        concesionario.getListaCoches().add(c);
    }

    public void insertarCsv(Path archivoXML) throws BBDDException{
        String separador = ",";
        String separadorExtra = "\\|";

        try (BufferedReader reader = Files.newBufferedReader(archivoXML)){
            reader.readLine();

            String linea;
            List<Coche> cocheList = new ArrayList<>();

            while ((linea = reader.readLine()) != null){
                String[] valores = linea.split(separador);

                String matricula = valores[0];
                String marca = valores[1];
                String modelo = valores[2];
                String extras = valores[3];

                String[] equipamientoString = extras.split(separadorExtra);

                List<Equipamiento> equipamientos = new ArrayList<>();

                for (String s : equipamientoString){
                    equipamientos.add(new Equipamiento(s));
                }

                Coche c1 = new Coche(matricula, marca, modelo,equipamientos);
            }
            for (Coche c2 : cocheList){
                insertar(c2);
            }

        }catch (IOException e){
            throw new BBDDException("Error al leer el archivo CSV: " + e.getMessage());
        } catch (CocheException e) {
            throw new RuntimeException(e);
        }
    }
}
