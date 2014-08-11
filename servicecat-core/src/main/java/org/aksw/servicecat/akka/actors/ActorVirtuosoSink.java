package org.aksw.servicecat.akka.actors;

import java.io.ByteArrayInputStream;

import org.aksw.servicecat.akka.messages.WriteMessage;

import virtuoso.jena.driver.VirtGraph;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ActorVirtuosoSink extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public void onReceive(Object message) throws Exception {
        log.info("Got a message on " + this.getClass().getCanonicalName() + ": " + message);
        
        if(message instanceof WriteMessage) {
            WriteMessage msg = (WriteMessage)message;

            String graphName = msg.getGraphName();

            log.info("start writing into " + graphName);
            
            Model model = ModelFactory.createDefaultModel();
            model.read(new ByteArrayInputStream(msg.getRdfPayload().getBytes()), "http://example.org/", "N-TRIPLES");

            VirtGraph virtGraph = new VirtGraph(graphName, "jdbc:virtuoso://localhost:1152", "dba", "dba");
            
            virtGraph.clear();
            GraphUtil.addInto(virtGraph, model.getGraph());
            
            virtGraph.close();
            log.info("done writing");
        } else {
            log.error("Unsupported message at " + this.getClass().getCanonicalName() + ": " + message);
        }

        getSender().tell("success", getSelf());        
    }
}