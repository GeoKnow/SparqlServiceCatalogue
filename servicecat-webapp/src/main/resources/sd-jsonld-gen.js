(function() {
    var base = "http://servicecat.cc/resource/";
    
    return {
        '@id': function() { this.$serviceUrlEnc = encodeURIComponent(this.endpoint); return base + 'service-' + this.$serviceUrlEnc; },
        availableGraphs: {
            '@id': function() { this.$graphUrlEnc = encodeURIComponent(this.namedGraph.name); return base + 'availableGraphs-' + this.$serviceUrlEnc; },
            namedGraph: {
                '@id': function() { return base + 'namedGraph-' + this.$serviceUrlEnc + '-' + this.$graphUrlEnc;  },
                name: {
                    '@id': function() { return this.name; }
                }
            }
        }
    };
})

