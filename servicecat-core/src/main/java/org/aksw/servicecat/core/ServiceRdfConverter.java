package org.aksw.servicecat.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.aksw.commons.util.StreamUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class ServiceRdfConverter {
    
    //@Autowired
    private Gson gson;
    
    public ServiceRdfConverter() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    public Model convert(String jsonStr) {
        //String json = gson.toJson(jsonStr, Object.class);        
        Model result;
        
        try {
            result = createModelFromSdJson(jsonStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return result;
    }
 
    
    public static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public static Model createModelFromSdJson(String jsonStr) throws IOException, ScriptException {
        //String json = StreamUtils.toString(resolver.getResource("/sd-test-data.json").getInputStream());

        String jsonLd = createSdJsonLd(jsonStr);
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(gson.fromJson(jsonLd, Object.class)));
        //System.out.println(jsonLd);
        
        Model model = ModelFactory.createDefaultModel();
        model.read(new ByteArrayInputStream(jsonLd.getBytes()), "http://example.org/", "JSON-LD");
        //model.write(System.out, "TURTLE");
        return model;
    }

    public static String createSdJsonLd(String json) throws ScriptException, IOException {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource jsonLdUtils = resolver.getResource("/json-ld-utils.js");
        Reader reader = new InputStreamReader(jsonLdUtils.getInputStream());
        //sdContext.

        String sdStaticSchema = StreamUtils.toString(resolver.getResource("/sd-jsonld-schema.js").getInputStream());
        sdStaticSchema = "(function() { return " + sdStaticSchema + " ; })();";
        
        String template = StreamUtils.toString(resolver.getResource("/sd-jsonld-gen.js").getInputStream());
        
        template = template + "()";
        
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        Bindings bindings = new SimpleBindings();
        
        bindings.put("templateStr", template);
        bindings.put("jsonStr", json);
        bindings.put("staticSchemaStr", sdStaticSchema);
        
        engine.eval(reader, bindings);
        String result = (String)bindings.get("jsonLdStr");

        return result;
    }

}
