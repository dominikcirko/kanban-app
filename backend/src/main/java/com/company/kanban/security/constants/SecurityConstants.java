package com.company.kanban.security.constants;

import com.company.kanban.config.PropertiesConfig;

public class SecurityConstants {

    //public static final String JWT_SECRET_KEY = propertiesConfig.jwtSecret();
    public static final int TOKEN_EXPIRATION = 7200000; // 7200000 milliseconds = 7200 seconds = 2 hours.
    public static final String BEARER = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";

    //"bQeThWmZq4t7w!z$C&F)J@NcRfUjXn2r5u8x/A?D*G-KaPdSgVkYp3s6v9y$B&E)"
}