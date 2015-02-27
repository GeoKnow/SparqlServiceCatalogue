(function() {
    var base = "http://servicecat.aksw.org/resource/";
    
    return {
        '@type': 'sd:Service',
        '@id': function() { this.$serviceUrlEnc = encodeURIComponent(this.endpoint); return base + 'service-' + this.$serviceUrlEnc; },
        'labels': function() { return {'': 'Service description of ' + this.endpoint }; },
        availableGraphs: {
            '@type': 'sd:GraphCollection',
            '@id': function() { this.$graphUrlEnc = encodeURIComponent(this.namedGraph.name); return base + 'availableGraphs-' + this.$serviceUrlEnc; },
            'labels': function() { return {'': 'Collection of available graphs for ' + this.endpoint }; },
            namedGraph: {
                '@type': 'sd:NamedGraph',
                '@id': function() { return base + 'namedGraph-' + this.$serviceUrlEnc + '-' + this.$graphUrlEnc; },
                name: {
                    '@id': function() { return this.name; }
                }
            }
        }
//        feature: {
//            '@id': function() { return base + 'feature-' + this.$serviceUrlEnc + this. },            
//        }
    };
})

/*
class GraphCollection {
    List<NamedGraph> namedGraph;
}

class Service
   GraphCollection availableGraphs;
}

class NamedGraph {
    String name;
}
*/