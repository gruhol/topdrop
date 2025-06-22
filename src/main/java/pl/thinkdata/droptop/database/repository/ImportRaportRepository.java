package pl.thinkdata.droptop.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.database.model.ImportRaport;

import java.util.Optional;

public interface ImportRaportRepository extends JpaRepository<ImportRaport, Long> {

    Optional<ImportRaport> findFirstByImportStatusOrderByImportDateDesc(String importStatus);
}
