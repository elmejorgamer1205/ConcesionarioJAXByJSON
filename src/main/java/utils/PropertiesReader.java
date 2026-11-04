package utils;

import java.io.FileInputStream;
import java.util.Properties;

/** Carga las rutas y opciones definidas en el fichero config.properties. */
public class PropertiesReader {

    /** Lee la configuración del proyecto; informa por consola si no puede abrirse. */
    public static Properties cargarConfiguracion(){

        Properties config = new Properties();

        try (FileInputStream fis = new FileInputStream("src/config.properties")) {
            config.load(fis);
        } catch (java.io.FileNotFoundException e) {
            System.err.println("Error al leer el archivo de configuración: " + e.getMessage());
        } catch (java.io.IOException e) {
            System.err.println("Error al leer la configuración: " + e.getMessage());
        }

        return config;
    }
}
