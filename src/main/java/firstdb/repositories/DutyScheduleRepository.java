package firstdb.repositories;

import firstdb.model.User;
import firstdb.services.schedule.DutySchedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DutyScheduleRepository extends JpaRepository<DutySchedule, Long> {
    void deleteAll();
    List<DutySchedule> findByDate(LocalDate date);
    List<DutySchedule> findAllByOrderByDateAsc();

    List<DutySchedule> findByParticipantAndDutyType(User participant, String dutyType);
    @EntityGraph(attributePaths = {"participant"})
    List<DutySchedule> findAll();
    List<DutySchedule> findByDateAndDutyType(LocalDate date, String dutyType);
}
