package firstdb.services.user;

import lombok.Getter;
import lombok.Setter;



import java.beans.ConstructorProperties;

@Getter
@Setter
public class UserDto {

    private String username;
    private String password;
    private String fullname;
    private String email;
    private String role;
    private String phonenumber;


    @ConstructorProperties({"username", "password", "fullname", "email","role", "phonenumber"})
    public UserDto(String username, String password, String fullname, String email ,String role, String phonenumber) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.role = role;
        this.phonenumber = phonenumber;
    }

    @Override
    public String toString() {
        return "UserDto [username=" + username + ", password=" + password + ", fullname=" +
                fullname + ", email=" + email + ", role=" + role + ", phonenumber=" + phonenumber  + "]";
    }
}
