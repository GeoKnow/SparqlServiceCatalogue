package org.aksw.servicecat.akka.actors;

import java.io.ByteArrayOutputStream;

import org.aksw.servicecat.core.ServiceAnalyzerUtils;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.hp.hpl.jena.rdf.model.Model;

public class ActorServiceDescriptionGenerator extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public void onReceive(Object message) throws Exception {
        //log.info("Got a message on " + this.getClass().getCanonicalName() + ": " + message);

        String serviceUrl = "" + message;
        
        Model model = ServiceAnalyzerUtils.createServiceDescription(serviceUrl);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        model.write(baos, "N-TRIPLES");
        String result = baos.toString();
        
        getSender().tell(result, getSelf());        
    }
}