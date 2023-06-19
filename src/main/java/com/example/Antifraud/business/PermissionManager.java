package com.example.Antifraud.business;


import com.example.Antifraud.persistence.entities.UserEntity;
import com.example.Antifraud.persistence.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
public class PermissionManager {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/api/auth/user").permitAll()
                        .mvcMatchers("/api/auth/user/**").hasRole("ADMINISTRATOR")
                        .mvcMatchers("/api/auth/list").hasAnyRole("ADMINISTRATOR", "SUPPORT")
                        .antMatchers(HttpMethod.POST,"/api/antifraud/transaction").hasRole("MERCHANT")
                        .antMatchers("/api/auth/access").hasRole("ADMINISTRATOR")
                        .antMatchers("/api/auth/role").hasRole("ADMINISTRATOR")
                        .antMatchers("/actuator/shutdown").permitAll()
                        .antMatchers("/api/antifraud/suspicious-ip/**").hasRole("SUPPORT")
                        .antMatchers("/api/antifraud/stolencard/**").hasRole("SUPPORT")
                        .antMatchers("/api/antifraud/history/**").hasRole("SUPPORT")
                        .antMatchers(HttpMethod.PUT,"/api/antifraud/transaction").hasRole("SUPPORT")
                        .anyRequest().denyAll()
                )
                .formLogin(withDefaults())
                .httpBasic(withDefaults());
        http.csrf(kek-> kek.disable());
        return http.build();
    }
}


class UserDetailsImpl implements UserDetails {
    private final String username;
    private final String password;
    private final boolean unlocked;
    private final List<GrantedAuthority> rolesAndAuthorities;

    public UserDetailsImpl(UserEntity user) {
        username = user.getUsername();
        password = user.getPassword();
        this.unlocked = user.isUnlocked();
        rolesAndAuthorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println(rolesAndAuthorities);
        return rolesAndAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // 4 remaining methods that just return true
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return unlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}


@Service
class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userService.findOneByUsername(username.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("Not found: " + username);
        }
        return new UserDetailsImpl(user);
    }

}
