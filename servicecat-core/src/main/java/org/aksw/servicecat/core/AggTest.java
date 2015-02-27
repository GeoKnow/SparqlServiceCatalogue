package org.aksw.servicecat.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.jena_sparql_api.concepts.Concept;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.lookup.ListService;
import org.aksw.jena_sparql_api.lookup.ListServiceConcept;
import org.aksw.jena_sparql_api.lookup.LookupService;
import org.aksw.jena_sparql_api.lookup.LookupServiceUtils;
import org.aksw.jena_sparql_api.mapper.Agg;
import org.aksw.jena_sparql_api.mapper.AggList;
import org.aksw.jena_sparql_api.mapper.AggObject;
import org.aksw.jena_sparql_api.mapper.AggTransforms;
import org.aksw.jena_sparql_api.mapper.AggUtils;
import org.aksw.jena_sparql_api.mapper.MappedConcept;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

class Musician {
    String uri;
    String name;
    //Map<String, String> labels = new HashMap<String, String>();
    List<String> labels = new ArrayList<String>();

    @Override
    public String toString() {
        return "Musician [uri=" + uri + ", name=" + name + ", labels=" + labels
                + "]";
    }
}


class AggObjectBuilder<K> {
    private Map<K, Agg<?>> state;

    public AggObjectBuilder() {
        this(new HashMap<K, Agg<?>>());
    }

    public AggObjectBuilder(Map<K, Agg<?>> state) {
        this.state = state;
    }

    public AggObjectBuilder<K> put(K k, Agg<?> v) {
        state.put(k, v);
        return this;
    }

    public AggObject<K> done() {
        AggObject<K> result = AggObject.create(state);
        return result;
    }

    public static <K, V> AggObjectBuilder<K> create(K[] k) {
        AggObjectBuilder<K> result = new AggObjectBuilder<K>();
        return result;
    }
}

public class AggTest {
    public static void main(String[] args) {


        QueryExecutionFactory sparqlService = new QueryExecutionFactoryHttp("http://lod.openlinksw.com/sparql", "http://dbpedia.org");

        ListService<Concept, Node> ls = new ListServiceConcept(sparqlService);

        String qstr
            = ""
            + "  ?musician <http://purl.org/dc/terms/subject> <http://dbpedia.org/resource/Category:German_musicians> ."
            + "  ?musician <http://xmlns.com/foaf/0.1/name> ?name ."
            + "  OPTIONAL {"
            + "    ?musician <http://www.w3.org/2000/01/rdf-schema#comment> ?description_en ."
            + "    FILTER (LANG(?description_en) = 'en') ."
            + "  }"
            + "  OPTIONAL {"
            + "    ?musician <http://www.w3.org/2000/01/rdf-schema#comment> ?description_de ."
            + "    FILTER (LANG(?description_de) = 'de') ."
            + "  }";

        Concept concept = Concept.create(qstr, "musician");


        Agg<Musician> agg =
            AggTransforms.clazz(Musician.class,
                AggObjectBuilder.create(new String[0])
                .put("uri", AggUtils.literal("?musician"))
                .put("name", AggUtils.literal("?name"))
                .put("labels", AggList.create(AggUtils.literal("?description_en")))
                .done()
            );

//        AggObject<String> agg = AggObjectBuilder.create(new String[0])
//        .put("uri", AggUtils.literal("?musician"))
//        .put("name", AggUtils.literal("?name"))
//        .put("labels", AggList.create(AggUtils.literal("?description_en")))
//        .done();

//var workflow = somePromise.then(function() { return newPromise; }).then(function(val) {} )



        List<Node> nodes = ls.fetchData(concept, 10l, null);
        System.out.println(nodes);

        MappedConcept<Musician> mappedConcept = MappedConcept.create(concept, agg);
        //MappedConcept<Map<String, ?>> mappedConcept = MappedConcept.create(concept, agg);

        LookupService<Node, ?> lookup = LookupServiceUtils.createLookupService(sparqlService, mappedConcept);
        Map<Node, ?> map = lookup.lookup(nodes);

        Collection<?> musicians = map.values();

        for(Object item : musicians) {
            System.out.println("GOT: " + item);
        }

        System.out.println("ARGH");
        Query q = QueryFactory.create("Select ?p (Count(Distinct ?o) As ?c) { ?s ?p ?o } Group By ?p");
        System.out.println(q.getProject());
        System.out.println(q.getAggregators());
        System.out.println(q.getProject());

    }
}
