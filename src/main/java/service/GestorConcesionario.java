package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.BBDDException;
import exceptions.CocheException;
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


/** Gestiona las operaciones del concesionario y la persistencia en XML y JSON. */
public class GestorConcesionario {
    private Concesionario concesionario;
    private Path ruta;
    private JAXBContext context;
    private final File archivoXML;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private ObjectMapper json;
    private Properties config;

    /**
     * Inicializa el gestor y carga la base de datos configurada.
     * @param ruta directorio base para las rutas configuradas
     * @throws BBDDException si falta o no se puede cargar la configuración o los datos
     * @throws jakarta.xml.bind.JAXBException si no se puede preparar JAXB
     */
    public GestorConcesionario(Path ruta) throws BBDDException, jakarta.xml.bind.JAXBException {
        this.ruta = ruta;
        context = JAXBContext.newInstance(Concesionario.class);
        config = PropertiesReader.cargarConfiguracion();
        json = new ObjectMapper();
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
        unmarshaller = context.createUnmarshaller();
        String rutaXml = config.getProperty("bbdd");

        if(rutaXml == null || rutaXml.trim().isEmpty()){
            throw new BBDDException("La propiedad 'bbdd' no está definida en config.properties");
        }

        this.archivoXML = ruta.resolve(rutaXml).toFile();
        cargarXML();
    }

    /** Devuelve el concesionario actualmente cargado en memoria. */
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
        } catch (jakarta.xml.bind.JAXBException e) {
            throw new BBDDException("Error al cargar la base de datos XML: " + e.getMessage());
        }
    }

    /** Persiste en XML el concesionario en memoria. */
    public void guardarXML() throws BBDDException {
        try {
            JAXBUtil.guardarXML(this.concesionario, this.archivoXML);
        } catch (jakarta.xml.bind.JAXBException e) {
            throw new BBDDException("Error al guardar los datos en el XML: " + e.getMessage());
        }
    }

    private void insertarSinGuardar(Coche c){
        concesionario.getListaCoches().add(c);
    }

    /**
     * Importa coches desde CSV, omitiendo matrículas repetidas, y guarda el XML.
     * @param archivoXML ruta del CSV
     * @return número de coches nuevos importados
     * @throws BBDDException si el fichero no puede leerse o contiene datos inválidos
     */
    public int insertarCsv(Path archivoXML) throws BBDDException{
        String separador = ";";
        String separadorExtra = "\\|";

        try (BufferedReader reader = Files.newBufferedReader(archivoXML)){
            reader.readLine();

            String linea;
            List<Coche> cocheList = new ArrayList<>();

            while ((linea = reader.readLine()) != null){
                if (linea.isBlank()) continue;
                String[] valores = linea.split(separador, -1);
                if (valores.length < 4) throw new BBDDException("Fila CSV incompleta: " + linea);

                String matricula = valores[0].trim();
                String marca = valores[1].trim();
                String modelo = valores[2].trim();
                String extras = valores[3].trim();

                String[] equipamientoString = extras.split(separadorExtra);

                List<Equipamiento> equipamientos = new ArrayList<>();

                for (String s : equipamientoString){
                    if (!s.isBlank()) equipamientos.add(new Equipamiento(s.trim()));
                }

                Coche c1 = new Coche(matricula, marca, modelo,equipamientos);
                if (cocheList.stream().noneMatch(c -> c.getMatricula().equalsIgnoreCase(matricula))
                        && concesionario.getListaCoches().stream().noneMatch(c -> c.getMatricula().equalsIgnoreCase(matricula))) {
                    cocheList.add(c1);
                }
            }
            for (Coche c2 : cocheList){
                insertarSinGuardar(c2);
            }
            guardarXML();
            return cocheList.size();

        }catch (IOException e){
            throw new BBDDException("Error al leer el archivo CSV: " + e.getMessage());
        } catch (CocheException e) {
            throw new BBDDException("Matrícula no válida en el CSV: " + e.getMessage());
        }
    }

    /** Inserta un coche con matrícula única y guarda inmediatamente el XML. */
    public boolean insertar(Coche coche) throws BBDDException {
        if (buscar(coche.getMatricula()) != null) return false;
        insertarSinGuardar(coche);
        guardarXML();
        return true;
    }

    /** Busca un coche por matrícula, sin distinguir mayúsculas. */
    public Coche buscar(String matricula) {
        return concesionario.getListaCoches().stream()
                .filter(c -> c.getMatricula().equalsIgnoreCase(matricula.trim())).findFirst().orElse(null);
    }

    /** Elimina por matrícula y persiste el XML. */
    public boolean borrar(String matricula) throws BBDDException {
        boolean borrado = concesionario.getListaCoches().removeIf(c -> c.getMatricula().equalsIgnoreCase(matricula.trim()));
        if (borrado) guardarXML();
        return borrado;
    }

    /** Modifica marca, modelo y extras sin alterar la matrícula. */
    public boolean modificarCoche(String matricula, String marca, String modelo, List<Equipamiento> extras) throws BBDDException {
        Coche coche = buscar(matricula);
        if (coche == null) return false;
        coche.setMarca(marca);
        coche.setModelo(modelo);
        coche.setExtras(extras);
        guardarXML();
        return true;
    }

    /** Ordena por matrícula con las reglas de comparación españolas y guarda el XML. */
    public void ordenarPorMatricula() throws BBDDException {
        java.text.Collator collator = java.text.Collator.getInstance(new java.util.Locale("es", "ES"));
        concesionario.getListaCoches().sort((a, b) -> collator.compare(a.getMatricula(), b.getMatricula()));
        guardarXML();
    }

    /** Exporta el concesionario completo a JSON. */
    public void exportarBBDDJSON(Path rutaJson) throws BBDDException { exportarJSON(concesionario, rutaJson); }

    /** Exporta a JSON el coche con la matrícula indicada. */
    public boolean exportarCocheJSON(String matricula, Path rutaJson) throws BBDDException {
        Coche coche = buscar(matricula);
        if (coche == null) return false;
        exportarJSON(coche, rutaJson);
        return true;
    }

    private void exportarJSON(Object objeto, Path rutaJson) throws BBDDException {
        try {
            if (rutaJson.toAbsolutePath().getParent() != null) Files.createDirectories(rutaJson.toAbsolutePath().getParent());
            json.writerWithDefaultPrettyPrinter().writeValue(rutaJson.toFile(), objeto);
        } catch (IOException e) { throw new BBDDException("Error al exportar JSON: " + e.getMessage()); }
    }

    /** Sustituye la base de datos por el concesionario leído desde JSON y persiste el XML. */
    public void importarDesdeJSON(Path rutaJson) throws BBDDException {
        try {
            Concesionario nuevo = json.readValue(rutaJson.toFile(), Concesionario.class);
            java.util.Set<String> matriculas = new java.util.HashSet<>();
            if (nuevo.getListaCoches() != null) for (Coche c : nuevo.getListaCoches()) {
                if (c == null || !matriculas.add(c.getMatricula().toUpperCase(java.util.Locale.ROOT)))
                    throw new BBDDException("El JSON contiene coches inválidos o matrículas duplicadas.");
            }
            if (nuevo.getListaCoches() == null) nuevo.setListaCoches(new ArrayList<>());
            concesionario = nuevo;
            guardarXML();
        } catch (IOException e) { throw new BBDDException("Error al leer el JSON del concesionario: " + e.getMessage()); }
    }

    /** Añade un coche leído desde JSON si su matrícula aún no existe. */
    public boolean importarCocheDesdeJSON(Path rutaJson) throws BBDDException {
        try { return insertar(json.readValue(rutaJson.toFile(), Coche.class)); }
        catch (IOException e) { throw new BBDDException("Error al leer el JSON del coche: " + e.getMessage()); }
    }

    /** Genera el informe de total, coches agrupados por marca y extra más frecuente. */
    public void crearResumen() throws BBDDException {
        Path informe = ruta.resolve(config.getProperty("informe"));
        try {
            java.util.Map<String, List<Coche>> porMarca = concesionario.getListaCoches().stream()
                    .collect(java.util.stream.Collectors.groupingBy(Coche::getMarca, java.util.TreeMap::new, java.util.stream.Collectors.toList()));
            java.util.Map<String, Long> frecuencia = concesionario.getListaCoches().stream()
                    .filter(c -> c.getExtras() != null).flatMap(c -> c.getExtras().stream())
                    .map(Equipamiento::getNombre).collect(java.util.stream.Collectors.groupingBy(x -> x, java.util.stream.Collectors.counting()));
            String masRepetido = frecuencia.entrySet().stream().max(java.util.Map.Entry.comparingByValue())
                    .map(e -> e.getKey() + " (" + e.getValue() + " veces)").orElse("No hay extras");
            StringBuilder texto = new StringBuilder("Número total de coches: ").append(concesionario.getListaCoches().size()).append("\n\nCoches por marca:\n");
            porMarca.forEach((marca, coches) -> { texto.append("\n").append(marca).append(":\n"); coches.forEach(c -> texto.append(" - ").append(c.getMatricula()).append(" ").append(c.getModelo()).append("\n")); });
            texto.append("\nExtra más repetido: ").append(masRepetido).append('\n');
            if (informe.toAbsolutePath().getParent() != null) Files.createDirectories(informe.toAbsolutePath().getParent());
            Files.writeString(informe, texto.toString());
        } catch (IOException e) { throw new BBDDException("Error al escribir el informe: " + e.getMessage()); }
    }
}
