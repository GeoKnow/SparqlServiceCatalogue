package org.aksw.servicecat.core;

import java.util.ArrayList;
import java.util.List;

import org.aksw.jena_sparql_api.concepts.Concept;
import org.aksw.jena_sparql_api.concepts.ConceptUtils;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.lookup.ListService;
import org.aksw.jena_sparql_api.lookup.ListServiceConcept;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;

public class ServiceAnalyzerUtils {
    public static final Logger logger = LoggerFactory.getLogger(ServiceAnalyzerUtils.class);

    // There is a bug in Virtuoso 6.1.6 which yields empty results; adding a filter alleviates the problem.
    public static Concept listAllGraphsFallback = Concept.create("Graph ?g { ?s ?p ?o } . Filter(true)", "g");
    public static Concept listAllGraphsWithInstances = Concept.create("Graph ?g { ?s a ?o }", "g");

    
    public static List<String> getAvailableGraphs(String serviceUrl) {
        QueryExecutionFactory qef = new QueryExecutionFactoryHttp(serviceUrl);
        qef = new QueryExecutionFactoryPaginated(qef);
        ListService<Concept, Node> ls = new ListServiceConcept(qef);
        
        List<Node> graphNodes;
        
        try {
            graphNodes = ls.fetchData(ConceptUtils.listAllGraphs, null, null);
    
            // Retry with the fallback solution if no graphs were returned
            if(graphNodes.isEmpty()) {
                graphNodes = ls.fetchData(listAllGraphsFallback, null, null);
            }
        } catch(Exception e) {
            logger.warn("Attempt to fetch all graphs failed. Trying final fallback.", e);
            graphNodes = ls.fetchData(listAllGraphsWithInstances, null, null);            
        }
        
        //FunctionN
        List<String> result = new ArrayList<String>();
        for(Node node : graphNodes) {
            result.add(node.getURI());
        }
        
        return result;
    }
}
