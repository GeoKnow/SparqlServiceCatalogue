package org.aksw.rdf_dataset_catalog.web.main;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ExclusionStrategyClassAndFields
    implements ExclusionStrategy
{

    private Multimap<Class<?>, String> classToFieldName = HashMultimap.create();

    public ExclusionStrategyClassAndFields(
            Multimap<Class<?>, String> classToFieldName) {
        this.classToFieldName = classToFieldName;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        String fieldName = f.getName();
        Class<?> fieldClass = f.getDeclaringClass();
        
        Map<Class<?>, Collection<String>> map = classToFieldName.asMap();

        boolean result = false;

        for (Entry<Class<?>, Collection<String>> entry : map.entrySet()) {

            Class<?> entryClass = entry.getKey();
            Collection<String> entryFields = entry.getValue();

//            if (fieldClass.isAssignableFrom(entryClass)) {
            if (entryClass.isAssignableFrom(fieldClass)) {
                if (entryFields.contains(fieldName)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}