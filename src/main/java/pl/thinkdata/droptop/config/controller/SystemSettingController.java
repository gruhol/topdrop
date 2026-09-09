package pl.thinkdata.droptop.config.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.thinkdata.droptop.config.service.SystemSettingService;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin/settings")
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @GetMapping
    public String settings(Model model) {
        model.addAttribute("settings", systemSettingService.findAll());
        return "settings/settings";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @RequestParam String value,
                          @RequestParam String valueType,
                          @RequestParam(required = false) String description,
                          RedirectAttributes redirectAttributes) {
        try {
            systemSettingService.update(id, value, valueType, description);
            redirectAttributes.addFlashAttribute("message", "Zapisano ustawienie.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/settings";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            systemSettingService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Usunięto ustawienie.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/settings";
    }

    @PostMapping
    public String create(@RequestParam String key,
                          @RequestParam String value,
                          @RequestParam String valueType,
                          @RequestParam(required = false) String description,
                          RedirectAttributes redirectAttributes) {
        try {
            systemSettingService.create(key, value, valueType, description);
            redirectAttributes.addFlashAttribute("message", "Dodano nowe ustawienie.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/settings";
    }
}
