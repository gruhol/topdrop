package pl.thinkdata.droptop.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.database.model.ImportProductRaport;
import pl.thinkdata.droptop.database.model.ImportTypeEnu;

import java.util.Optional;

public interface ImportRaportRepository extends JpaRepository<ImportProductRaport, Long> {

    Optional<ImportProductRaport> findFirstByImportStatusAndImportTypeOrderByImportDateDesc(String importStatus, ImportTypeEnu importType);
}
