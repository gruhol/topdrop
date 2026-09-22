--liquibase formatted sql

--changeset dabrowskiw:19
INSERT INTO system_settings (config_key, config_value, value_type, description)
SELECT 'packing_cost', '0', 'DOUBLE', 'Koszt pakowania doliczany do ceny hurtowej (PriceCalculator)'
WHERE NOT EXISTS (SELECT 1 FROM system_settings WHERE config_key = 'packing_cost');

--changeset dabrowskiw:20
INSERT INTO system_settings (config_key, config_value, value_type, description)
SELECT 'baselinker_markup_per_order', '0', 'DOUBLE', 'Narzut Baselinkera za zamówienie doliczany do ceny hurtowej (PriceCalculator)'
WHERE NOT EXISTS (SELECT 1 FROM system_settings WHERE config_key = 'baselinker_markup_per_order');

--changeset dabrowskiw:21
INSERT INTO system_settings (config_key, config_value, value_type, description)
SELECT 'global_margin', '0', 'DOUBLE', 'Globalna marża procentowa doliczana do ceny hurtowej (PriceCalculator)'
WHERE NOT EXISTS (SELECT 1 FROM system_settings WHERE config_key = 'global_margin');
