package pl.thinkdata.droptop.allegro.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.thinkdata.droptop.allegro.service.AllegroAuthService;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin/allegro/connect")
public class AllegroConnectController {

    private final AllegroAuthService allegroAuthService;

    @GetMapping
    public String status(Model model) {
        model.addAttribute("connected", allegroAuthService.isConnected());
        model.addAttribute("pending", allegroAuthService.getPendingAuthorization().orElse(null));
        return "allegro/connect";
    }

    @PostMapping("/start")
    public String start(RedirectAttributes redirectAttributes) {
        try {
            allegroAuthService.startDeviceAuthorization();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Błąd inicjalizacji połączenia: " + e.getMessage());
        }
        return "redirect:/admin/allegro/connect";
    }

    @PostMapping("/poll")
    public String poll(RedirectAttributes redirectAttributes) {
        String result = allegroAuthService.pollDeviceAuthorization();
        redirectAttributes.addFlashAttribute("pollResult", result);
        return "redirect:/admin/allegro/connect";
    }
}
