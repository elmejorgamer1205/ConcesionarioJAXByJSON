package utils;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesReader {

    public static Properties cargarConfiguracion(){

        Properties config = new Properties();

        // El bloque try "intenta" ejecutar un código que podría fallar
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            // Si encuentra el archivo, carga todas las rutas en nuestro objeto config
            config.load(fis);
        } catch (Exception e) {
            // Si el archivo no existe o hay un error, el programa no se "rompe", sino que entra aquí
            System.err.println("Error al leer el archivo de configuración: " + e.getMessage());
        }

        return config;
    }
}
