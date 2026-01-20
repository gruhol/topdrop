package pl.thinkdata.droptop.csvconverter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "csv_client")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CsvClient {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "url", nullable = false, length = 512)
    private String url;

    @Column(name = "hash", nullable = false, length = 255)
    private String hash;

    @Column(name = "logo", nullable = false, length = 20, unique = true)
    private String logo;
}
