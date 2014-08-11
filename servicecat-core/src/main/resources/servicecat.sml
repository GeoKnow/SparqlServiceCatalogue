Prefix sml:<http://aksw.org/sparqlify/>

Prefix o: <http://servicecat.cc/ontology/>
Prefix r: <http://servicecat.cc/resource/>


Prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
Prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
Prefix owl: <http://www.w3.org/2002/07/owl#>

Prefix sd: <http://www.w3.org/ns/sparql-service-description#>


// Note: This should better go into the database for better performance
Create View ontology As
  Construct{
    rdf:type a rdf:Property .
    rdfs:label a rdf:Property .

    sd:Service a owl:Class .
  }


Create View _service As
  Construct {
    ?s
      a sd:Service ;
      sd:endpoint ?e
      .
      
  }
  With
    ?s = uri(r:, 'service-', sml:urlEncode(?endpoint))
    ?e = uri(?endpoint)
  From
    "service"


Create View service_labels As
  Construct {
    ?s
      rdfs:label ?l
      .
  }
  With
    ?s = uri(r:, 'service-', sml:urlEncode(?endpoint))
    ?l = plainLiteral(?text, ?language)
  From
    [[SELECT * FROM service_labels a JOIN service b ON (b.id = a.service_id)]]
    //service_labels

Create View service_availablegraphs_collection As
  Construct {
    ?s
      sd:availableGraphs ?ag
      .    
  }
  With 
    ?s = uri(r:, 'service-', sml:urlEncode(?endpoint))
    ?ag = uri(r:, 'service-availablegraphs-', sml:urlEncode(?endpoint))
  From
    "service"


Create View service_availablegraphs As
  Construct {
    ?ag
      sd:namedGraph ?g
      .    
  }
  With 
    ?ag = uri(r:, 'service-availablegraphs-', sml:urlEncode(?endpoint))
    ?g = uri(?url)
  From
    [[SELECT * FROM service_availablegraphs a JOIN service b ON (b.id = a.service_id)]]

    