package org.aksw.rdf_dataset_catalog.trash;

import java.lang.reflect.Type;
import java.util.List;

import org.hibernate.collection.internal.PersistentList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonSerializerPersistentList
    implements JsonSerializer<PersistentList> {

    @Override
    public JsonElement serialize(PersistentList src, Type typeOfSrc,
            JsonSerializationContext context) {

        JsonArray result = new JsonArray();
        
        for(Object item : src) {
            JsonElement e = context.serialize(item);
            result.add(e);
        }
        
        return result;
    }
}