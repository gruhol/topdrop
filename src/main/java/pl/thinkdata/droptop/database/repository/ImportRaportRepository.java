package pl.thinkdata.droptop.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.thinkdata.droptop.database.model.ImportRaport;

public interface ImportRaportRepository extends JpaRepository<ImportRaport, Long> {
}
