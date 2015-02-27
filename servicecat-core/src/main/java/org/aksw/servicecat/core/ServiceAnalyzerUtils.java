package org.aksw.servicecat.core;

import java.util.List;
import java.util.Map;

import org.aksw.jena_sparql_api.concepts.Concept;
import org.aksw.jena_sparql_api.concepts.ConceptUtils;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.lookup.ListService;
import org.aksw.jena_sparql_api.lookup.ListServiceConcept;
import org.aksw.jena_sparql_api.lookup.LookupService;
import org.aksw.jena_sparql_api.lookup.LookupServicePartition;
import org.aksw.jena_sparql_api.lookup.LookupServiceUtils;
import org.aksw.jena_sparql_api.mapper.Agg;
import org.aksw.jena_sparql_api.mapper.AggList;
import org.aksw.jena_sparql_api.mapper.AggMap;
import org.aksw.jena_sparql_api.mapper.AggTransforms;
import org.aksw.jena_sparql_api.mapper.AggUtils;
import org.aksw.jena_sparql_api.mapper.MappedConcept;
import org.aksw.jena_sparql_api.pagination.core.QueryExecutionFactoryPaginated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;

public class ServiceAnalyzerUtils {
    public static final Logger logger = LoggerFactory.getLogger(ServiceAnalyzerUtils.class);

    // There is a bug in Virtuoso 6.1.6 which yields empty results; adding a filter alleviates the problem.
    public static Concept listAllGraphsFallback = Concept.create("Graph ?g { ?s ?p ?o } . Filter(true)", "g");
    public static Concept listAllGraphsWithInstances = Concept.create("Graph ?g { ?s a ?o }", "g");

    
    public static List<Node> getAvailableGraphs(QueryExecutionFactory sparqlService, String serviceUrl) {
        ListService<Concept, Node> ls = new ListServiceConcept(sparqlService);
        
        List<Node> result;
        
        try {
            result = ls.fetchData(ConceptUtils.listAllGraphs, null, null);
    
            // Retry with the fallback solution if no graphs were returned
            if(result.isEmpty()) {
                result = ls.fetchData(listAllGraphsFallback, null, null);
            }
        } catch(Exception e) {
            logger.warn("Attempt to fetch all graphs failed. Trying final fallback.", e);
            result = ls.fetchData(listAllGraphsWithInstances, null, null);            
        }
        
        
        return result;
    }

    /*
    public static Map<String, Map<String, String>> getGraphLabels(List<String> graphNames) {
        
    }*/
    
    public static void createServiceDescription(String serviceUrl, JsonObject result) {
        
        QueryExecutionFactory sparqlService = new QueryExecutionFactoryHttp(serviceUrl);
        sparqlService = new QueryExecutionFactoryPaginated(sparqlService);
        
        List<Node> availableGraphs = getAvailableGraphs(sparqlService, serviceUrl);

        // Fetch labels
        Concept concept = Concept.create("?s ?p ?l . Filter(?p = <http://www.w3.org/2000/01/rdf-schema#label>)", "s");
        Agg<List<Node>> agg = AggList.create(AggUtils.literalNode("?l"));
        MappedConcept<List<Node>> mappedConcept = MappedConcept.create(concept, agg);


        LookupService<Node, List<Node>> ls = LookupServiceUtils.createLookupService(sparqlService, mappedConcept);
        ls = LookupServicePartition.create(ls, 30);

        Map<Node, List<Node>> labelMap = ls.lookup(availableGraphs);

//        {
//        Agg<Multimap<Node, Node>> foo = AggTransforms.multimap(
//            AggMap.create(AggUtils.mapper("?s"),
//                    AggList.create(AggUtils.literal("?l"))));
//        }


        //LookupService<Node, ResultSetPart> ls = new LookupServiceSparqlQuery(sparqlService, query, Var.alloc("s"));
        
        /*
        List<String> availableGraphs = new ArrayList<String>();
        for(Node node : graphNodes) {
            availableGraphs.add(node.getURI());
        }*/

        
        result.addProperty("endpoint", serviceUrl);

        // TODO Fetch the labels
        // TODO If no label was found, derive one from the URI
        
        JsonArray ags = new JsonArray();
        result.add("availableGraphs", ags);
        
        for(Node graphNode : availableGraphs) {
            JsonObject namedGraph = new JsonObject();
            ags.add(namedGraph);
            JsonObject graph = new JsonObject();
            namedGraph.add("namedGraph", graph);
            
            graph.addProperty("name", graphNode.getURI());
            
            JsonObject labels = new JsonObject();

            List<Node> graphLabels = labelMap.get(graphNode);
            if(graphLabels != null) {
                for(Node graphLabel : graphLabels) {
                    labels.addProperty(graphLabel.getLiteralLanguage(), graphLabel.getLiteralLexicalForm());
                
                    graph.add("labels", labels);
                }
            }

            /*
      http://brown.nlp2rdf.org/dataid.ttl      for(NodeValue label : ) {
                
            }*/
        }
    }
    
    public static Model createServiceDescription(String serviceUrl) {
        ServiceRdfConverter converter = new ServiceRdfConverter();
        JsonObject serviceDescription = new JsonObject();
        ServiceAnalyzerUtils.createServiceDescription(serviceUrl, serviceDescription);
        String json = serviceDescription.toString();
        Model result = converter.convert(json);
        //result.write(System.out, "TURTLE");

        return result;
    }

}
