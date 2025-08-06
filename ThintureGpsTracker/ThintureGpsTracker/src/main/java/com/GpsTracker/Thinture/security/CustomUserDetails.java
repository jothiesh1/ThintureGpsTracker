package com.GpsTracker.Thinture.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.GpsTracker.Thinture.model.*;

public class CustomUserDetails implements UserDetails {

private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);

private String username;
private String password;
private Long id;  // Store ID for Admin, SuperAdmin, Dealer, User, or Client
private List<GrantedAuthority> authorities;

// Constructor for SuperAdmin
public CustomUserDetails(SuperAdmin superAdmin) {
this.username = superAdmin.getEmail();
this.password = superAdmin.getPassword();
this.id = superAdmin.getId();
this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
logger.info("Created CustomUserDetails for SuperAdmin: {} with ID: {}", username, id);
}

// Constructor for Admin
public CustomUserDetails(Admin admin) {
this.username = admin.getEmail();
this.password = admin.getPassword();
this.id = admin.getId();
this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
logger.info("Created CustomUserDetails for Admin: {} with ID: {}", username, id);
}

// Constructor for Dealer
public CustomUserDetails(Dealer dealer) {
this.username = dealer.getEmail();
this.password = dealer.getPassword();
this.id = dealer.getId();
this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_DEALER"));
logger.info("Created CustomUserDetails for Dealer: {} with ID: {}", username, id);
}

// Constructor for User
public CustomUserDetails(User user) {
this.username = user.getEmail();
this.password = user.getPassword();
this.id = user.getId();
this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
logger.info("Created CustomUserDetails for User: {} with ID: {}", username, id);
}

// Constructor for Client
public CustomUserDetails(Client client) {
this.username = client.getEmail();
this.password = client.getPassword();
this.id = client.getId();
this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"));
logger.info("Created CustomUserDetails for Client: {} with ID: {}", username, id);
}

// Getter for ID
public Long getId() {
return id;
}

@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
logger.debug("Fetching authorities for user: {}", username);
return authorities;
}

@Override
public String getPassword() {
logger.debug("Fetching password for user: {}", username);
return password;
}

@Override
public String getUsername() {
logger.debug("Fetching username: {}", username);
return username;
}

@Override
public boolean isAccountNonExpired() {
return true;
}

@Override
public boolean isAccountNonLocked() {
return true;
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













/* 
 * 
 * Role basedb\ authentication filter
 * 
 * 
 * 
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class CustomUserDetails implements UserDetails {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);

    private String username;
    private String password;
    private Long id;  // Store the Admin, SuperAdmin, Dealer, User, or Client ID
    private List<GrantedAuthority> authorities;

    // Constructor for SuperAdmin
    public CustomUserDetails(SuperAdmin superAdmin) {
        this.username = superAdmin.getEmail();
        this.password = superAdmin.getPassword();
        this.id = superAdmin.getId();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
        logger.info("Created CustomUserDetails for SuperAdmin: {} with ID: {}", username, id);
    }

    // Constructor for Admin
    public CustomUserDetails(Admin admin) {
        this.username = admin.getEmail();
        this.password = admin.getPassword();
        this.id = admin.getId();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        logger.info("Created CustomUserDetails for Admin: {} with ID: {}", username, id);
    }

    // Constructor for Dealer
    public CustomUserDetails(Dealer dealer) {
        this.username = dealer.getEmail();
        this.password = dealer.getPassword();
        this.id = dealer.getId();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_DEALER"));
        logger.info("Created CustomUserDetails for Dealer: {} with ID: {}", username, id);
    }

    // Constructor for User
    public CustomUserDetails(User user) {
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.id = user.getId();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        logger.info("Created CustomUserDetails for User: {} with ID: {}", username, id);
    }

    // **NEW: Constructor for Client**
    public CustomUserDetails(Client client) {
        this.username = client.getEmail();
        this.password = client.getPassword();
        this.id = client.getId();  // Set Client ID
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT"));
        logger.info("Created CustomUserDetails for Client: {} with ID: {}", username, id);
    }

    // Getter for ID
    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        logger.debug("Fetching authorities for user: {}", username);
        return authorities;
    }

    @Override
    public String getPassword() {
        logger.debug("Fetching password for user: {}", username);
        return password;
    }

    @Override
    public String getUsername() {
        logger.debug("Fetching username: {}", username);
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
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


*/



