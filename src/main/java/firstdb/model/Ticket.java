package firstdb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Setter
@Getter
@Entity
public class Ticket {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;



    public Ticket() {
    }

    public Ticket(String title, String description, String status, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;

    }
}
