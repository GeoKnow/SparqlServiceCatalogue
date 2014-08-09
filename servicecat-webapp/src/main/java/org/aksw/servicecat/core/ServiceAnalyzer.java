package org.aksw.servicecat.core;

import com.hp.hpl.jena.rdf.model.Model;

public interface ServiceAnalyzer {
    Model analyze(String serviceUrl);
}
