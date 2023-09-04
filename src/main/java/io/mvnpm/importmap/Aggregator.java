package io.mvnpm.importmap;

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
import io.mvnpm.importmap.model.Imports;

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
        return aggregate(true);
    }
    
    public Imports aggregate(boolean scanClassPath) {
        return aggregate("", scanClassPath);
    }
    
    public Imports aggregate(String root){
        return aggregate(root, true);
    }
    
    public Imports aggregate(String root, boolean scanClassPath) {
        if(root.endsWith("/")){
            root = root.substring(0, root.length()-1);
        }
        
        Map<String, String> allimports = new HashMap<>();
        allimports.putAll(userProvidedImports);
        allimports.putAll(scanUserProviderUrls(root));
        if(scanClassPath)allimports.putAll(scanClassPath(root));
        return new Imports(allimports);
    }
    
    public String aggregateAsJson(){
        return aggregateAsJson(true);
    }
    
    public String aggregateAsJson(boolean scanClassPath){
        return aggregateAsJson("", scanClassPath);
    }
    
    public String aggregateAsJson(String root){
        return aggregateAsJson(root, true);
    }
    
    public String aggregateAsJson(String root, boolean scanClassPath){
        try {
            Imports i = aggregate(root, scanClassPath);
            return this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(i);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void reset(){
        this.userProvidedImports.clear();
        this.userProvidedJarUrls.clear();
    }
    
    private Map<String,String> scanUserProviderUrls(String root){
        if(!userProvidedJarUrls.isEmpty()){
            URLClassLoader urlClassLoader = new URLClassLoader(userProvidedJarUrls.toArray(new URL[] {}));
            try {
                Enumeration<URL> enumer = urlClassLoader.getResources(Location.IMPORTMAP_PATH);
                Map<String,String> m = new HashMap<>();
                while (enumer.hasMoreElements()) {
                    URL importmapFile = enumer.nextElement();
                    Imports importsForPackage = objectMapper.readValue(importmapFile, Imports.class);
                    Map<String, String> importForPackage = importsForPackage.getImports();
                    for(Map.Entry<String, String> kv:importForPackage.entrySet()){
                        m.put(kv.getKey(), root + kv.getValue());
                    }
                }
                return m;
            }catch (IOException ex) {
                throw new UncheckedIOException("Could not aggregate importmaps from set of urls", ex);
            }
        }
        return Map.of();
    }
    
    private Map<String,String> scanClassPath(String root){
        try {
            Enumeration<URL> enumer = Thread.currentThread().getContextClassLoader().getResources(Location.IMPORTMAP_PATH);
            Map<String,String> m = new HashMap<>();
            while (enumer.hasMoreElements()) {
                URL importmapFile = enumer.nextElement(); 
                Imports importsForPackage = objectMapper.readValue(importmapFile, Imports.class);
                Map<String, String> importForPackage = importsForPackage.getImports();
                for(Map.Entry<String, String> kv:importForPackage.entrySet()){
                    m.put(kv.getKey(), root + kv.getValue());
                }
            }
            return m;
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not aggregate importmaps from classpath", ex);
        }
    }
}
