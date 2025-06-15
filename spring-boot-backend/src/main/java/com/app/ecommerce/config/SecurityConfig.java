package com.app.ecommerce.config;

import com.app.ecommerce.constants.SecurityConstants;
import com.app.ecommerce.exceptions.CustomAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private com.app.ecommerce.config.JwtAuthenticationEntryPoint entryPoint;

    @Autowired
    private com.app.ecommerce.config.CustomUserDetailsService customUserDetailService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public com.app.ecommerce.config.JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new com.app.ecommerce.config.JwtAuthenticationFilter();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(customUserDetailService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().
                exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeHttpRequests()
                .antMatchers(
                        "/",
                        "favicon.ico",
                        "/**/*.png",
                        "/**/*.gif",
                        "**/*.svg",
                        "**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/ping"
                ).permitAll()
                .antMatchers("/user/login", "/user/register", "/user/getUser", "/products/**", "/user/passwordResetEmailVerification/**",
                        "/user/reset-password/**", "/user/verify/**", "/user/validate-reset-token/**", "/reviews/product/**").permitAll()
                .antMatchers("/admin/**", "/user/getUsers", "/orders/userOrders/").hasRole("ADMIN")
                .antMatchers(SecurityConstants.H2_URL).permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
