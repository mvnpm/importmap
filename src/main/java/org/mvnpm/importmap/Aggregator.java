package org.mvnpm.importmap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.mvnpm.importmap.model.Imports;

/**
 * Scans the classpath and create an aggregation of all generated import maps
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 * TODO: Add support for use supplied import map to be merged in
 */
public class Aggregator {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> userProvidedImports = new HashMap<>();
    private final Set<URL> userProvidedJarUrls = new HashSet<>();
    
    public Aggregator(){}
    
    public Aggregator(Set<URL> urls){
        this.userProvidedJarUrls.addAll(urls);
    }
    
    public Aggregator(Map<String, String> importMapings){
        this.userProvidedImports.putAll(importMapings);
    }
    
    public Aggregator(Set<URL> urls, Map<String, String> importMapings){
        this.userProvidedJarUrls.addAll(urls);
        this.userProvidedImports.putAll(importMapings);
    }
    
    public void addMapping(String key, String value){
        userProvidedImports.put(key, value);
    }
    
    public void addMappings(Map<String, String> all){
        userProvidedImports.putAll(all);
    }
    
    public void addJarUrl(URL url){
        userProvidedJarUrls.add(url);
    }
    
    public void addJarUrls(Set<URL> urls){
        userProvidedJarUrls.addAll(urls);
    }
    
    public Imports aggregate() {
        Map<String, String> allimports = new HashMap<>();
        allimports.putAll(userProvidedImports);
        allimports.putAll(scanUserProviderUrls());
        allimports.putAll(scanClassPath()); // TODO: Add boolen to exclude this ?
        return new Imports(allimports);
    }
    
    public String aggregateAsJson(){
        try {
            Imports i = aggregate();
            return this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(i);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void reset(){
        this.userProvidedImports.clear();
        this.userProvidedJarUrls.clear();
    }
    
    private Map<String,String> scanUserProviderUrls(){
        if(!userProvidedJarUrls.isEmpty()){
            URLClassLoader urlClassLoader = new URLClassLoader(userProvidedJarUrls.toArray(new URL[] {}));
            try {
                Enumeration<URL> enumer = urlClassLoader.getResources(Location.IMPORTMAP_PATH);
                Map<String,String> m = new HashMap<>();
                while (enumer.hasMoreElements()) {
                    URL importmapFile = enumer.nextElement();
                    Imports importForPackage = objectMapper.readValue(importmapFile, Imports.class);
                    m.putAll(importForPackage.getImports());
                }
                return m;
            }catch (IOException ex) {
                throw new UncheckedIOException("Could not aggregate importmaps from set of urls", ex);
            }
        }
        return Map.of();
    }
    
    private Map<String,String> scanClassPath(){
        try {
            Enumeration<URL> enumer = Thread.currentThread().getContextClassLoader().getResources(Location.IMPORTMAP_PATH);
            Map<String,String> m = new HashMap<>();
            while (enumer.hasMoreElements()) {
                URL importmapFile = enumer.nextElement(); 
                Imports importForPackage = objectMapper.readValue(importmapFile, Imports.class);
                m.putAll(importForPackage.getImports());
            }
            return m;
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not aggregate importmaps from classpath", ex);
        }
    }
}
