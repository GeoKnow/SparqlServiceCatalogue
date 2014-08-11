package org.aksw.servicecat.web.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service("authService")
public class MyUserDetailsService
    implements UserDetailsService
{

    @Override
    //@Transactional
    public UserDetails loadUserByUsername(String username){

//        UserDetails result =
//                entityManager
//                    .createQuery("FROM UserInfo WHERE username = :username", UserInfo.class)
//                    .setParameter("username", username)
//                    .getSingleResult();

        UserDetails result = null;
        return result;
    }
}
