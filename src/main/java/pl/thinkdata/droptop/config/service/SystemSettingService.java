package pl.thinkdata.droptop.config.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.config.model.SystemSetting;
import pl.thinkdata.droptop.config.repository.SystemSettingRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    public List<SystemSetting> findAll() {
        return systemSettingRepository.findAllByOrderByKeyAsc();
    }

    public SystemSetting update(Long id, String value, String valueType, String description) {
        SystemSetting setting = systemSettingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ustawienia o id: " + id));
        setting.setValue(value);
        setting.setValueType(valueType);
        setting.setDescription(description);
        return systemSettingRepository.save(setting);
    }

    public SystemSetting create(String key, String value, String valueType, String description) {
        if (systemSettingRepository.findByKey(key).isPresent()) {
            throw new IllegalArgumentException("Klucz '" + key + "' już istnieje.");
        }
        SystemSetting setting = new SystemSetting();
        setting.setKey(key);
        setting.setValue(value);
        setting.setValueType(valueType);
        setting.setDescription(description);
        return systemSettingRepository.save(setting);
    }

    public void delete(Long id) {
        if (!systemSettingRepository.existsById(id)) {
            throw new IllegalArgumentException("Nie znaleziono ustawienia o id: " + id);
        }
        systemSettingRepository.deleteById(id);
    }

    public Optional<String> findValue(String key) {
        return systemSettingRepository.findByKey(key).map(SystemSetting::getValue);
    }

    public void upsertValue(String key, String value, String valueType, String description) {
        SystemSetting setting = systemSettingRepository.findByKey(key).orElseGet(SystemSetting::new);
        setting.setKey(key);
        setting.setValue(value);
        setting.setValueType(valueType);
        setting.setDescription(description);
        systemSettingRepository.save(setting);
    }

    public <T> T getValue(String key, Class<T> type) {
        String value = systemSettingRepository.findByKey(key)
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
