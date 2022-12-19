package com.kanni.springCache.repository;

import com.kanni.springCache.model.Roles;
import com.kanni.springCache.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    PasswordEncoder encoder;

    List<User> userDetails=new ArrayList<>();

    public User findByUsername(String username){

        return userDetails.stream().filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst().orElseThrow(()-> new UsernameNotFoundException("user not found"+username));
    }

    @PostConstruct
    public void createUsers(){

        User  user1=new User(1l,"test","test@gmail.com",null,null);
        user1.setRoles(new HashSet<>() {{ add(Roles.ROLE_ADMIN);add(Roles.ROLE_BI);}});
        user1.setPassword(encoder.encode("test"));

        User  user2=new User(1l,"client","client@gmail.com","client",null);
        user2.setRoles(new HashSet<>() {{ add(Roles.ROLE_USER);}});
        user2.setPassword(encoder.encode("client"));

        userDetails.add(user1);
        userDetails.add(user2);
    }

}
