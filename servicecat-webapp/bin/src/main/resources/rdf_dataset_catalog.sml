
Prefix o: <http://dcat.cc/ontology/>
Prefix r: <http://dcat.cc/resource/>


Prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
Prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>
Prefix owl: <http://www.w3.org/2002/07/owl#>

Prefix dcat: <http://www.w3.org/ns/dcat#>


// Note: This should better go into the database for better performance
Create View ontology As
  Construct{
    rdf:type a rdf:Property .
    rdfs:label a rdf:Property .

    o:groupId a rdf:Property .
    o:artifactId a rdf:Property .
    o:version a rdf:Property .
    
    o:owner a rdf:Property .
    o:hasDownloads a rdf:Property .
    o:hasEndpoints a rdf:Property .
    o:url a rdf:Property .
    o:hasDefaultGraphs a rdf:Property .
  }



/*
This view would be a test case for incorrect RDB2RDF mapping: the labels are duplicated if multiple datasets use the same group id

Create View dataset As
  Construct {
    ?s
      a dcat:Dataset ;
      rdfs:label ?l ;
      o:groupId ?g ;
      o:artifactId ?a ;
      o:version ?v ;      
      .
      
    ?g rdfs:label ?gl .
    ?a rdfs:label ?al .
    ?v rdfs:label ?vl .    
  }
  With
    ?s = uri(r:, 'dataset', ?id)
    ?l = plainLiteral(?name)

    ?g = uri(r:, 'groupId-', ?groupid)
    ?a = uri(r:, 'artifactId-', ?artifactid)
    ?v = uri(r:, 'version-', ?version)

    ?gl = plainLiteral(?groupid)
    ?al = plainLiteral(?artifactid)
    ?vl = plainLiteral(?version)

  From
    [[SELECT *, CONCAT(groupid, ':', artifactid, ':', version) AS name FROM dataset]]
*/


// NOTE: A view definition with just the plain table enables inverse-mapping of view definitions!
Create View dataset As
  Construct {
    ?s
      a dcat:Dataset ;
      o:groupId ?g ;
      o:artifactId ?a ;
      o:version ?v ;
      o:owner ?o ;
      .
  }
  With
    ?s = uri(r:, 'dataset', ?id)
    ?g = uri(r:, 'groupId-', ?groupid)
    ?a = uri(r:, 'artifactId-', ?artifactid)
    ?v = uri(r:, 'version-', ?version)
    ?o = uri(r:, 'user', ?owner_id)
  From
    dataset


Create View dataset_labels As
  Construct {
    ?s
      rdfs:label ?l ;
      .
  }
  With
    ?s = uri(r:, 'dataset', ?id)
    ?l = plainLiteral(?name)
  From
    [[SELECT id, CONCAT(groupid, ':', artifactid, ':', version) AS name FROM dataset]]


Create View users As
  Construct {
    ?s
      a o:User ;
      rdfs:label ?l ;
    .
  }
  With
    ?s = uri(r:, 'user', ?id)
    ?l = plainLiteral(?username)
  From
    userinfo


// NOTE we need to ensure labels are unique...
Create View groupLabels As
  Construct {
    ?s rdfs:label ?l ;
  }
  With
    ?s = uri(r:, 'groupId-', ?groupid)
    ?l = plainLiteral(?groupid) 
  From
    [[SELECT DISTINCT groupid FROM dataset]]


Create View artifactLabels As
  Construct {
    ?s rdfs:label ?l ;
  }
  With
    ?s = uri(r:, 'artifactId-', ?artifactid)
    ?l = plainLiteral(?artifactid) 
  From
    [[SELECT DISTINCT artifactid FROM dataset]]


Create View versionLabels As
  Construct {
    ?s rdfs:label ?l ;
  }
  With
    ?s = uri(r:, 'version-', ?version)
    ?l = plainLiteral(?version) 
  From
    [[SELECT DISTINCT version FROM dataset]]


Create View dataset_download_seq As
  Construct {
    ?s
      a rdf:Seq ;
      .
    
    ?d
      o:hasDownloads ?s ;
      .
  }
  With
    ?s = uri(r:, 'dataset-downloads', ?dataset_id)
    ?d = uri(r:, 'dataset', ?dataset_id)
  From
    [[SELECT DISTINCT "dataset_id" FROM "dataset_downloadlocations"]]



Create View dataset_downloads As
  Construct {
    ?s ?p ?o
  }
  With
    ?s = uri(r:, 'dataset-downloads', ?dataset_id)
    ?p = uri(rdfs:, '_', ?sid)
    ?o = uri(?url)
  From
    [[SELECT "dataset_id", "sequence_id" + 1 AS "sid", "url" FROM "dataset_downloadlocations"]]

    

Create View dataset_endpoint_seq As
  Construct {
    ?s a rdf:Seq .    
    ?d o:hasEndpoints ?s
  }
  With
    ?s = uri(r:, 'dataset-endpoints', ?dataset_id)
    ?d = uri(r:, 'dataset', ?dataset_id)
  From
    [[SELECT DISTINCT "dataset_id" FROM "sparqllocation"]]


// TODO SML parser needs fixes for missing comma and missing question mark
Create View dataset_endpoints As
  Construct {
    ?s ?p ?o .
    ?o o:url ?u 
  }
  With
    ?s = uri(r:, 'dataset-endpoints', ?dataset_id)
    ?p = uri(rdfs:, '_', ?sid)
    ?o = uri(r:, 'dataset-endpoint', ?dataset_id, '-', ?sid)
    ?u = uri(?url)
  From
    [[SELECT "dataset_id", "sequence_id" + 1 AS "sid", "url" FROM "sparqllocation"]]


Create View endpoint_graph_seq As
  Construct {
    ?s o:hasDefaultGraphs ?g .
    ?g a rdf:Seq .
  }
  With
    ?s = uri(r:, 'dataset-endpoint', ?dataset_id, '-', ?sid)
    ?g = uri(r:, 'dataset-endpoint-graphs', ?dataset_id, '-', ?sid)
  From
      [[SELECT "a"."dataset_id", "a"."sequence_id" + 1 AS "sid" FROM "sparqllocation" "a" JOIN "sparqllocation_defaultgraphs" "b" ON("b"."sparqllocation_id" = "a"."id")]]
      
      
Create View endpoint_graphs As
  Construct {
    ?s ?p ?o
  }
  With
    ?s = uri(r:, 'dataset-endpoint-graphs', ?dataset_id, '-', ?sid)
    ?p = uri(rdf:, '_', ?sid)
    ?o = uri(?uri)
  From
      [[SELECT "a"."dataset_id", "a"."sequence_id" + 1 AS "sid", "uri" FROM "sparqllocation" "a" JOIN "sparqllocation_defaultgraphs" "b" ON("b"."sparqllocation_id" = "a"."id")]]
  