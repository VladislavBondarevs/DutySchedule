package firstdb.repositories;


import firstdb.model.User;
import firstdb.services.user.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    
    User save(UserDto userDto);

}