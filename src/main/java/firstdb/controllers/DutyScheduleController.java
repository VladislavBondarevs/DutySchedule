package firstdb.controllers;

import firstdb.services.EmailService;
import firstdb.services.schedule.DutySchedule;
import firstdb.services.DutyScheduleService;
import firstdb.model.User;
import firstdb.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DutyScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(DutyScheduleController.class);

    @Autowired
    private DutyScheduleService scheduleService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @Autowired
    public DutyScheduleController(DutyScheduleService scheduleService, UserService userService, EmailService emailService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/schedule")
    public String showSchedule(Model model, @RequestParam(required = false) Integer page) {
        int pageSize = 10;
        List<DutySchedule> allSchedules = scheduleService.getAllSchedules();
        List<User> participants = userService.getAllUsers();

        if (page == null) {
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
            int weekStartIndex = -1;

            for (int i = 0; i < allSchedules.size(); i++) {
                if (!allSchedules.get(i).getDate().isBefore(startOfWeek)) {
                    weekStartIndex = i;
                    break;
                }
            }
            page = (weekStartIndex >= 0) ? (weekStartIndex / pageSize) : 0;
        }
        int totalSchedules = allSchedules.size();
        int totalPages = (int) Math.ceil((double) totalSchedules / pageSize);

        if (page >= totalPages) {
            page = totalPages - 1;
        }
        if (page < 0) {
            page = 0;
        }

        int startItem = page * pageSize;
        List<DutySchedule> schedules;

        if (allSchedules.size() < startItem) {
            schedules = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, allSchedules.size());
            schedules = allSchedules.subList(startItem, toIndex);
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("participants", participants);
        model.addAttribute("newSchedule", new DutySchedule());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);


        return "schedule";
    }

    @PostMapping("/schedule")
    public String addNewSchedule(@ModelAttribute("newSchedule") DutySchedule schedule, HttpServletRequest request) {
        if (schedule.getParticipant() != null) {
            User participant = userService.findById(schedule.getParticipant().getId());
            if (participant != null) {
                schedule.setParticipant(participant);
            }
        }

        scheduleService.save(schedule);

        return "redirect:/calendar_view";
    }

    private void sendEmailToVertreter(DutySchedule schedule, HttpServletRequest request) {
        User vertreter = schedule.getParticipant();

        if (vertreter != null && vertreter.getEmail() != null && !vertreter.getEmail().isEmpty()) {
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String confirmUrl = baseUrl + "/confirm_duty/" + schedule.getId();
            String declineUrl = baseUrl + "/decline_duty/" + schedule.getId();

            String message = "Sehr geehrte(r) " + vertreter.getFullname() + ",<br><br>" +
                    "Sie wurden als Diensthabender für den " + schedule.getDate() + " eingeteilt.<br><br>" +
                    "Bitte bestätigen Sie Ihre Teilnahme:<br>" +
                    "<a href='" + confirmUrl + "'>Küche</a> oder <a href='" + declineUrl + "'>Abwesend</a>.<br><br>" +
                    "Vielen Dank und freundliche Grüße.";

            try {
                emailService.sendEmail(vertreter.getEmail(), "Änderung des Dienstplans", message);
                logger.info("E-Mail erfolgreich gesendet an: " + vertreter.getEmail());
            } catch (MessagingException e) {
                logger.warn("Fehler beim Senden der E-Mail: " + e.getMessage());
            }
        } else {
            logger.warn("E-Mail-Adresse für Vertreter fehlt. Keine E-Mail gesendet.");
        }
    }

    @GetMapping("/confirm_duty/{dutyId}")
    public String confirmDuty(@PathVariable Long dutyId) {
        DutySchedule schedule = scheduleService.findById(dutyId);
        if (schedule != null) {
            schedule.setDutyType("Küche");
            scheduleService.save(schedule);
            System.out.println("Dienst wurde als 'Küche' bestätigt für ID: " + dutyId);
        }
        return "redirect:/calendar_view";
    }

    @GetMapping("/decline_duty/{dutyId}")
    public String declineDuty(@PathVariable Long dutyId, Model model, HttpServletRequest request) {
        DutySchedule declinedSchedule = scheduleService.findById(dutyId);

        if (declinedSchedule != null) {
            declinedSchedule.setDutyType("Abwesend");
            scheduleService.save(declinedSchedule);
            logger.info("Duty Type für Dienst {} auf 'Abwesend' gesetzt.", declinedSchedule.getId());
        } else {
            logger.warn("Dienst mit ID {} nicht gefunden.", dutyId);
            model.addAttribute("errorMessage", "Dienst nicht gefunden.");
            return "redirect:/calendar_view";
        }

        List<DutySchedule> vertreterSchedules = scheduleService.getAllByDateAndDutyType(declinedSchedule.getDate(), "Vertreter");

        Set<String> sentEmails = new HashSet<>();

        for (DutySchedule vertreterSchedule : vertreterSchedules) {
            User vertreter = vertreterSchedule.getParticipant();

            if (vertreter != null && vertreter.getEmail() != null && !vertreter.getEmail().isEmpty()) {

                if (!sentEmails.contains(vertreter.getEmail())) {
                    try {
                        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                        String confirmUrl = baseUrl + "/confirm_duty/" + vertreterSchedule.getId();
                        String declineUrl = baseUrl + "/decline_duty/" + vertreterSchedule.getId();

                        String message = "Sehr geehrte(r) " + vertreter.getFullname() + ",<br><br>" +
                                "Der aktuelle Teilnehmer konnte nicht erscheinen. Sie wurden für den Dienst am " + declinedSchedule.getDate() + " eingeteilt.<br><br>" +
                                "Bitte bestätigen Sie Ihre Teilnahme:<br>" +
                                "<a href='" + confirmUrl + "'>Küche</a> oder <a href='" + declineUrl + "'>Abwesend</a>.<br><br>" +
                                "Vielen Dank und freundliche Grüße.";

                        emailService.sendEmail(vertreter.getEmail(), "Änderung des Dienstplans", message);
                        logger.info("Email sent to participant: {}", vertreter.getFullname());

                        sentEmails.add(vertreter.getEmail());
                    } catch (MessagingException e) {
                        logger.error("Fehler beim Senden der E-Mail an {}: {}", vertreter.getFullname(), e.getMessage());
                    }
                } else {
                    logger.info("Email bereits gesendet an: {}", vertreter.getEmail());
                }
            }
        }
        return "redirect:/calendar_view";
    }

    @GetMapping("/calendar_view")
    public String viewCalendar(Model model) {
        List<DutySchedule> dutySchedules = scheduleService.getAllSchedules();
        List<User> participants = userService.getAllUsers();
        List<Map<String, String>> events = new ArrayList<>();

        for (DutySchedule schedule : dutySchedules) {
            if (schedule.getParticipant() != null) {
                Map<String, String> event = new HashMap<>();
                event.put("title", schedule.getDutyType() + " - " + schedule.getParticipant().getFullname());
                event.put("start", schedule.getDate().toString());
                events.add(event);
            }
        }
        model.addAttribute("events", events);
        model.addAttribute("schedules", dutySchedules);
        model.addAttribute("participants", userService.getAllUsers());
        model.addAttribute("participants", participants);
        model.addAttribute("newSchedule", new DutySchedule());

        return "calendar_view";
    }

    @GetMapping("/delete_schedule/{id}")
    public String deleteSchedule(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            scheduleService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Duty schedule successfully deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the duty schedule.");
        }
        return "redirect:/schedule";
    }

    @GetMapping("/update_schedule/{id}")
    public String editSchedule(@PathVariable Long id, Model model) {
        DutySchedule schedule = scheduleService.findById(id);
        model.addAttribute("schedule", schedule);
        return "update_schedule";
    }

    @PostMapping("/update_schedule/{id}")
    public String updateSchedule(@PathVariable Long id, @ModelAttribute("schedule") DutySchedule updatedSchedule, HttpServletRequest request) {
        DutySchedule schedule = scheduleService.findById(id);
        String previousDutyType = schedule.getDutyType();

        schedule.setDutyType(updatedSchedule.getDutyType());
        scheduleService.save(schedule);

        if ("Abwesend".equals(schedule.getDutyType()) && "Abwesend".equals(updatedSchedule.getDutyType())) {
            logger.info("Duty type manually changed to 'Abwesend'. No email will be sent.");
            return "redirect:/schedule";
        }
        scheduleService.save(schedule);
        if (schedule.getParticipant() != null && schedule.getParticipant().getEmail() != null &&
                !schedule.getParticipant().getEmail().isEmpty() && !"Vertreter".equals(schedule.getDutyType())) {

            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String confirmUrl = baseUrl + "/confirm_duty/" + schedule.getId();
            String declineUrl = baseUrl + "/decline_duty/" + schedule.getId();

            String message = "Sehr geehrte(r) " + schedule.getParticipant().getFullname() + ",<br><br>" +
                    "Sie wurden als Diensthabender für den " + schedule.getDate() + " eingeteilt.<br><br>" +
                    "Bitte bestätigen Sie Ihre Teilnahme:<br>" +
                    "<a href='" + confirmUrl + "'>Küche</a> oder <a href='" + declineUrl + "'>Abwesend</a>.<br><br>" +
                    "Vielen Dank und freundliche Grüße.";
            try {
                emailService.sendEmail(schedule.getParticipant().getEmail(), "Änderung des Dienstplans", message);
                logger.info("E-Mail erfolgreich gesendet an: " + schedule.getParticipant().getEmail());
            } catch (MessagingException e) {
                logger.warn("Fehler beim Senden der E-Mail: " + e.getMessage());
            }
        } else {
            logger.warn("E-Mail-Adresse für Teilnehmer fehlt. Keine E-Mail gesendet.");
        }

        if ("Abwesend".equals(schedule.getDutyType()) && !"Abwesend".equals(previousDutyType)) {
            List<DutySchedule> vertreterSchedules = scheduleService.getAllByDateAndDutyType(schedule.getDate(), "Vertreter");

            Set<String> sentEmails = new HashSet<>();

            for (DutySchedule vertreterSchedule : vertreterSchedules) {
                User vertreter = vertreterSchedule.getParticipant();

                if (vertreter != null && vertreter.getEmail() != null && !vertreter.getEmail().isEmpty()) {

                    if (!sentEmails.contains(vertreter.getEmail())) {
                        try {
                            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                            String confirmUrl = baseUrl + "/confirm_duty/" + vertreterSchedule.getId();
                            String declineUrl = baseUrl + "/decline_duty/" + vertreterSchedule.getId();

                            String message = "Sehr geehrte(r) " + vertreter.getFullname() + ",<br><br>" +
                                    "Ein Teilnehmer hat für den " + schedule.getDate() + " als 'Abwesend' geantwortet.<br><br>" +
                                    "Bitte bestätigen Sie Ihre Teilnahme:<br>" +
                                    "<a href='" + confirmUrl + "'>Küche</a> oder <a href='" + declineUrl + "'>Abwesend</a>.<br><br>" +
                                    "Vielen Dank und freundliche Grüße.";

                            emailService.sendEmail(vertreter.getEmail(), "Änderung des Dienstplans", message);
                            logger.info("Email sent to participant: {}", vertreter.getFullname());

                            sentEmails.add(vertreter.getEmail());
                        } catch (MessagingException e) {
                            logger.error("Fehler beim Senden der E-Mail an {}: {}", vertreter.getFullname(), e.getMessage());
                        }
                    } else {
                        logger.info("Email bereits gesendet an: {}", vertreter.getEmail());
                    }
                }
            }
        }
        return "redirect:/schedule";
    }

    @PostMapping("/schedule/deleteAll")
    public String deleteAllSchedules() {
        scheduleService.deleteAllSchedules();
        return "redirect:/schedule";
    }
    @PostMapping("/schedule/manualAssignForYear")
    public String manualAssignForYear(@RequestParam Long participantId,
                                      @RequestParam String dayOfWeek,
                                      @RequestParam String dutyType) {
        User participant = userService.findById(participantId);
        DayOfWeek targetDay = DayOfWeek.valueOf(dayOfWeek.toUpperCase());

        LocalDate startDate = LocalDate.now().withDayOfYear(1);
        LocalDate endDate = startDate.withDayOfYear(startDate.lengthOfYear());

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (date.getDayOfWeek().equals(targetDay)) {
                DutySchedule dutySchedule = new DutySchedule();
                dutySchedule.setDate(date);
                dutySchedule.setParticipant(participant);
                dutySchedule.setDutyType(dutyType);

                scheduleService.save(dutySchedule);
            }
        }
        return "redirect:/schedule";
    }

    @GetMapping("/statistics_view")
    public String viewStatistics(Model model) {
        Map<User, Long> statistics = scheduleService.getShiftsStatistics();

        Map<User, Long> sortedStatistics = statistics.entrySet()
                .stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        model.addAttribute("statistics", sortedStatistics);
        return "statistics_view";
    }
    @PostMapping("/reassignShift")
    public String reassignShift(@RequestParam("scheduleId") Long scheduleId) {
        DutySchedule abwesendeSchicht = scheduleService.findById(scheduleId);

        if (abwesendeSchicht == null) {
            logger.warn("Schicht mit der angegebenen ID {} nicht gefunden.", scheduleId);
            return "redirect:/calendar_view";
        }

        if (!"Abwesend".equals(abwesendeSchicht.getDutyType())) {
            logger.info("Schicht mit ID {} ist nicht 'Abwesend' und erfordert keine Neuzuweisung.", scheduleId);
            return "redirect:/calendar_view";
        }
        scheduleService.reassignShiftForAbsentUser(abwesendeSchicht);
        logger.info("Schicht mit ID {} erfolgreich neu zugewiesen.", scheduleId);

        return "redirect:/calendar_view";
    }
}