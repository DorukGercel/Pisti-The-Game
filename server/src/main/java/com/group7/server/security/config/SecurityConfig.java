package com.group7.server.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** The configurations of the web security components*/
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsManager mUserDetailsManager;
    private final PasswordEncoder mPasswordEncoder;
    private final JwtRequestFilter mJwtRequestFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     *This method determines the authentication requirements to URLs of the api.
     *They can either be public or private (require JWT token).
     * */
    // TODO: Rearrange url's authentication settings
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/player/register").permitAll()
                .antMatchers("/api/player/login").permitAll()
                .antMatchers("/api/player/resetPassword").permitAll()
                .antMatchers("/api/game/startGame").permitAll()
                .antMatchers("/api/leaderboard/create").permitAll()
                .antMatchers("/api/leaderboard/update").permitAll()
                .antMatchers("/api/leaderboard/delete").permitAll()
                .antMatchers("/api/leaderboard/allTimes").permitAll()
                .antMatchers("/api/leaderboard/weekly").permitAll()
                .antMatchers("/api/leaderboard/monthly").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/swagger-ui/index.html").permitAll()
                .antMatchers("/swagger-ui/index.html").permitAll()
                .antMatchers("/swagger-ui").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .anyRequest().permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(mJwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                .csrf().disable();
    }

    /** The component which is an authentication provide and used at authentication related services.*/
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(mUserDetailsManager);
        daoAuthenticationProvider.setPasswordEncoder(mPasswordEncoder);
        return daoAuthenticationProvider;
    }

}
