package firstdb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String fullname;
    private String email;
    private String role;
    private String phonenumber;

    public User(String username, String password, String fullname, String email, String role, String phonenumber) {
        super();
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.role = role;
        this.phonenumber = phonenumber;

    }

    public User() {
    }

    @Override
    public String toString() {
        return "UserDto [username=" + username + ", password=" + password + ", fullname=" +
                fullname + ", email=" + email + ", role=" + role + ",phonenumber=" + phonenumber + "]";
    }
}