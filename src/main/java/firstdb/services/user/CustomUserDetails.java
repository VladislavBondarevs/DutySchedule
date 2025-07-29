package firstdb.services.user;

import java.util.Collection;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    @Getter
    private String fullname;


    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
                             String fullname) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.fullname = fullname;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}