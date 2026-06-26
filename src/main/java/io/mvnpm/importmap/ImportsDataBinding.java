package io.mvnpm.importmap;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import io.mvnpm.importmap.model.Imports;

/**
 * Object to json binding
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public class ImportsDataBinding {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    
    private ImportsDataBinding(){}
    
    public static Imports toImports(String json) {
        try {
            return objectMapper.readValue(json, Imports.class);
        } catch (JacksonException ex) {
            throw new RuntimeException("Error while binding to Imports Object. Json = [" + json + "]", ex);
        }
    }
    
    public static String toJson(Imports imports){
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(imports);
        } catch (JacksonException ex) {
            throw new RuntimeException("Error while binding to Json. Imports Object = [" + imports + "]", ex);
        }
    }
    
}
