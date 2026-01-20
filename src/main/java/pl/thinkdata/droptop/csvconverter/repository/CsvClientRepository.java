package pl.thinkdata.droptop.csvconverter.repository;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.csvconverter.model.CsvClient;

import java.util.Optional;

@Registered
public interface CsvClientRepository extends JpaRepository<CsvClient, Long> {

    Optional<CsvClient> findByHash(String hash);
}
