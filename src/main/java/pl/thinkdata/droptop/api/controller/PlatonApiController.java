package pl.thinkdata.droptop.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.thinkdata.droptop.api.dto.GetPublicationsDto;
import pl.thinkdata.droptop.api.dto.GetStocksDto;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.api.dto.catalog.Catalog;
import pl.thinkdata.droptop.api.dto.catalog.CatalogResponseSummary;
import pl.thinkdata.droptop.api.dto.catalog.ProductFromXml;
import pl.thinkdata.droptop.api.dto.catalog.Rc;
import pl.thinkdata.droptop.api.dto.orderDrop.DeliveryPoint;
import pl.thinkdata.droptop.api.dto.orderDrop.OrderDropDto;
import pl.thinkdata.droptop.api.dto.orderDrop.OrderLine;
import pl.thinkdata.droptop.api.dto.stock.Summary;
import pl.thinkdata.droptop.api.service.ApiProductService;
import pl.thinkdata.droptop.api.service.GetOrderDropExternalService;
import pl.thinkdata.droptop.api.service.GetPublicationsExternalService;
import pl.thinkdata.droptop.api.service.GetStocksExternalService;
import pl.thinkdata.droptop.common.repository.ProductOfferLogRepository;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.common.service.ImageService;
import pl.thinkdata.droptop.database.model.*;
import pl.thinkdata.droptop.database.repository.ImportRaportRepository;
import pl.thinkdata.droptop.mapper.ProductMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pl.thinkdata.droptop.mapper.ProductOfferLogMapper.map;

@Controller
@RequiredArgsConstructor
public class PlatonApiController {

    private final GetPublicationsExternalService getPublictionService;
    private final GetStocksExternalService getStockService;
    private final ProductRepository productRepository;
    private final ImportRaportRepository importRaportRepository;
    private final GetOrderDropExternalService getOrderDropExternalService;
    private final ApiProductService apiProductService;
    private final ProductOfferLogRepository productOfferLogRepository;
    private final ProductMapper productMapper;
    private final ImageService imageService;

    PlatonResponse data;

    @GetMapping("/stany")
    public String getStockFromApi(Model model) {
        int pageNumber = 1;
        int downloadCount = 0;
        int total  = 0;
        int totalStockSave = 0;
        do {
            GetStocksDto getStocksDto = GetStocksDto.builder()
                    .pageNo(pageNumber)
                    .pageSize(25000)
                    .lastChangeDate(getLastUpdate(ImportTypeEnu.STOCK))
                    .transactionNumber(1)
                    .build();
            this.data = getStockService.get(getStocksDto);
            if (!isNull(data.getMessage())) {
                saveImportRaport("Error", data.getMessage(), 0, 0, ImportTypeEnu.STOCK);
                break;
            }
            if (!data.getStock().getRecords().isEmpty()) {
                List<ProductOfferLog> stockToSave = data.getStock().getRecords().stream()
                        .map(record -> map(record, "platon"))
                        .toList();
                List<ProductOfferLog> productOfferLogs = productOfferLogRepository.saveAll(stockToSave);
                
                List<Product> listProductUpdateStatus = productOfferLogs.stream()
                        .map(ProductOfferLog::getProduct)
                        .filter(Objects::nonNull)
                        .peek(this::changeStatus)
                        .toList();
                productRepository.saveAll(listProductUpdateStatus);


                totalStockSave += stockToSave.size();
            }
            total = Optional.ofNullable(data.getStock().getSummary())
                    .map(Summary::getTotal)
                    .orElse(0);
            downloadCount += 25000;
            pageNumber++;
        } while (total > downloadCount);

        if (isNull(this.data.getMessage())) {
            saveImportRaport("OK", null, totalStockSave, 0, ImportTypeEnu.STOCK);
        }

        model.addAttribute("data", data);
        return "Test";
    }

    private void changeStatus(Product p) {
        if (nonNull(p.getExportLog())) {
            p.setSyncStatus(SyncStatus.TO_UPDATE);
        } else {
            p.setSyncStatus(SyncStatus.NEW);
        }
    }

    @GetMapping("/produkt")
    public String getProductsFromApi(Model model) {
        int pageNumber = 1;
        int downloadCount = 0;
        int total;
        List<Product> listOfSaveProducts = new ArrayList<>();
        List<Product> dowloadProducts = new ArrayList<>();
        Set<String> notDuplatedDownloadEan = new HashSet<>();
        List<Product> productToUpdate = new ArrayList<>();

        do {
            GetPublicationsDto getPublicationsDto = GetPublicationsDto.builder()
                    .pageNo(pageNumber)
                    .pageSize(25000)
                    .lastChangeDate(getLastUpdate(ImportTypeEnu.PRODUCT))
                    .transactionNumber(1)
                    .build();
            this.data = getPublictionService.get(getPublicationsDto);

            String covertUrl = Optional.ofNullable(this.data.getCatalog())
                    .map(Catalog::getRc)
                    .map(Rc::getUrlCoverBookLink)
                    .orElse("");

            if (!isNull(data.getMessage())) {
                saveImportRaport("Error", data.getMessage(), 0, 0, ImportTypeEnu.PRODUCT);
                break;
            }
            downloadCount += 25000;
            pageNumber++;
            total = Optional.ofNullable(data.getCatalog())
                    .map(Catalog::getSummary)
                    .map(CatalogResponseSummary::getTotal)
                    .orElse(0);
            List<ProductFromXml> productFromXmls = Optional.ofNullable(data.getCatalog())
                    .map(Catalog::getRc)
                    .map(Rc::getProducts)
                    .orElse(Collections.emptyList());
            productFromXmls.stream()
                    .map(p -> productMapper.mapToProduct(p, covertUrl))
                    .filter(Objects::nonNull)
                    .forEach(dowloadProducts::add);
        }
        while (total > downloadCount);

        List<String> dowloadEans = dowloadProducts.stream()
                .map(Product::getEan)
                .toList();

        Set<String> findEanInBase = productRepository.findByEanIn(dowloadEans).stream()
                .map(Product::getEan)
                .collect(Collectors.toSet());

        for (Product prod : dowloadProducts) {
            if (findEanInBase.contains(prod.getEan())) {
                prod.setSyncStatus(SyncStatus.TO_UPDATE);
                productToUpdate.add(prod);
            } else if (notDuplatedDownloadEan.contains(prod.getEan())) {
                prod.setSyncStatus(SyncStatus.TO_UPDATE);
                productToUpdate.add(prod);
            } else {
                prod.setSyncStatus(SyncStatus.NEW);
                listOfSaveProducts.add(prod);
            }
            notDuplatedDownloadEan.add(prod.getEan());
        }

        productRepository.saveAll(listOfSaveProducts);
        apiProductService.updateAll(productToUpdate);

        if (isNull(this.data.getMessage())) {
            saveImportRaport("OK", null, listOfSaveProducts.size(), productToUpdate.size(), ImportTypeEnu.PRODUCT);
        }
        model.addAttribute("data", listOfSaveProducts);
        return "Test";
    }

    private LocalDateTime getLastUpdate(ImportTypeEnu importType) {
        return importRaportRepository.findFirstByImportStatusAndImportTypeOrderByImportDateDesc("OK", importType)
                .map(ImportProductRaport::getImportDate)
                .orElse(null);
    }

    private void saveImportRaport(String status, String message, int addedTotal, int updateTotal, ImportTypeEnu type) {
        ImportProductRaport importProductRaport = ImportProductRaport.builder()
                .importDate(LocalDateTime.now())
                .importAddedRecord(addedTotal)
                .importUpdatedRecord(updateTotal)
                .importType(type)
                .importStatus(status)
                .importErrorMessage(message)
                .build();
        importRaportRepository.save(importProductRaport);
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
                .deliveryMethod(16)
                .machineName("WAW430M")
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

    @GetMapping("/get/{dir}/{filename}")
    public ResponseEntity<Resource> serveImages(@PathVariable String dir, @PathVariable String filename) {
        return imageService.serveImages(filename, dir);
    }
}
