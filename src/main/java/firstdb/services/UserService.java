package firstdb.services;

import firstdb.model.User;
import firstdb.services.user.UserDto;
import java.util.List;

public interface UserService {

    User findByUsername(String username);
    User save(UserDto userDto);
    List<User> getAllUsers();
    void deleteUserAndDependencies(Long userId);
    User findById(Long id);
}