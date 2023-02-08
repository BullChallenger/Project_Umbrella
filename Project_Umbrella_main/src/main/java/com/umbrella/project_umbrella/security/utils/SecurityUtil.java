package com.umbrella.project_umbrella.security.utils;

import com.umbrella.project_umbrella.security.userDetails.UserContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public String getLoginUserEmail() {
        UserContext userContext = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userContext.getUsername();
    }

    public Long getLoginUserId() {
        UserContext userContext = (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userContext.getId();
    }
}
