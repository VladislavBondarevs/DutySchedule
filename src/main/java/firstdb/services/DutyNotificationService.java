package firstdb.services;

import firstdb.model.User;
import firstdb.services.impl.DutyScheduleServiceImpl;
import firstdb.services.schedule.DutySchedule;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DutyNotificationService {

    @Autowired
    private DutyScheduleServiceImpl scheduleService;

    @Autowired
    private EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

                //"0 * * * * ?" | "0 0 8 * * ?"
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyDutyNotifications() {
         LocalDate today = LocalDate.now();
         List<DutySchedule> todaySchedules = scheduleService.getSchedulesForDate(today);

         for (DutySchedule schedule : todaySchedules) {
            User participant = schedule.getParticipant();

            if (participant != null && participant.getEmail() != null && !participant.getEmail().isEmpty() && "Küche".equals(schedule.getDutyType())) {
            String email = participant.getEmail();
            String subject = "Ihr Küchendienst heute!";

            String confirmUrl = baseUrl + "/confirm_duty/" + schedule.getId();
            String declineUrl = baseUrl + "/decline_duty/" + schedule.getId();

            String text = "Sehr geehrte(r) " + participant.getFullname() + ",<br><br>"
            + "Wir möchten Sie daran erinnern, dass Sie heute, am " + today + ", für den Küchendienst eingeteilt sind.<br><br>"
            + "Bitte bestätigen Sie Ihre Teilnahme:<br>"
            + "<a href='" + confirmUrl + "'>Bestätigen</a> oder <a href='" + declineUrl + "'>Abwesend</a>.<br><br>"
            + "Vielen Dank und freundliche Grüße.";

            try {
                 emailService.sendEmail(email, subject, text);
                 System.out.println("E-Mail erfolgreich gesendet an: " + email);
                 } catch (MessagingException e) {
                 System.err.println("Fehler beim Senden der E-Mail an " + email + ": " + e.getMessage());
                 }
            }
         }
    }
}
