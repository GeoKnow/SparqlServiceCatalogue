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
        log.info("Got a message on " + this.getClass().getCanonicalName() + ": " + message);

        String serviceUrl = "" + message;


        ActorRef sdGenerator = getContext().actorOf(Props.create(ActorServiceDescriptionGenerator.class));
        //ActorSelection tripleStore = getContext().actorSelection("/user/sd-triplestore");
        
        //ActorSelection tripleStore = getContext().actorSelection("akka://sd-system/user/tripleStore");
        //ActorRef actor = getContext().actorFor("tripleStore");
        //System.out.println(actor.isTerminated());
        //System.out.println("Actor path: " + actor.path());
        
        //tripeStore.
        //tripleStore.forward(, context);
        //System.out.println("triple store again: " + tripleStore);
        
        Timeout timeout = new Timeout(Duration.create(600, "seconds"));
        Future<Object> future = Patterns.ask(sdGenerator, serviceUrl, timeout);
        String rdf = (String)Await.result(future, timeout.duration());
        
        WriteMessage wm = new WriteMessage(serviceUrl, rdf);
        
        ActorRef tripleStore = getContext().actorOf(Props.create(ActorVirtuosoSink.class));
        
        Future<Object> future2 = Patterns.ask(tripleStore, wm, timeout);
        
        Patterns.pipe(future2, getContext().system().dispatcher()).to(getSender());
        
        //this.getSender().tell("moo", this.getSelf());
        //Patterns.
        //Patterns.pipe(tmp, getContext().system().dispatcher()).to(getSender());
    }
}