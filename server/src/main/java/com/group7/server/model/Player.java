package com.group7.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/** Model of the player created after registration which includes credentials*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(name = "idgen", sequenceName = "PLAYER_SEQ")
@Entity
@Table(name="PLAYER")
public class Player extends BaseModel
        implements UserDetails, CredentialsContainer {

    /** Unique username of the player*/
    @NotNull
    @Column(name = "USERNAME", unique = true, nullable = false)
    private String username;

    /** Password of the player*/
    @NotNull
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    /** Unique email of the player*/
    @NotNull
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    /** Set Player with given id*/
    public Player(Long playerId) {
        this.setId(playerId);
    }

    /**
     * Methods implemented below are
     * necessary for authentication
     * and authorization properties
     * of the player.
     * */
    @Override
    public void eraseCredentials() {
        password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
