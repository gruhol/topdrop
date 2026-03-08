package pl.thinkdata.droptop.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.thinkdata.droptop.api.dto.GetStocksDto;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.database.model.ProductOfferLog;

import java.util.List;

import static pl.thinkdata.droptop.common.mapper.ProductOfferLogMapper.map;

@Service
@RequiredArgsConstructor
public class CheckStockService {

    private final GetStocksExternalService getStockService;
    PlatonResponse data;

    @Transactional
    public boolean getStockFromApi() {

            GetStocksDto getStocksDto = GetStocksDto.builder()
                    .transactionNumber(1)
                    .build();
            this.data = getStockService.get(getStocksDto);

            if (data.getStock().getRecords() != null && !data.getStock().getRecords().isEmpty()) {
                List<ProductOfferLog> stockToSave = data.getStock().getRecords().stream()
                        .map(record -> map(record, "platon"))
                        .toList();

            }

            //TODO

        return false;
    }
}
