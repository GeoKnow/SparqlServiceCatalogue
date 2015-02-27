package org.aksw.servicecat.akka.actors;

import org.aksw.servicecat.akka.messages.WriteMessage;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class ActorSdProcess extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public void onReceive(Object message) throws Exception {
        //log.info("Got a message on " + this.getClass().getCanonicalName() + ": " + message);

        String serviceUrl = "" + message;


        ActorRef sdGenerator = getContext().actorOf(Props.create(ActorServiceDescriptionGenerator.class));
        
        Timeout timeout = new Timeout(Duration.create(3600, "seconds"));
        Future<Object> future = Patterns.ask(sdGenerator, serviceUrl, timeout);
        String rdf = (String)Await.result(future, timeout.duration());
        
        WriteMessage wm = new WriteMessage(serviceUrl, rdf);
        ActorRef tripleStore = getContext().actorOf(Props.create(ActorVirtuosoSink.class));
        
        Future<Object> future2 = Patterns.ask(tripleStore, wm, timeout);
        Patterns.pipe(future2, getContext().system().dispatcher()).to(getSender());
    }
}