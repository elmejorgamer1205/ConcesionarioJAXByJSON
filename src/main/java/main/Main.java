package main;

import exceptions.BBDDException;
import jakarta.xml.bind.JAXBException;
import models.Coche;
import models.Equipamiento;
import service.GestorConcesionario;
import utils.PropertiesReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/** Punto de entrada de la aplicación del concesionario. */
public class Main {
    /** Evita crear instancias: todos los métodos de la clase son estáticos. */
    private Main() { }

    /**
     * Carga la configuración, prepara el XML si aún no existe e inicia el menú.
     * @param args argumentos de ejecución, no utilizados
     */
    public static void main(String[] args) {
        // Las rutas de los ficheros se obtienen de config.properties.
        Properties propiedades = PropertiesReader.cargarConfiguracion();
        try {
            Path rutaBbdd = Path.of(requerida(propiedades, "bbdd"));
            inicializarBBDD(rutaBbdd, Path.of(requerida(propiedades, "plantilla")));
            // El gestor carga el XML y se ocupa de persistir cada modificación.
            GestorConcesionario gestor = new GestorConcesionario(Path.of("."));
            ejecutarMenu(gestor, propiedades);
        } catch (IOException | BBDDException | JAXBException e) {
            System.out.println("No se pudo iniciar la aplicación: " + e.getMessage());
        }
    }

    /**
     * Muestra las opciones disponibles y delega cada operación al gestor.
     * @param gestor acceso a la base de datos y a las operaciones del concesionario
     * @param propiedades rutas configuradas para las importaciones y exportaciones
     */
    private static void ejecutarMenu(GestorConcesionario gestor, Properties propiedades) {
        try (Scanner scanner = new Scanner(System.in)) {
            int opcion;
            do {
                System.out.println("\n=== CONCESIONARIO ===");
                System.out.println("1. Insertar coche");
                System.out.println("2. Importar coches desde CSV");
                System.out.println("3. Ordenar coches por matrícula");
                System.out.println("4. Borrar coche");
                System.out.println("5. Modificar coche");
                System.out.println("6. Exportar concesionario a JSON");
                System.out.println("7. Exportar coche a JSON");
                System.out.println("8. Importar concesionario desde JSON");
                System.out.println("9. Importar coche desde JSON");
                System.out.println("10. Crear resumen");
                System.out.println("0. Salir");
                opcion = leerOpcion(scanner);
                try {
                    // Cada opción corresponde a una de las operaciones del enunciado.
                    switch (opcion) {
                        case 1 -> insertarCoche(gestor, scanner);
                        case 2 -> {
                            Path csv = leerRuta(scanner, "Ruta del CSV (Enter para usar la configurada): ", propiedades.getProperty("csv"));
                            System.out.println("Coches importados: " + gestor.insertarCsv(csv));
                        }
                        case 3 -> { gestor.ordenarPorMatricula(); System.out.println("Coches ordenados."); }
                        case 4 -> System.out.println(gestor.borrar(leerLinea(scanner, "Matrícula a borrar: ")) ? "Coche borrado." : "No se encontró la matrícula.");
                        case 5 -> modificarCoche(gestor, scanner);
                        case 6 -> gestor.exportarBBDDJSON(leerRuta(scanner, "JSON de salida (Enter para usar la configurada): ", propiedades.getProperty("jsonExportacionBBDD")));
                        case 7 -> {
                            String matricula = leerLinea(scanner, "Matrícula del coche: ");
                            Path salida = leerRuta(scanner, "JSON de salida (Enter para usar la configurada): ", propiedades.getProperty("jsonExportacionCoche"));
                            System.out.println(gestor.exportarCocheJSON(matricula, salida) ? "Coche exportado." : "No se encontró la matrícula.");
                        }
                        case 8 -> gestor.importarDesdeJSON(leerRuta(scanner, "JSON del concesionario (Enter para usar la configurada): ", propiedades.getProperty("jsonConcesionario")));
                        case 9 -> System.out.println(gestor.importarCocheDesdeJSON(leerRuta(scanner, "JSON del coche (Enter para usar la configurada): ", propiedades.getProperty("jsonCoche"))) ? "Coche importado." : "Matrícula duplicada; no se importó.");
                        case 10 -> { gestor.crearResumen(); System.out.println("Informe generado."); }
                        case 0 -> System.out.println("Hasta luego.");
                        default -> System.out.println("Opción no válida.");
                    }
                } catch (BBDDException | exceptions.CocheException | java.nio.file.InvalidPathException e) {
                    System.out.println("No se pudo completar la operación: " + e.getMessage());
                }
            } while (opcion != 0);
        }
    }

    /**
     * Lee una opción numérica y repite la solicitud mientras esté fuera del menú.
     * @param scanner lector asociado a la entrada de consola
     * @return opción comprendida entre 0 y 10
     */
    private static int leerOpcion(Scanner scanner) {
        while (true) {
            String entrada = leerLinea(scanner, "Elige una opción: ").trim();
            try {
                int opcion = Integer.parseInt(entrada);
                if (opcion >= 0 && opcion <= 10) return opcion;
            } catch (NumberFormatException ignored) {
                // Se vuelve a solicitar una opción numérica válida.
            }
            System.out.println("Introduce un número entre 0 y 10.");
        }
    }

    /**
     * Elige la ruta escrita por el usuario o, si dejó el campo vacío, la configurada.
     * @param rutaIntroducida ruta opcional escrita en el menú
     * @param rutaConfigurada ruta por defecto procedente de config.properties
     * @return ruta que debe usar la operación
     */
    private static Path resolverRuta(String rutaIntroducida, String rutaConfigurada) {
        String ruta = rutaIntroducida.isEmpty() ? rutaConfigurada : rutaIntroducida;
        if (ruta == null || ruta.isBlank()) throw new java.nio.file.InvalidPathException("", "No hay una ruta configurada");
        return Path.of(ruta);
    }

    /** Solicita una ruta y aplica la ruta configurada cuando la respuesta está vacía. */
    private static Path leerRuta(Scanner scanner, String mensaje, String configurada) {
        return resolverRuta(leerLinea(scanner, mensaje).trim(), configurada);
    }

    /**
     * Obtiene una propiedad obligatoria de configuración.
     * @param propiedades propiedades cargadas desde config.properties
     * @param clave nombre de la propiedad que se necesita
     * @return valor no vacío de la propiedad
     * @throws IOException si falta la propiedad o no tiene contenido
     */
    private static String requerida(Properties propiedades, String clave) throws IOException {
        String valor = propiedades.getProperty(clave);
        if (valor == null || valor.isBlank()) throw new IOException("Falta la propiedad '" + clave + "' en config.properties.");
        return valor.trim();
    }

    /** Recoge los campos de un coche y lo inserta si la matrícula no está duplicada. */
    private static void insertarCoche(GestorConcesionario gestor, Scanner scanner) throws BBDDException, exceptions.CocheException {
        String matricula = leerLinea(scanner, "Matrícula: ").trim();
        String marca = leerLinea(scanner, "Marca: ").trim();
        String modelo = leerLinea(scanner, "Modelo: ").trim();
        Coche coche = new Coche(matricula, marca, modelo, leerEquipamiento(scanner));
        System.out.println(gestor.insertar(coche) ? "Coche insertado." : "Ya existe esa matrícula.");
    }

    /** Modifica marca, modelo y extras; la matrícula se utiliza solo para localizar el coche. */
    private static void modificarCoche(GestorConcesionario gestor, Scanner scanner) throws BBDDException {
        String matricula = leerLinea(scanner, "Matrícula del coche que quieres modificar: ").trim();
        String marca = leerLinea(scanner, "Nueva marca: ").trim();
        String modelo = leerLinea(scanner, "Nuevo modelo: ").trim();
        boolean modificado = gestor.modificarCoche(matricula, marca, modelo, leerEquipamiento(scanner));
        System.out.println(modificado ? "Coche modificado." : "No se encontró la matrícula.");
    }

    /** Solicita cero o más extras y devuelve la lista construida. */
    private static List<Equipamiento> leerEquipamiento(Scanner scanner) {
        List<Equipamiento> extras = new ArrayList<>();
        while (true) {
            String respuesta = leerLinea(scanner, "¿Añadir un extra? (S/N): ").trim();
            if (respuesta.equalsIgnoreCase("N")) return extras;
            if (respuesta.equalsIgnoreCase("S")) {
                String nombre = leerLinea(scanner, "Nombre del extra: ").trim();
                if (!nombre.isEmpty()) extras.add(new Equipamiento(nombre));
                else System.out.println("El nombre del extra no puede estar vacío.");
            } else System.out.println("Respuesta no válida; escribe S o N.");
        }
    }

    /** Muestra un mensaje y devuelve la línea completa que escribe el usuario. */
    private static String leerLinea(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    /**
     * Copia la plantilla configurada si todavía no existe el XML de datos.
     * @param rutaBbdd destino del fichero XML que usará el gestor
     * @param plantilla XML inicial del proyecto
     * @throws IOException si la plantilla no existe o no se puede copiar
     */
    private static void inicializarBBDD(Path rutaBbdd, Path plantilla) throws IOException {
        if (Files.exists(rutaBbdd)) return;
        if (!Files.exists(plantilla)) throw new IOException("No se encontró la plantilla XML: " + plantilla);
        // Se prepara el directorio padre antes de copiar la plantilla.
        Path directorio = rutaBbdd.toAbsolutePath().getParent();
        if (directorio != null) Files.createDirectories(directorio);
        Files.copy(plantilla, rutaBbdd);
    }
}
