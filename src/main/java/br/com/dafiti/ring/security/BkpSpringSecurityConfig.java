/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.dafiti.ring.security;

import br.com.dafiti.ring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 *
 * @author guilherme.almeida
 */
//@Configuration
//@EnableWebSecurity
//@EnableScheduling
public class BkpSpringSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private UserService userDetailService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private @Value("${ring.anonymous.access:true}")
    boolean anonymousEnabled;
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(
                        "/observer",
                        "/webjars/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/customization/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Identify if anonymous access is enabled.
        if (anonymousEnabled) {
            http
                    .authorizeRequests()
                    .antMatchers("/",
                            "/**/home",
                            "/**/list",
                            "/**/view/**",
                            "/**/detail/**",
                            "/**/search/**",
                            "/**/log/**",
                            "/flow/**",
                            "/propagation/**",
                            "/**/user/confirmation/**",
                            "/**/alter/",
                            "/error/**",
                            "/user/edit/**",
                            "/build/history/**", "/**/**").permitAll();
        }

        http
                .authorizeRequests()
                .antMatchers(
                        "/user/add/",
                        "/user/role/**",
                        "/user/active/**",
                        "/configuration/**").access("hasRole('HERO')")
                .antMatchers(
                        "/**/delete/**",
                        "/**/rebuild/**",
                        "/workbench/workbench/**",
                        "/workbench/query/**").access("hasRole('ADMIN') || hasRole('HERO')")
                .antMatchers(
                        "/**/edit/**",
                        "/**/add/**").access("hasRole('USER') || hasRole('ADMIN') || hasRole('HERO')")
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/home").successHandler(loginSuccessHandler())
                .and()
                .logout().logoutUrl("/logout").permitAll().logoutSuccessUrl("/home")
                .and()
                .requestCache()
                .and()
                .exceptionHandling().accessDeniedPage("/403");

        http
                .sessionManagement()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry());
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }
    
}
