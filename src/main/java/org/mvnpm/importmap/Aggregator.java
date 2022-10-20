package org.mvnpm.importmap;

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
    
    private Aggregator(){}
    
    public static Imports aggregate() {
        Map<String, String> allimports = new HashMap<>();
        try {
            Enumeration<URL> enumer = Thread.currentThread().getContextClassLoader().getResources(Location.IMPORTMAP_PATH);
            while (enumer.hasMoreElements()) {
                URL importmapFile = enumer.nextElement(); 
                Imports importForPackage = objectMapper.readValue(importmapFile, Imports.class);
                allimports.putAll(importForPackage.imports());
            }
            
            return new Imports(allimports);
        } catch (IOException ex) {
            throw new RuntimeException("Could not aggregate importmaps from classpath", ex);
        }
    }
}
