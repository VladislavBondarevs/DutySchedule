package firstdb.services.schedule;


import firstdb.services.EmailService;
import firstdb.services.UserService;
import firstdb.model.User;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class EmailSchedule {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

   // @Scheduled(cron = "0 0 10 * * ?")
    public void fetchUsersAndSendEmail()
    {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            try {
                emailService.sendEmail(user.getEmail(), "Du muss Heute arbeiten", "Erinnerung: Du hast heute Küchendienst.");
            } catch (MessagingException e) {
                System.out.println("Fehler beim sendung " + user.getEmail() + ". Fehler: " + e.getMessage());
            }
        }
    }
}