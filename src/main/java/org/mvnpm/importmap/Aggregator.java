package org.mvnpm.importmap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.mvnpm.importmap.model.Imports;

/**
 * Scans the classpath and create an aggregation of all generated import maps
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 * TODO: Add support for use supplied import map to be merged in
 */
public class Aggregator {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final static Map<String, String> userProvidedImports = new HashMap<>();
    private final static Map<String, String> discoveredImports = new HashMap<>();
    
    private Aggregator(){}
    
    public static Imports aggregate() {
        Map<String, String> allimports = new HashMap<>();
        allimports.putAll(discoveredImports);
        allimports.putAll(userProvidedImports);
        return new Imports(allimports);
    }
    
    public static String aggregateAsJson(){
        try {
            Imports i = Aggregator.aggregate();
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(i);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void add(String key, String value){
        userProvidedImports.put(key, value);
    }
    
    public static void add(Map<String, String> all){
        userProvidedImports.putAll(all);
    }
    
    static {
        try {
            Enumeration<URL> enumer = Thread.currentThread().getContextClassLoader().getResources(Location.IMPORTMAP_PATH);
            while (enumer.hasMoreElements()) {
                URL importmapFile = enumer.nextElement(); 
                Imports importForPackage = objectMapper.readValue(importmapFile, Imports.class);
                discoveredImports.putAll(importForPackage.getImports());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not aggregate importmaps from classpath", ex);
        }
    }
}
