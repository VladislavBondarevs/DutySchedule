package firstdb.services.impl;

import firstdb.model.User;
import firstdb.repositories.UserRepository;
import firstdb.services.DutyScheduleService;
import firstdb.services.EmailService;
import firstdb.services.schedule.DutySchedule;

import firstdb.repositories.DutyScheduleRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class DutyScheduleServiceImpl implements DutyScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(DutyScheduleServiceImpl.class);

    private final Set<Long> notifiedParticipants = new HashSet<>();
    private final UserRepository userRepository;


    @Autowired
    private DutyScheduleRepository dutyScheduleRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    public DutyScheduleServiceImpl(UserRepository userRepository, DutyScheduleRepository dutyScheduleRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.dutyScheduleRepository = dutyScheduleRepository;
        this.emailService = emailService;

    }
    @Override
    public List<DutySchedule> getAllByDateAndDutyType(LocalDate date, String dutyType) {
        return dutyScheduleRepository.findByDateAndDutyType(date, dutyType);
    }

    @Override
    public void save(DutySchedule dutySchedule) {
        dutyScheduleRepository.save(dutySchedule);
    }

    @Override
    public List<DutySchedule> getAllSchedules() {
        return dutyScheduleRepository.findAllByOrderByDateAsc();
    }

    @Override
    public DutySchedule findById(Long id) {
        return dutyScheduleRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        dutyScheduleRepository.deleteById(id);
    }

    public DutySchedule update(Long id, DutySchedule updatedSchedule) {
        return dutyScheduleRepository.findById(id)
                .map(schedule -> {
                    schedule.setParticipant(updatedSchedule.getParticipant());
                    schedule.setParticipant(updatedSchedule.getParticipant());
                    return dutyScheduleRepository.save(schedule);
                }).orElseThrow(() -> new EntityNotFoundException("Duty schedule not found"));
    }
    @Override
    public void deleteAllSchedules() {
        dutyScheduleRepository.deleteAll();
    }

    public List<DutySchedule> getSchedulesForDate(LocalDate date) {
        return dutyScheduleRepository.findByDate(date);
    }

    @Override
    public Map<User, Long> getShiftsStatistics() {
        Map<User, Long> statistics = new HashMap<>();

        List<DutySchedule> allSchedules = dutyScheduleRepository.findAll();

        for (DutySchedule schedule : allSchedules) {
            User participant = schedule.getParticipant();
            Long currentCount = statistics.getOrDefault(participant, 0L);

            if ("Abwesend".equals(schedule.getDutyType())) {
                statistics.put(participant, currentCount - 1);
            } else {
                statistics.put(participant, currentCount + 1);
            }
        }
        return statistics;
    }

    public User findAvailableParticipantForReplacement(Long currentParticipantId) {
        List<User> participants = userRepository.findAll();
        Collections.reverse(participants);

        logger.debug("Liste aller Teilnehmer (insgesamt {}): {}", participants.size(), participants);
        logger.debug("Liste der IDs der benachrichtigten Teilnehmer vor der Überprüfung (insgesamt {}): {}", notifiedParticipants.size(), notifiedParticipants);

        for (User participant : participants) {
            logger.debug("Überprüfung des Teilnehmers: {} mit ID: {}", participant.getFullname(), participant.getId());


            if (participant.getId().equals(currentParticipantId) || notifiedParticipants.contains(participant.getId())) {
                logger.debug("Teilnehmer {} übersprungen, da er bereits benachrichtigt wurde oder aktuell ist.", participant.getFullname());
                continue;
            }

            List<DutySchedule> absentSchedules = dutyScheduleRepository.findByParticipantAndDutyType(participant, "Abwesend");
            absentSchedules = absentSchedules.stream()
                    .filter(schedule -> schedule.getDate().equals(LocalDate.now()))
                    .collect(Collectors.toList());

            logger.debug("Abwesenheitseinträge für Teilnehmer {}: {}", participant.getFullname(), absentSchedules);

            if (absentSchedules.isEmpty()) {
                notifiedParticipants.add(participant.getId());
                logger.info("Teilnehmer für Ersatz ausgewählt: {}", participant.getFullname());
                return participant;
            } else {
                logger.debug("Teilnehmer {} abgelehnt, da er einen 'Abwesend'-Eintrag hat.", participant.getFullname());
            }
        }

        if (notifiedParticipants.size() == participants.size()) {
            logger.info("Alle Teilnehmer wurden benachrichtigt; Zurücksetzen der Benachrichtigungsliste.");
            notifiedParticipants.clear();
        }

        logger.warn("Kein verfügbarer Teilnehmer für Ersatz gefunden.");
        return null;
    }

    public void reassignShiftForAbsentUser(DutySchedule abwesendeSchicht) {
        logger.info("Aufruf der Ersatzteilnehmerzuweisung für Dienst mit ID: {}", abwesendeSchicht.getId());

        User ersatzteilnehmer = findAvailableParticipantForReplacement(abwesendeSchicht.getParticipant().getId());
        if (ersatzteilnehmer != null) {
            DutySchedule ersatzSchicht = new DutySchedule();
            ersatzSchicht.setDate(abwesendeSchicht.getDate());
            ersatzSchicht.setDutyType("Küche");
            ersatzSchicht.setParticipant(ersatzteilnehmer);

            dutyScheduleRepository.save(ersatzSchicht);

            logger.info("Neuer Teilnehmer erfolgreich zugewiesen: {}", ersatzteilnehmer.getFullname());

            if (ersatzteilnehmer.getEmail() != null && !ersatzteilnehmer.getEmail().isEmpty()) {
                try {
                    emailService.sendEmail(
                            ersatzteilnehmer.getEmail(),
                            "Änderung des Dienstplans",
                            "Hallo " + ersatzteilnehmer.getFullname() + ",\n\n"
                                    + "Sie wurden als Diensthabender für den " + abwesendeSchicht.getDate() + " eingeteilt.\n"
                                    + "Vielen Dank und freundliche Grüße."
                    );
                    logger.info("E-Mail erfolgreich gesendet an: {}", ersatzteilnehmer.getEmail());
                } catch (MessagingException e) {
                    logger.error("Fehler beim Senden der E-Mail: {}", e.getMessage());
                }
            } else {
                logger.info("Ersatzteilnehmer hat keine E-Mail, daher keine E-Mail gesendet.");
            }
        } else {
            logger.info("Kein verfügbarer Teilnehmer für die Vertretung gefunden.");
        }
    }
}



