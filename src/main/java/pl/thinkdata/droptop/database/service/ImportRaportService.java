package pl.thinkdata.droptop.database.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.database.model.ImportProductRaport;
import pl.thinkdata.droptop.database.repository.ImportRaportRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportRaportService {

    private final ImportRaportRepository importRaportRepository;

    public List<ImportProductRaport> getImportRaport() {
        return importRaportRepository.findTop100ByOrderByImportDateDesc();
    }
}
