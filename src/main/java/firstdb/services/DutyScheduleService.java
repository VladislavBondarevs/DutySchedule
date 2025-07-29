package firstdb.services;

import firstdb.model.User;
import firstdb.services.schedule.DutySchedule;
import java.time.LocalDate;

import java.util.List;
import java.util.Map;


public interface DutyScheduleService {
    List<DutySchedule> getAllSchedules();
    DutySchedule findById(Long id);
    void save(DutySchedule dutySchedule);
    void deleteById(Long id);
    void deleteAllSchedules();
    Map<User, Long> getShiftsStatistics();
    void reassignShiftForAbsentUser(DutySchedule absentSchedule);
    User findAvailableParticipantForReplacement(Long currentParticipantId);
    List<DutySchedule> getAllByDateAndDutyType(LocalDate date, String dutyType);
}
