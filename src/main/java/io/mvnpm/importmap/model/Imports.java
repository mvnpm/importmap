package io.mvnpm.importmap.model;

import java.util.Map;

public class Imports{
    private Map<String,String> imports;

    public Imports(){
        
    }
            
    public Imports(Map<String,String> imports){
        this.imports = imports;
    }     

    public Map<String, String> getImports() {
        return imports;
    }

    public void setImports(Map<String, String> imports) {
        this.imports = imports;
    }
}
