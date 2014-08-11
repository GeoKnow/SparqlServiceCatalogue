package org.aksw.servicecat.core;

import org.aksw.servicecat.akka.actors.ActorSdProcess;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;

public class ServiceAnalyzerProcessorAkkaImpl
    implements ServiceAnalyzerProcessor
{
    private ActorSystem actorSystem;
    
    public ServiceAnalyzerProcessorAkkaImpl(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }
    
    public void process(String serviceUrl) {
        
        ActorRef sdGeneratorProcess = actorSystem.actorOf(Props.create(ActorSdProcess.class)); //, "sd-process");
        
        Timeout timeout = new Timeout(Duration.create(600, "seconds"));
        Future<Object> future = Patterns.ask(sdGeneratorProcess, serviceUrl, timeout);
        
        String result;
        try {
            result = (String) Await.result(future, timeout.duration());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    
        System.out.println("Done: " + result);        
    }
    
    public static ServiceAnalyzerProcessorAkkaImpl create() throws Exception {
        ActorSystem actorSystem = ActorSystem.create("sd-system");
        //ActorRef tripleStore = system.actorOf(Props.create(ActorVirtuosoSink.class), "sd-triplestore");
        
        ServiceAnalyzerProcessorAkkaImpl result = new ServiceAnalyzerProcessorAkkaImpl(actorSystem);        
        return result;
    }
}
