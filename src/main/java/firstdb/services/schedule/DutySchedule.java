package firstdb.services.schedule;

import firstdb.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "duty_schedule")
public class DutySchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "duty_schedule_seq")
    @SequenceGenerator(name = "duty_schedule_seq", sequenceName = "duty_schedule_id_seq", allocationSize = 1)

    private Long id;
    private LocalDate date;
    private String dutyType;
    @ManyToOne
    @JoinColumn(name = "participant_id")
    private User participant;
}
