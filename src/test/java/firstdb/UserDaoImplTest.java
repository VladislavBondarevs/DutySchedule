//package firstdb;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//public class UserDaoImplTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private UserDaoImpl userDao;
//    private User testUser;
//
//    @BeforeEach
//    void setUp(){
//        MockitoAnnotations.openMocks(this);
//        testUser = new User("testUser", "password", "Test User", "test@example.com", "USER");
//    }
//
//    @Test
//    public void whenFindById_thenReturnUser(){
//        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
//
//        Optional<User> foundUser = userDao.getUserById(1L);
//
//        assertThat(foundUser.isPresent()).isTrue();
//        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
//
//    }
//    @Test
//    public void whenSaveUser_thenUserIsSaved(){
//        when(userRepository.save(testUser)).thenReturn(testUser);
//
//        User savedUser = userDao.save(testUser);
//        testUser.setUsername("testUser");
//        testUser.setPassword("password123");
//        testUser.setEmail("test@example.com");
//
//        assertThat(savedUser).isNotNull();
//        assertThat(savedUser.getUsername()).isEqualTo(testUser.getUsername());
//
//    }
//}
