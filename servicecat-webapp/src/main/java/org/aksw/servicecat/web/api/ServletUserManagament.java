package org.aksw.servicecat.web.api;

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

import org.aksw.servicecat.model.Service;
import org.aksw.servicecat.model.UserInfo;
import org.aksw.sparqlify.jpa.EntityInverseMapper;
import org.aksw.sparqlify.jpa.EntityRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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


@org.springframework.stereotype.Service
@Path("/users")
@Transactional
public class ServletUserManagament
{
	@PersistenceContext
	private EntityManager em;

    @Autowired
    private PasswordEncoder passwordEncoder;
	
    @Resource(name="authService")
    private UserDetailsService userDetailsService;

    @Autowired
    private EntityInverseMapper entityInverseMapper;
    
    @Autowired
    private Gson gson;

	
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
			
}
