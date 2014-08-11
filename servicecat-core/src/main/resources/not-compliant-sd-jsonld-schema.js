{
    "@context": {
        rdfs: "http://www.w3.org/2000/01/rdf-schema#",
        endpoint: { "@id": "http://www.w3.org/ns/sparql-service-description#endpoint", "@type": "@id" },
        availableGraphs: {
            "@id": "http://www.w3.org/ns/sparql-service-description#availableGraphs",
            namedGraph: {
                "@id": "http://www.w3.org/ns/sparql-service-description#namedGraph",
                name: { "@id": "http://www.w3.org/ns/sparql-service-description#name", "@type": "@id" },
                labels: { "@id": "rdfs:label", "@container": "@language" }
            },
        }
    }
}
