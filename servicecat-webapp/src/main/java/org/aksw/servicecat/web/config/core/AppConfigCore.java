package org.aksw.servicecat.web.config.core;

import org.aksw.servicecat.core.ServiceAnalyzerProcessor;
import org.aksw.servicecat.core.ServiceAnalyzerProcessorAkkaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan({"org.aksw.servicecat.web"})
public class AppConfigCore {
    private static final Logger logger = LoggerFactory
            .getLogger(AppConfigCore.class);

    @javax.annotation.Resource
    private Environment env;

    // TODO Add virtuoso / triple-store connection util
    
    @Bean
    public ServiceAnalyzerProcessor serviceAnalyzerProcessor() throws Exception {
        ServiceAnalyzerProcessor result = ServiceAnalyzerProcessorAkkaImpl.create();
        return result;
    }
}
