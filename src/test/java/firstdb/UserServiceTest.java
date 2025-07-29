//package firstdb;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//
//    @Test
//    public void shouldCreateAndDeleteUser() {
//        // Arrange
//        Long userId = 1L;
//
//        doNothing().when(userRepository).deleteById(userId);
//
//        UserDto userDto = new UserDto("testUser", "password", "fullName", "email@example.com", "USER");
//
//        User user = new User(userDto.getUsername(), "encodedPassword", userDto.getFullname(), userDto.getEmail(), userDto.getRole());
//
//        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        // Act
//        User createdUser = userService.save(userDto);
//        userService.deleteUserById(userId);
//
//        // Assert
//        assertNotNull(createdUser);
//        assertEquals("testUser", createdUser.getUsername());
//        assertEquals("encodedPassword", createdUser.getPassword());
//        assertEquals("fullName", createdUser.getFullname());
//        assertEquals("email@example.com", createdUser.getEmail());
//        assertEquals("USER", createdUser.getRole());
//
//        verify(userRepository, times(1)).deleteById(userId);
//
//        when(userRepository.existsById(userId)).thenReturn(false);
//        boolean exists = userRepository.existsById(userId);
//        assertFalse(exists);
//    }
//}