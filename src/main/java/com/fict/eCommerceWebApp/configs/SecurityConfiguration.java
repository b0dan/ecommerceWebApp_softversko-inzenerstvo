package com.fict.eCommerceWebApp.configs;

import com.fict.eCommerceWebApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private UserService userService;

    @Autowired
    public SecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();
        http.headers().frameOptions().disable();

        http.authorizeRequests()
            .mvcMatchers("/listings/create").authenticated()
            .mvcMatchers("/listings/purchase/{id}").authenticated()
            .mvcMatchers("/listings/edit/{id}").authenticated()
            .mvcMatchers("/listings/delete/{id}").authenticated()
            .mvcMatchers("/listings/{listingId}/images").authenticated()
            .mvcMatchers("/listings/{listingId}/images/delete/{imageId}").authenticated()
            .mvcMatchers("/profile/{id}/balance").authenticated()
            .mvcMatchers("/profile/{id}/purchased").authenticated()
            .mvcMatchers("/profile/{id}/listings").authenticated()
            .mvcMatchers("/profile/{id}/settings").authenticated()
            .mvcMatchers("/profile/{id}/delete").authenticated()
            .mvcMatchers("/api/export/{id}").authenticated()
            .mvcMatchers("/**").permitAll().anyRequest().authenticated().and()
            .formLogin().loginPage("/login").usernameParameter("email").successHandler(successHandler()).permitAll().and()
            .logout().logoutUrl("/logout").logoutSuccessUrl("/login").permitAll();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}