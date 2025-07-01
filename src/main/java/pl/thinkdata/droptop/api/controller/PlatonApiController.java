package pl.thinkdata.droptop.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.thinkdata.droptop.api.dto.GetPublicationsDto;
import pl.thinkdata.droptop.api.dto.GetStocksDto;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.api.dto.orderDrop.DeliveryPoint;
import pl.thinkdata.droptop.api.dto.orderDrop.OrderDropDto;
import pl.thinkdata.droptop.api.dto.orderDrop.OrderLine;
import pl.thinkdata.droptop.api.service.GetOrderDropExternalService;
import pl.thinkdata.droptop.api.service.GetPublicationsExternalService;
import pl.thinkdata.droptop.api.service.GetStocksExternalService;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.database.model.ImportRaport;
import pl.thinkdata.droptop.database.model.Product;
import pl.thinkdata.droptop.database.repository.ImportRaportRepository;
import pl.thinkdata.droptop.mapper.ProductMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.isNull;

@Controller
@RequiredArgsConstructor
public class PlatonApiController {

    private final GetPublicationsExternalService getPublictionService;
    private final GetStocksExternalService getStockService;
    private final ProductRepository productRepository;
    private final ImportRaportRepository importRaportRepository;
    private final GetOrderDropExternalService getOrderDropExternalService;

    PlatonResponse data;

    @GetMapping("/stany")
    public String getStockFromApi(Model model) {
        GetStocksDto getStocksDto = GetStocksDto.builder()
                .pageNo("1")
                .pageSize("10")
                .lastChangeDate(LocalDateTime.of(2024,01,01, 12, 11, 2, 33))
                .transactionNumber(1)
                .build();
        PlatonResponse data = getStockService.get(getStocksDto);
        model.addAttribute("data", data);

        return "Test";
    }

    @GetMapping("/produkt")
    public String getProductsFromApi(Model model) {
        int pageNumber = 1;
        int downloadCount = 0;
        int total  = 0;
        List<Product> listOfSaveProducts = new ArrayList<>();

        do {
            GetPublicationsDto getPublicationsDto = GetPublicationsDto.builder()
                    .pageNo(String.valueOf(pageNumber))
                    .pageSize("1000")
                    .lastChangeDate(getLastUpdate())
                    .transactionNumber(1)
                    .build();
            this.data = getPublictionService.get(getPublicationsDto);

            if (!isNull(data.getMessage())) {
                saveImportRaport("Error", data.getMessage(), total);
                break;
            }
            downloadCount += 1000;
            pageNumber++;
            total = data.getCatalog().getSummary().getTotal();
            data.getCatalog().getRc().getProducts().stream()
                    .map(ProductMapper::mapToProduct)
                    .filter(Objects::nonNull)
                    .forEach(listOfSaveProducts::add);
            productRepository.saveAll(listOfSaveProducts);
        }
        while (total > downloadCount);

        if (isNull(this.data.getMessage())) {
            saveImportRaport("OK", null, total);
        }
        model.addAttribute("data", listOfSaveProducts);
        return "Test";
    }

    private LocalDateTime getLastUpdate() {
        return importRaportRepository.findFirstByImportStatusOrderByImportDateDesc("OK")
                .map(ImportRaport::getImportDate)
                .orElse(null);
    }

    private void saveImportRaport(String status, String message, int total) {
        ImportRaport importRaport = ImportRaport.builder()
                .importDate(LocalDateTime.now())
                .importRecord(total)
                .importStatus(status)
                .importErrorMessage(message)
                .build();
        importRaportRepository.save(importRaport);
    }

    @GetMapping("/order-drop/{idOrder}/{eanOrder}")
    public String sendOrder(@PathVariable String idOrder, @PathVariable String eanOrder, Model model) {
        DeliveryPoint deliveryPoint = DeliveryPoint.builder()
                .customerKind(1)
                .name("Wojciech")
                .surname("Dąbrowski")
                .street("Strzygłowska")
                .homeNumber("58D")
                .cityName("Warszawa")
                .postCode("04-872")
                .post("Warszawa")
                .email("dabrowskiw@gmail.com")
                .phone("+48662078402")
                .country("PL")
                .deliveryMethod(42)
                .build();

        OrderLine orderLine = OrderLine.builder()
                .supplierItemCode(eanOrder)
                .orderedQuantity(1)
                .build();

        OrderDropDto orderDropDto = OrderDropDto.builder()
                .orderNumber(idOrder)
                .orderDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .accountNumber("30418")
                .deliveryPoint(deliveryPoint)
                .orderLine(Arrays.asList(orderLine))
                .orderRemarks("Uwagi do zamówienia")
                .build();
        PlatonResponse data = getOrderDropExternalService.get(orderDropDto);

        model.addAttribute("data", data);
        return "Test";
    }
}
