package com.app.ecommerce.constants;

public class SecurityConstants {

    public static final String SIGN_UP_URLS = "/user/**";
    public static final String H2_URL = "/h2-console/**";
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long EXPIRATION_TIME = 86400000;
    public static final String SENDGRID_API_KEY = "SENDGRID_API_KEY";
    public static final String STRIPE_API_SECRET = "STRIPE_API_SECRET";
    public static final String STRIPE_WEBHOOK_KEY = "STRIPE_WEBHOOK_KEY";
}
