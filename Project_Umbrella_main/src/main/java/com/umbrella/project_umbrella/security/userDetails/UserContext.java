package com.umbrella.project_umbrella.security.userDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class UserContext extends User {

    private Long id;

    public UserContext (com.umbrella.project_umbrella.domain.User.User user, List<GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);

        this.id = user.getId();
    }

    public Long getId() {
        return this.id;
    }
}