package org.aksw.servicecat.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OrderColumn;


@Entity
public class Service
    extends Resource
{

	@Column(nullable=false, unique=true)
	private String endpoint;
		
	// The basic comment associated with the dataset
//	@Column(nullable=false)
//	private String comment;
	
	
	// TODO Ensure that persisting a dataset object cannot create a new user.
//	@ManyToOne(optional=false, cascade=CascadeType.REFRESH, fetch=FetchType.EAGER)
//	private UserInfo owner;


	@ElementCollection(fetch=FetchType.EAGER)
	@OrderColumn(name="sequence_id")
	@Column(name="url")
	private List<String> availableGraphs = new ArrayList<String>();
	//private GraphCollection availableGraphs;

	// count [ value foo ; computedByProcess [ timeTaken 1000 ; timeStarted someData ] ] 

//    public Long getId() {
//        return id;
//    }
//
//
//    public void setId(Long id) {
//        this.id = id;
//    }


    public String getEndpoint() {
        return endpoint;
    }


    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }


//    public UserInfo getOwner() {
//        return owner;
//    }
//
//
//    public void setOwner(UserInfo owner) {
//        this.owner = owner;
//    }


    public List<String> getAvailableGraphs() {
        return availableGraphs;
    }


    public void setAvailableGraphs(List<String> availableGraphs) {
        this.availableGraphs = availableGraphs;
    }


    @Override
    public String toString() {
        return "Service [endpoint=" + endpoint + ", availableGraphs="
                + availableGraphs + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((availableGraphs == null) ? 0 : availableGraphs.hashCode());
        result = prime * result
                + ((endpoint == null) ? 0 : endpoint.hashCode());
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
        Service other = (Service) obj;
        if (availableGraphs == null) {
            if (other.availableGraphs != null)
                return false;
        } else if (!availableGraphs.equals(other.availableGraphs))
            return false;
        if (endpoint == null) {
            if (other.endpoint != null)
                return false;
        } else if (!endpoint.equals(other.endpoint))
            return false;
        return true;
    }
    
}
