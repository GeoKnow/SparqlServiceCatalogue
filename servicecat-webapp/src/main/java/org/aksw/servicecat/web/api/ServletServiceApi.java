package org.aksw.servicecat.web.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.aksw.servicecat.core.ServiceAnalyzerProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
@Path("/services")
public class ServletServiceApi {
    
    @Autowired
    private ServiceAnalyzerProcessor processor;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/put")
    public String registerService(@QueryParam("url") String serviceUrl)
    {
        processor.process(serviceUrl);
        
        String result = "{}";
        return result;
    }


}
