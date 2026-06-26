package io.mvnpm.importmap;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AggregatorTest {
    
    public AggregatorTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testAggregateAsJson() throws JacksonException{
        Aggregator aggregator = new Aggregator();
        String importmap = aggregator.aggregateAsJson();
        
        System.out.println(importmap);
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, String>> actualMap = mapper.readValue(importmap, new TypeReference<>() {});

        assertEquals("/_static/at/lit-labs/labs/ssr-dom-shim/1.6.0/", actualMap.get("imports").get("@lit-labs/ssr-dom-shim/"));
        assertEquals("/_static/trusted-types/2.0.7/index.js", actualMap.get("imports").get("@types/trusted-types"));
        assertEquals("/_static/lit-element/4.0.6/index.js", actualMap.get("imports").get("lit-element"));
        assertEquals("/_static/reactive-element/2.1.2/reactive-element.js", actualMap.get("imports").get("@lit/reactive-element"));
        assertEquals("/_static/lit/3.1.4/index.js", actualMap.get("imports").get("lit"));
        assertEquals("/_static/lit-element/4.0.6/", actualMap.get("imports").get("lit-element/"));
        assertEquals("/_static/lit-html/3.1.4/", actualMap.get("imports").get("lit-html/"));
        assertEquals("/_static/trusted-types/2.0.7/", actualMap.get("imports").get("@types/trusted-types/"));
        assertEquals("/_static/lit/3.1.4/", actualMap.get("imports").get("lit/"));
        assertEquals("/_static/reactive-element/2.1.2/", actualMap.get("imports").get("@lit/reactive-element/"));
        assertEquals("/_static/lit-html/3.1.4/lit-html.js", actualMap.get("imports").get("lit-html"));
        assertEquals("/_static/at/lit-labs/labs/ssr-dom-shim/1.6.0/index.js", actualMap.get("imports").get("@lit-labs/ssr-dom-shim"));
    }
    
    
    @Test
    public void testAggregateAsJs() throws JacksonException{
        Aggregator aggregator = new Aggregator();
        String importjs = aggregator.aggregateAsJs();
        
        System.out.println(importjs);
        
        assertTrue(importjs.contains("/_static/at/lit-labs/labs/ssr-dom-shim/1.6.0/"));
        assertTrue(importjs.contains("/_static/trusted-types/2.0.7/index.js"));
        assertTrue(importjs.contains("/_static/lit-element/4.0.6/index.js"));
        assertTrue(importjs.contains("/_static/reactive-element/2.1.2/reactive-element.js"));
        assertTrue(importjs.contains("/_static/lit/3.1.4/index.js"));
        assertTrue(importjs.contains("/_static/lit-element/4.0.6/"));
        assertTrue(importjs.contains("/_static/lit-html/3.1.4/"));
        assertTrue(importjs.contains("/_static/trusted-types/2.0.7/"));
        assertTrue(importjs.contains("/_static/lit/3.1.4/"));
        assertTrue(importjs.contains("/_static/reactive-element/2.1.2/"));
        assertTrue(importjs.contains("/_static/lit-html/3.1.4/lit-html.js"));
        assertTrue(importjs.contains("/_static/at/lit-labs/labs/ssr-dom-shim/1.6.0/index.js"));
    }
    
    @Test
    public void testAggregateAsJsWithRoot() throws JacksonException{
        Aggregator aggregator = new Aggregator();
        String importjs = aggregator.aggregateAsJs("/node_modules");
        
        System.out.println(importjs);
        
        assertTrue(importjs.contains("/node_modules/_static/at/lit-labs/labs/ssr-dom-shim/1.6.0/"));
        assertTrue(importjs.contains("/node_modules/_static/trusted-types/2.0.7/index.js"));
        assertTrue(importjs.contains("/node_modules/_static/lit-element/4.0.6/index.js"));
        assertTrue(importjs.contains("/node_modules/_static/reactive-element/2.1.2/reactive-element.js"));
        assertTrue(importjs.contains("/node_modules/_static/lit/3.1.4/index.js"));
        assertTrue(importjs.contains("/node_modules/_static/lit-element/4.0.6/"));
        assertTrue(importjs.contains("/node_modules/_static/lit-html/3.1.4/"));
        assertTrue(importjs.contains("/node_modules/_static/trusted-types/2.0.7/"));
        assertTrue(importjs.contains("/node_modules/_static/lit/3.1.4/"));
        assertTrue(importjs.contains("/node_modules/_static/reactive-element/2.1.2/"));
        assertTrue(importjs.contains("/node_modules/_static/lit-html/3.1.4/lit-html.js"));
        assertTrue(importjs.contains("/node_modules/_static/at/lit-labs/labs/ssr-dom-shim/1.6.0/index.js"));
    }
}
