package org.aksw.rdf_dataset_catalog.web.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.aksw.rdf_dataset_catalog.model.Dataset;
import org.aksw.rdf_dataset_catalog.model.UserInfo;
import org.aksw.sparqlify.jpa.EntityInverseMapper;
import org.aksw.sparqlify.jpa.EntityRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.vocabulary.RDF;

/*
interface ObjectAccessManager {
    canModify(;)
    canDelete();
    
}
*/


@Service
@Path("/store")
@Transactional
public class ServletDatastore
{
	@PersistenceContext
	private EntityManager em;

    @Autowired
    private PasswordEncoder passwordEncoder;
	
    @Resource(name="authService")
    private UserDetailsService userDetailsService;

    @Autowired
    private EntityInverseMapper entityInverseMapper;
    
    //@Resource
    @Autowired
    private Gson gson;

    
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/test")
	public String test() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = (UserInfo)auth.getPrincipal();

	    for(int i = 0; i < 100; ++i) {
	        String groupId = "com.example.group" + i;

	        for(int j = 0; j < 3; ++j) {
	            String artifactId = "artifact" + j;
	            
	            for(int k = 0; k < 3; ++k) {
	                String version = "0.0." + k;
	                

	                Dataset dataset = new Dataset();
	                dataset.setGroupId(groupId);
	                dataset.setArtifactId(artifactId);
	                dataset.setVersion(version);
	                dataset.setComment("");
	                dataset.setPrimaryIri("");
	                
	                for(int l = 0; l < 3; ++l) {
	                    String url = "http://example.com/download" + l;
	                    dataset.getDownloadLocations().add(url);
	                }
	                
                    System.out.println("dataset:" + dataset);
	                registerDataset(dataset, user);
	            }
	        }
	        
	    }

	    
	    
		return "{}";
	}
	
	/**
	 * Note: NEVER send the password in plain text - send a hash instead (and if possible, use HTTPS)
	 * 
	 * @param username
	 * @return
	 */
	@POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/registerUser")
    public String registerUser(@FormParam("username") String username, @FormParam("password") String rawPassword, @FormParam("email") String email) {
	    UserInfo userInfo = new UserInfo();
	    userInfo.setUsername(username);
	    userInfo.setEmail(email);

	    // TODO Make sure the email does not already exist - right now 
	    
	    
	    String encodedPassword = passwordEncoder.encode(rawPassword);
	    userInfo.setPassword(encodedPassword);

	    em.persist(userInfo);
	    em.flush();
	    
	    
	    login(username, rawPassword);

	    String result = gson.toJson(userInfo);
	    
	    return result;
	}

	
	public UserInfo getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Object details = auth.getPrincipal();
        
        UserInfo result;
        if(!auth.isAuthenticated() || !(details instanceof UserInfo)) {
            result = null;
        } else {
            result = (UserInfo)details;
        }
	    
        return result;
	}
	
	public String getUserJson(UserInfo user) {
        String result = user == null ? "{\"isAuthenticated\": false}" : gson.toJson(user);  

        return result;	    
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/checkSession")
	public String checkSession() {
	    
	    UserInfo user = getUserInfo();	    
	    String result = getUserJson(user);  

	    return result;
	}

//	@Autowired
//	private AuthenticationManager authenticationManager;
	
//	@Autowired
//	private ProviderManager authenticationManager;

//	@Inject
//	@InjectParam("org.springframework.security.authenticationManager")
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/logIn")
	public String login(@FormParam("username") String username, @FormParam("password") String password) {
	    Authentication request = new UsernamePasswordAuthenticationToken(username, password);
	    
	    Authentication auth = authenticationManager.authenticate(request);
	    
	    SecurityContextHolder.getContext().setAuthentication(auth);

	    
        UserInfo user = getUserInfo();
        Map<String, Object> publicInfo = new HashMap<String, Object>();
        publicInfo.put("id", user.getId());
        publicInfo.put("username", user.getUsername());

        String result = gson.toJson(publicInfo);
        //String result = getUserJson(user);
        
        return result;
	}
	
	private @Autowired HttpServletRequest request;

	@POST
    @Path("/logOut")
	public void logout() throws ServletException {
	    request.logout();
	    
	    SecurityContextHolder.clearContext();

        //if (invalidateHttpSession) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        //}	    
	}
	
	public static CriteriaQuery<?> createCriteriaQuery(EntityManager em, EntityRef er) {
        Class<?> entityClass = er.getEntityClass();
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaQuery<?> cq = cb.createQuery(entityClass);
        Root<?> root = cq.from(entityClass);
        
        for(Entry<String, Object> entry : er.getPropertyToValue().entrySet()) {
            String propertyName = entry.getKey();
            Object propertyValue = entry.getValue();
            
            cq.where(cb.equal(root.get(propertyName), propertyValue));
        }

        return cq;
	}
	
	public  List<?> fetchEntities(Quad quad) {
	    
        List<EntityRef> entityRefs = entityInverseMapper.map(quad);
        
        List<Object> result = new ArrayList<Object>();
        for(EntityRef er : entityRefs) {
        
        
            CriteriaQuery<?> cq = createCriteriaQuery(em, er);
            List<?> tmp = em.createQuery(cq).getResultList();
            
            result.addAll(tmp);
        }
        
        return result;
	}
	
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get")
    public String getInstance(@QueryParam("id") String uri)
    {
        Quad quad = new Quad(Quad.defaultGraphNodeGenerated, NodeFactory.createURI(uri), RDF.type.asNode(), NodeFactory.createURI("http://www.w3.org/ns/dcat#Dataset"));

        //quad = new Quad(Quad.defaultGraphNodeGenerated, NodeFactory.createVariable("foo"), NodeFactory.createURI("http://dcat.cc/ontology/groupId"), NodeFactory.createURI("http://dcat.cc/resource/groupId-org.dbpedia.en"));
    
        List<?> items = fetchEntities(quad);

        if(items.isEmpty()) {
            throw new RuntimeException("No record under this id");
        }
        else if(items.size() > 1) {
            throw new RuntimeException("Multiple records under this id, but only at most one expected");
        }
        
        Object item = items.get(0);
        //HibernateUti
        String result = gson.toJson(item);
        return result;
    }
	
	
	/**
	 * Create a new service based on the given configuration
	 * 
	 * @param json
	 * @return
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/put")
	public String createInstance(@FormParam("data") String json)
	{	    
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = (UserInfo)auth.getPrincipal();

        Dataset dataset = gson.fromJson(json, Dataset.class);
        
        String result = registerDataset(dataset, user);
        return result;
	}
	
	public String registerDataset(Dataset dataset, UserInfo user) {
        boolean isUpdate = false;

        // This is a request to update a dataset - get the old state
        if(dataset.getId() != null) {

            Dataset oldState = em.find(Dataset.class, dataset.getId());

            if(oldState != null) {
                isUpdate = true;
            
                // Check if the user of the oldState equals the current user
                Long ownerId = oldState.getOwner().getId();
                
                if(!user.getId().equals(ownerId)) {
                    throw new RuntimeException("Cannot update dataset of another owner");
                }
                
                em.remove(oldState);
                em.flush();
                dataset.setId(null);
            }

            //em.detach(oldState);
            //em.remove(oldState);
            //em.
            //em.flush();
        }

		dataset.setOwner(user);
		
//		if(isUpdate) {
//		    em.merge(dataset);
//		} else {
//	        em.persist(dataset);		    
//		}

		em.persist(dataset);
		em.flush();

		return "{}";
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/delete")
	public String deleteDataset(@FormParam("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = (UserInfo)auth.getPrincipal();

        
        Dataset oldState = em.find(Dataset.class, id);
        
        // Check if the user of the oldState equals the current user
        Long ownerId = oldState.getOwner().getId();
        
        if(!user.getId().equals(ownerId)) {
            throw new RuntimeException("Cannot update dataset of another owner");
        }
	    
	    
		Dataset config = em.find(Dataset.class, id);
		em.remove(config);
		em.flush();
				
		return "{}";
	}

}
