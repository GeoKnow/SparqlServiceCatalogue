package org.aksw.rdf_dataset_catalog.web.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.collection.internal.PersistentList;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;



/**
 * This TypeAdapter unproxies Hibernate proxied objects, and serializes them
 * through the registered (or default) TypeAdapter of the base class.
 * 
 * Source: http://stackoverflow.com/questions/13459718/could-not-serialize-object-cause-of-hibernateproxy
 */
public class TypeAdapterList extends TypeAdapter<List> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            
            TypeAdapter<T> result = null;

            
            System.out.println(type);
            if(List.class.isAssignableFrom(type.getRawType())) {
                TypeAdapter<List> delegate = gson.getDelegateAdapter(this, TypeToken.get(List.class));

                result = (TypeAdapter<T>)(new TypeAdapterList(gson, delegate));
            }


            
            return result;
        }
    };

    private final Gson context;
    TypeAdapter<List> delegate;

    private TypeAdapterList(Gson context, TypeAdapter<List> delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    public PersistentList read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void write(JsonWriter out, List value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        
        List<Object> clone = new ArrayList<Object>();
        clone.addAll(value);

        delegate.write(out, clone);
    }
}
