package org.aksw.servicecat.core;

import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ServiceAnalyzerAvailableGraphs
    implements ServiceAnalyzer
{
    @Override
    public Model analyze(String serviceUrl) {
        List<String> graphs = ServiceAnalyzerUtils.getAvailableGraphs(serviceUrl);
        
        Model result = ModelFactory.createDefaultModel();
        
        return result;
    }

}
