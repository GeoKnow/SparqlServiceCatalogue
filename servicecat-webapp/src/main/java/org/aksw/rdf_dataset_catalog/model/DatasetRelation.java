package org.aksw.rdf_dataset_catalog.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class DatasetRelation {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    //@ManyToOne
    //private Dataset sourceDataset;
    
    @ManyToOne
    private Dataset targetDataset;
    
    @ManyToOne
    private DatasetRelationType relationType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dataset getTargetDataset() {
        return targetDataset;
    }

    public void setTargetDataset(Dataset targetDataset) {
        this.targetDataset = targetDataset;
    }

    public DatasetRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(DatasetRelationType relationType) {
        this.relationType = relationType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((relationType == null) ? 0 : relationType.hashCode());
        result = prime * result
                + ((targetDataset == null) ? 0 : targetDataset.hashCode());
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
        DatasetRelation other = (DatasetRelation) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (relationType == null) {
            if (other.relationType != null)
                return false;
        } else if (!relationType.equals(other.relationType))
            return false;
        if (targetDataset == null) {
            if (other.targetDataset != null)
                return false;
        } else if (!targetDataset.equals(other.targetDataset))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DatasetRelation [id=" + id + ", targetDataset=" + targetDataset
                + ", relationType=" + relationType + "]";
    }
}
