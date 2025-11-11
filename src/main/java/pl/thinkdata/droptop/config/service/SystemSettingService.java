package pl.thinkdata.droptop.config.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.config.model.SystemSetting;
import pl.thinkdata.droptop.config.repository.SystemSettingRepository;

@Service
@RequiredArgsConstructor
public class SystemSettingService {

    private final SystemSettingRepository repo;

    public <T> T getValue(String key, Class<T> type) {
        String value = repo.findByKey(key)
                .map(SystemSetting::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Brak klucza: " + key));

        if (type == String.class) {
            return type.cast(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return type.cast(Boolean.parseBoolean(value));
        } else if (type == Integer.class || type == int.class) {
            return type.cast(Integer.parseInt(value));
        } else if (type == Double.class || type == double.class) {
            return type.cast(Double.parseDouble(value));
        } else {
            throw new IllegalArgumentException("Nieobsługiwany typ: " + type.getSimpleName());
        }
    }
}
