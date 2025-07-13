package pl.thinkdata.droptop.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_raport")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ImportRaport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_added_record")
    private Integer importAddedRecord;

    @Column(name = "import_updated_record")
    private Integer importUpdatedRecord;

    @Column(name = "import_date")
    private LocalDateTime importDate;

    @Column(name = "import_status", length = 10)
    private String importStatus;

    @Column(name = "import_error_message", columnDefinition = "TEXT")
    private String importErrorMessage;
}
