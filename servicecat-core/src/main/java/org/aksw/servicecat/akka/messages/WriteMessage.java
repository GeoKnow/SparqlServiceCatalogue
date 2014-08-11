package org.aksw.servicecat.akka.messages;

import java.io.Serializable;

public class WriteMessage
    implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -6247723103398524485L;
 
    private String graphName;
    private String rdfPayload; // Currently expected to be n-triples
    
    public WriteMessage(String graphName, String rdfPayload) {
        super();
        this.graphName = graphName;
        this.rdfPayload = rdfPayload;
    }

    public String getGraphName() {
        return graphName;
    }
    public String getRdfPayload() {
        return rdfPayload;
    }
}