package pl.thinkdata.droptop.csvconverter.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class CsvProcessingService {

    public void process(InputStream input, OutputStream output) throws IOException {

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        ) {

            CSVParser parser = CSVFormat.DEFAULT
                    .withDelimiter(';')
                    .withFirstRecordAsHeader()
                    .parse(reader);

            CSVPrinter printer = new CSVPrinter(writer,
                    CSVFormat.DEFAULT
                            .withDelimiter(';')
                            .withHeader(
                                    "KodTowaru",
                                    "Stan",
                                    "Cena netto po rabacie"
                            )
            );

            for (CSVRecord record : parser) {
                printer.printRecord(
                        record.get("KodTowaru"),
                        convertStock(record.get("Stan")),
                        record.get("Cena netto po rabacie")
                );
            }

            printer.flush();
        }
    }

    private String convertStock(String stock) {
        return switch (stock) {
            case "1-2" -> "1";
            case "3-5" -> "3";
            case "6-20" -> "6";
            case "21-50" -> "21";
            case ">50" -> "50";
            default -> stock;
        };
    }
}