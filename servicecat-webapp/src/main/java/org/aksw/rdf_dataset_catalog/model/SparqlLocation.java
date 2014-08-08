package org.aksw.rdf_dataset_catalog.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;

@Entity
public class SparqlLocation {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Dataset dataset;
    
    
    private String url;
    
    @ElementCollection
    @OrderColumn(name="sequence_id")
    @Column(name="uri")
    private List<String> defaultGraphs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getDefaultGraphs() {
        return defaultGraphs;
    }

    public void setDefaultGraphs(List<String> defaultGraphs) {
        this.defaultGraphs = defaultGraphs;
    }

    
    
    
//    public Dataset getDataset() {
//        return dataset;
//    }
//
 
    void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((defaultGraphs == null) ? 0 : defaultGraphs.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime
                * result
                + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SparqlLocation other = (SparqlLocation) obj;
        if (defaultGraphs == null) {
            if (other.defaultGraphs != null)
                return false;
        } else if (!defaultGraphs.equals(other.defaultGraphs))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SparqlLocation [id=" + id + ", url="
                + url + ", defaultGraphs=" + defaultGraphs
                + "]";
    }


}
