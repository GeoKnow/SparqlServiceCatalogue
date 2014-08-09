package org.aksw.servicecat.web.api;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.aksw.servicecat.core.ServiceAnalyzerUtils;
import org.aksw.servicecat.model.Service;
import org.aksw.servicecat.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

@org.springframework.stereotype.Service
@Path("/services")
@Transactional
public class ServletDataStore {

    @PersistenceContext
    private EntityManager em;
    
    private static UserInfo guestUser;
    
    static {
        guestUser = new UserInfo();
        guestUser.setId(-1l);
        guestUser.setEmail("guest@foo.bar");
        guestUser.setUsername("guest");
        guestUser.setPassword("guest");
    }
    
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    
//    @Resource(name="authService")
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    private EntityInverseMapper entityInverseMapper;
//    
    @Autowired
    private Gson gson;

    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/put")
    public String registerService(@QueryParam("url") String serviceUrl)
    {

        List<String> availableGraphs = ServiceAnalyzerUtils.getAvailableGraphs(serviceUrl);
        
        Service service = new Service();
        service.setEndpoint(serviceUrl);
        service.setAvailableGraphs(availableGraphs);
        
        replaceEntity(em, service);
        
        
        // Write the graphs to the backend
        // We could directly generate RDF; but I am still undecided on whether this is a better approach:
        // If we stored data in Java entities and then persist them, we can easily use any RDF mapping we want
        // using RDB2RDF
        // Alternatively, we could convert all data to json, and then attempt to use a json mapping similar to R2R
        // (of course, an SML adaption would look much nicer)
        
        String result = "{}";
        return result;
    }

    
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/test")
//    public String test() {
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserInfo user = (UserInfo)auth.getPrincipal();
//
//        for(int i = 0; i < 100; ++i) {
//            String groupId = "com.example.group" + i;
//
//            for(int j = 0; j < 3; ++j) {
//                String artifactId = "artifact" + j;
//                
//                for(int k = 0; k < 3; ++k) {
//                    String version = "0.0." + k;
//                    
//
//                    Service service = new Service();
//                    
//                    for(int l = 0; l < 3; ++l) {
//                        String url = "http://example.com/download" + l;
//                        service.getGraphs().add(url);
//                    }
//                    
//                    System.out.println("service:" + service);
//                    registerService(service, user);
//                }
//            }
//            
//        }
//
//        
//        
//        return "{}";
//    }
    
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/get")
//    public String getInstance(@QueryParam("id") String uri)
//    {
//        Quad quad = new Quad(Quad.defaultGraphNodeGenerated, NodeFactory.createURI(uri), RDF.type.asNode(), NodeFactory.createURI("http://www.w3.org/ns/sparql-service-description#Service"));
//
//        //quad = new Quad(Quad.defaultGraphNodeGenerated, NodeFactory.createVariable("foo"), NodeFactory.createURI("http://dcat.cc/ontology/groupId"), NodeFactory.createURI("http://dcat.cc/resource/groupId-org.dbpedia.en"));
//    
//        List<?> items = fetchEntities(quad);
//
//        if(items.isEmpty()) {
//            throw new RuntimeException("No record under this id");
//        }
//        else if(items.size() > 1) {
//            throw new RuntimeException("Multiple records under this id, but only at most one expected");
//        }
//        
//        Object item = items.get(0);
//        //HibernateUti
//        String result = gson.toJson(item);
//        return result;
//    }
//    
    
    /**
     * Create a new service based on the given configuration
     * 
     * @param json
     * @return
     */
    /*
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/put")
    public String createInstance(@FormParam("data") String json)
    {       
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserInfo user = (UserInfo)auth.getPrincipal();

        Service service = gson.fromJson(json, Service.class);
        
        String result = registerService(service, user);
        return result;
    }
    */
    
    
//    
    public static <T> void replaceEntity(EntityManager em, T entity) {

        // This is a request to update a service - get the old state
        Object currentId = em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        if(currentId != null) {

            Object oldState = em.find(entity.getClass(), currentId);

            if(oldState != null) {
                em.remove(oldState);
                em.flush();
            }
        }

        em.persist(entity);
        em.flush();
    }
//
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/delete")
//    public String deleteDataset(@FormParam("id") Long id) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserInfo user = (UserInfo)auth.getPrincipal();
//
//        
//        Service oldState = em.find(Service.class, id);
//        
//        // Check if the user of the oldState equals the current user
//        Long ownerId = oldState.getOwner().getId();
//        
//        if(!user.getId().equals(ownerId)) {
//            throw new RuntimeException("Cannot update service of another owner");
//        }
//        
//        
//        Service config = em.find(Service.class, id);
//        em.remove(config);
//        em.flush();
//                
//        return "{}";
//    }
//    
//    public  List<?> fetchEntities(Quad quad) {
//        
//        List<EntityRef> entityRefs = entityInverseMapper.map(quad);
//        
//        List<Object> result = new ArrayList<Object>();
//        for(EntityRef er : entityRefs) {
//        
//        
//            CriteriaQuery<?> cq = createCriteriaQuery(em, er);
//            List<?> tmp = em.createQuery(cq).getResultList();
//            
//            result.addAll(tmp);
//        }
//        
//        return result;
//    }
//    
//    public static CriteriaQuery<?> createCriteriaQuery(EntityManager em, EntityRef er) {
//        Class<?> entityClass = er.getEntityClass();
//        
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        
//        CriteriaQuery<?> cq = cb.createQuery(entityClass);
//        Root<?> root = cq.from(entityClass);
//        
//        for(Entry<String, Object> entry : er.getPropertyToValue().entrySet()) {
//            String propertyName = entry.getKey();
//            Object propertyValue = entry.getValue();
//            
//            cq.where(cb.equal(root.get(propertyName), propertyValue));
//        }
//
//        return cq;
//    }

}
