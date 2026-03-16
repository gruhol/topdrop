package pl.thinkdata.droptop.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.thinkdata.droptop.api.dto.GetStocksDto;
import pl.thinkdata.droptop.api.dto.PlatonResponse;

import java.util.List;

import static pl.thinkdata.droptop.common.mapper.ProductOfferLogMapper.map;

@Service
@RequiredArgsConstructor
public class CheckStockService {

    private final GetStocksExternalService getStockService;
    PlatonResponse data;

    @Transactional
    public boolean checkStockFromApi(List<Long> productsSupplierIds) {

            this.data = getStockService.get(getStocksDto(productsSupplierIds));
            if (data.getStock().getRecords() != null && !data.getStock().getRecords().isEmpty()) {
                return data.getStock().getRecords().stream()
                        .map(record -> map(record, "platon"))
                        .allMatch(stock -> stock.getStock() > 0);
            }
        return false;
    }

    private GetStocksDto getStocksDto(List<Long> productsSupplierIds) {
        return GetStocksDto.builder()
                .transactionNumber(1)
                .items(productsSupplierIds)
                .build();
    }
}
