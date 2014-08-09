package org.aksw.servicecat.core;

import java.util.ArrayList;
import java.util.List;

import org.aksw.jena_sparql_api.concepts.Concept;
import org.aksw.jena_sparql_api.concepts.ConceptUtils;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.lookup.ListService;
import org.aksw.jena_sparql_api.lookup.ListServiceConcept;

import com.hp.hpl.jena.graph.Node;

public class ServiceAnalyzerUtils {

    public static List<String> getAvailableGraphs(String serviceUrl) {
        QueryExecutionFactory qef = new QueryExecutionFactoryHttp(serviceUrl);
        ListService<Concept, Node> ls = new ListServiceConcept(qef);
        List<Node> graphNodes = ls.fetchData(ConceptUtils.listAllGraphs, null, null);

        //FunctionN
        List<String> result = new ArrayList<String>();
        for(Node node : graphNodes) {
            result.add(node.getURI());
        }
        
        return result;
    }
}
