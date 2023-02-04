package com.umbrella.project_umbrella.security.login.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    public static String getLoginUserNickName() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                                                                        .getAuthentication()
                                                                        .getPrincipal();

        return userDetails.getUsername();
    }
}
