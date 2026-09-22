package pl.thinkdata.droptop.config.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.config.model.SystemSetting;
import pl.thinkdata.droptop.config.repository.SystemSettingRepository;
import pl.thinkdata.droptop.database.model.product.SyncStatus;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SystemSettingService {

    public static final String PACKING_COST = "packing_cost";
    public static final String BASELINKER_MARKUP_PER_ORDER = "baselinker_markup_per_order";
    public static final String GLOBAL_MARGIN = "global_margin";

    // zmiana któregoś z tych ustawień zmienia cenę każdego produktu
    private static final Set<String> PRICE_SETTINGS = Set.of(PACKING_COST, BASELINKER_MARKUP_PER_ORDER, GLOBAL_MARGIN);

    private final SystemSettingRepository systemSettingRepository;
    private final ProductRepository productRepository;

    public List<SystemSetting> findAll() {
        return systemSettingRepository.findAllByOrderByKeyAsc();
    }

    @Transactional
    public SystemSetting update(Long id, String value, String valueType, String description) {
        SystemSetting setting = systemSettingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ustawienia o id: " + id));
        boolean priceChanged = PRICE_SETTINGS.contains(setting.getKey()) && !Objects.equals(setting.getValue(), value);
        setting.setValue(value);
        setting.setValueType(valueType);
        setting.setDescription(description);
        SystemSetting saved = systemSettingRepository.save(setting);
        if (priceChanged) {
            markAllProductsForPriceUpdate();
        }
        return saved;
    }

    private void markAllProductsForPriceUpdate() {
        int withStock = productRepository.markExportedForPriceUpdate(SyncStatus.STOCK_UPDATE, SyncStatus.PRICE_STOCK_UPDATE);
        int synced = productRepository.markExportedForPriceUpdate(SyncStatus.SYNCED, SyncStatus.PRICE_UPDATE);
        log.info("Zmiana ustawień ceny - oznaczono {} produktów do aktualizacji ceny", withStock + synced);
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
