package pl.thinkdata.droptop.database.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.thinkdata.droptop.csvconverter.model.CsvClient;
import pl.thinkdata.droptop.csvconverter.repository.CsvClientRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin")
public class CsvClientController {

    private final CsvClientRepository csvClientRepository;

    @GetMapping("/csv-clients")
    public String getCsvClients(Model model) {
        model.addAttribute("clients", csvClientRepository.findAll());
        return "database/csv-clients";
    }

    @PostMapping("/csv-clients")
    public String createCsvClient(@RequestParam String url,
                                  @RequestParam String hash,
                                  @RequestParam String logo) {
        CsvClient client = CsvClient.builder()
                .url(url)
                .hash(hash)
                .logo(logo)
                .build();
        csvClientRepository.save(client);
        return "redirect:/admin/csv-clients";
    }

    @PostMapping("/csv-clients/{id}/edit")
    public String updateCsvClient(@PathVariable Long id,
                                  @RequestParam String url,
                                  @RequestParam String hash,
                                  @RequestParam String logo) {
        CsvClient client = csvClientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CsvClient not found: " + id));
        client.setUrl(url);
        client.setHash(hash);
        client.setLogo(logo);
        csvClientRepository.save(client);
        return "redirect:/admin/csv-clients";
    }

    @PostMapping("/csv-clients/{id}/delete")
    public String deleteCsvClient(@PathVariable Long id) {
        csvClientRepository.deleteById(id);
        return "redirect:/admin/csv-clients";
    }
}