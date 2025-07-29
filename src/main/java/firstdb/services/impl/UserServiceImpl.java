package firstdb.services.impl;

import firstdb.services.DutyScheduleService;
import firstdb.services.UserService;
import firstdb.services.schedule.DutySchedule;
import firstdb.model.User;
import firstdb.services.user.UserDto;
import firstdb.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;



@Service
public class UserServiceImpl implements UserService {

    PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final DutyScheduleService scheduleService;

    public UserServiceImpl( PasswordEncoder passwordEncoder, UserRepository userRepository, DutyScheduleService scheduleService) {
        super();

        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.scheduleService = scheduleService;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(UserDto userDto) {
        User user = new User(
                userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getFullname(),
                userDto.getEmail(),
                userDto.getRole(),
                userDto.getPhonenumber());
        return userRepository.save(user);
    }
    public void deleteUserAndDependencies(Long userId) {
        List<DutySchedule> schedules = scheduleService.getAllSchedules();
        for (DutySchedule schedule : schedules) {
            scheduleService.deleteById(schedule.getId());
        }
        userRepository.deleteById(userId);
    }
}




