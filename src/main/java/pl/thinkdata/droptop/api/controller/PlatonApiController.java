package pl.thinkdata.droptop.api.controller;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.thinkdata.droptop.api.dto.GetPublicationsDto;
import pl.thinkdata.droptop.api.dto.GetStocksDto;
import pl.thinkdata.droptop.api.dto.PlatonResponse;
import pl.thinkdata.droptop.api.dto.UpdateProductInfo;
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
import pl.thinkdata.droptop.common.mapper.PlatonProductMapper;
import pl.thinkdata.droptop.common.repository.ProductOfferLogRepository;
import pl.thinkdata.droptop.common.repository.ProductRepository;
import pl.thinkdata.droptop.common.service.ImageService;
import pl.thinkdata.droptop.database.model.*;
import pl.thinkdata.droptop.database.model.product.Product;
import pl.thinkdata.droptop.database.model.product.SyncStatus;
import pl.thinkdata.droptop.database.repository.ImportRaportRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pl.thinkdata.droptop.common.mapper.ProductOfferLogMapper.map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api")
public class PlatonApiController {

    private final GetPublicationsExternalService getPublictionService;
    private final GetStocksExternalService getStockService;
    private final ProductRepository productRepository;
    private final ImportRaportRepository importRaportRepository;
    private final GetOrderDropExternalService getOrderDropExternalService;
    private final ApiProductService apiProductService;
    private final ProductOfferLogRepository productOfferLogRepository;
    private final PlatonProductMapper platonProductMapper;
    private final ImageService imageService;
    private final EntityManager entityManager;

    PlatonResponse data;

    @GetMapping("/getstocks")
    public String getStockFromApi(Model model) {
        int stockCount = getStockFromApi(10000);
        model.addAttribute("updated", stockCount);
        return "api/get_stocks";
    }

    @GetMapping("/getproducts")
    public String getProductsFromApi(Model model) {
        UpdateProductInfo productsFromApi = getProductsFromApi(10000);
        model.addAttribute("newprod", productsFromApi.getNewprod());
        model.addAttribute("update", productsFromApi.getUpdate());
        return "api/get_products";
    }

    public int getStockFromApi(int pageSize) {
        int pageNumber = 1;
        int downloadCount = 0;
        int total = 0;
        int totalStockSave = 0;
        do {
            GetStocksDto getStocksDto = GetStocksDto.builder()
                    .pageNo(pageNumber)
                    .pageSize(pageSize)
                    .lastChangeDate(getLastUpdate(ImportTypeEnu.STOCK))
                    .transactionNumber(1)
                    .build();
            this.data = getStockService.get(getStocksDto);
            if (!isNull(data.getMessage())) {
                saveImportRaport("Error", data.getMessage(), 0, 0, ImportTypeEnu.STOCK);
                break;
            }
            if (data.getStock().getRecords() != null && !data.getStock().getRecords().isEmpty()) {
                List<ProductOfferLog> stockToSave = data.getStock().getRecords().stream()
                        .map(record -> map(record, "platon"))
                        .toList();
                List<ProductOfferLog> productOfferLogs = productOfferLogRepository.saveAll(stockToSave);

                Set<String> uniqueEans = productOfferLogs.stream()
                        .map(ProductOfferLog::getProductEan)
                        .collect(Collectors.toSet());

                List<Product> products = productRepository.findByEanIn(uniqueEans);
                List<ProductOfferLog> offers = productOfferLogRepository.findTop2OffersByEans(uniqueEans);

                Map<String, List<ProductOfferLog>> offersByEan = offers.stream()
                        .collect(Collectors.groupingBy(ProductOfferLog::getProductEan));

                products.forEach(p -> {
                    List<ProductOfferLog> productOffers = offersByEan.getOrDefault(p.getEan(), new ArrayList<>());
                    p.setOffers(productOffers);
                });

                products.forEach(this::changeStatus);
                productRepository.saveAll(products);
                totalStockSave += stockToSave.size();

                // saveAll() już zacommitował zmiany we własnej transakcji; clear() tylko
                // zwalnia z pamięci encje zarządzane przez EntityManager (który przy
                // open-in-view=true żyje przez cały czas trwania żądania HTTP).
                entityManager.clear();
            }
            total = Optional.ofNullable(data.getStock().getSummary())
                    .map(Summary::getTotal)
                    .orElse(0);
            downloadCount += pageSize;
            pageNumber++;
        } while (total > downloadCount);

        if (isNull(this.data.getMessage())) {
            saveImportRaport("OK", null, totalStockSave, 0, ImportTypeEnu.STOCK);
        }
        return totalStockSave;
    }

    public UpdateProductInfo getProductsFromApi(int pageSize) {
        int pageNumber = 1;
        int downloadCount = 0;
        int total;
        int totalNew = 0;
        int totalUpdated = 0;

        do {
            this.data = getPublictionService.get(getRequestDto(pageSize, pageNumber));

            String covertUrl = Optional.ofNullable(this.data.getCatalog())
                    .map(Catalog::getRc)
                    .map(Rc::getUrlCoverBookLink)
                    .orElse("");

            if (!isNull(data.getMessage())) {
                saveImportRaport("Error", data.getMessage(), totalNew, totalUpdated, ImportTypeEnu.PRODUCT);
                break;
            }
            downloadCount += pageSize;
            pageNumber++;
            total = Optional.ofNullable(data.getCatalog())
                    .map(Catalog::getSummary)
                    .map(CatalogResponseSummary::getTotal)
                    .orElse(0);
            List<ProductFromXml> productFromXmls = Optional.ofNullable(data.getCatalog())
                    .map(Catalog::getRc)
                    .map(Rc::getProducts)
                    .orElse(Collections.emptyList());
            List<Product> pageProducts = productFromXmls.stream()
                    .map(p -> platonProductMapper.mapToProduct(p, covertUrl))
                    .filter(Objects::nonNull)
                    .toList();

            UpdateProductInfo pageResult = saveProductPage(pageProducts);
            totalNew += pageResult.getNewprod();
            totalUpdated += pageResult.getUpdate();

            log.info("Pobrano i zapisano z Platona: {} (łącznie nowych: {}, zaktualizowanych: {})",
                    downloadCount, totalNew, totalUpdated);
        }
        while (total > downloadCount);

        if (isNull(this.data.getMessage())) {
            saveImportRaport("OK", null, totalNew, totalUpdated, ImportTypeEnu.PRODUCT);
        }
        return UpdateProductInfo.builder()
                .newprod(totalNew)
                .update(totalUpdated)
                .build();
    }

    private UpdateProductInfo saveProductPage(List<Product> pageProducts) {
        if (pageProducts.isEmpty()) {
            return UpdateProductInfo.builder().newprod(0).update(0).build();
        }

        List<String> pageEans = pageProducts.stream()
                .map(Product::getEan)
                .distinct()
                .toList();

        Map<String, Product> existingByEan = new HashMap<>();
        for (int i = 0; i < pageEans.size(); i += 1000) {
            List<String> batch = pageEans.subList(i, Math.min(i + 1000, pageEans.size()));
            productRepository.findByEanIn(batch).forEach(p -> existingByEan.put(p.getEan(), p));
        }

        Map<String, Product> newByEan = new LinkedHashMap<>();
        Map<String, Product> updateByEan = new LinkedHashMap<>();

        for (Product downloaded : pageProducts) {
            String ean = downloaded.getEan();
            Product existing = existingByEan.get(ean);
            if (existing != null) {
                apiProductService.applyFields(existing, downloaded);
                existing.setSyncStatus(SyncStatus.TO_UPDATE);
                updateByEan.put(ean, existing);
            } else {
                Product staged = newByEan.get(ean);
                if (staged != null) {
                    apiProductService.applyFields(staged, downloaded);
                } else {
                    downloaded.setSyncStatus(SyncStatus.NEW);
                    newByEan.put(ean, downloaded);
                }
            }
        }

        List<Product> listOfSaveProducts = new ArrayList<>(newByEan.values());
        List<Product> productToUpdate = new ArrayList<>(updateByEan.values());

        productRepository.saveAll(listOfSaveProducts);
        productRepository.saveAll(productToUpdate);

        // saveAll() już zacommitował i sflushował zmiany we własnej transakcji;
        // clear() tylko zwalnia z pamięci encje zarządzane przez EntityManager
        // (który przy open-in-view=true żyje przez cały czas trwania żądania HTTP).
        entityManager.clear();

        return UpdateProductInfo.builder()
                .newprod(listOfSaveProducts.size())
                .update(productToUpdate.size())
                .build();
    }

    private GetPublicationsDto getRequestDto(int pageSize, int pageNumber) {
        return GetPublicationsDto.builder()
                .pageNo(pageNumber)
                .pageSize(pageSize)
                .lastChangeDate(getLastUpdate(ImportTypeEnu.PRODUCT))
                .transactionNumber(1)
                .build();
    }

    private void changeStatus(Product p) {
        if (nonNull(p.getExportLog())) {
            if (p.getOffers() != null && p.getOffers().size() > 2) {
                List<ProductOfferLog> sorted = p.getOffers().stream()
                        .sorted(Comparator.comparing(ProductOfferLog::getFetchedAt).reversed())
                        .toList();
                ProductOfferLog now = sorted.get(0);
                ProductOfferLog old = sorted.get(1);
                if (productChangePriceAndStock(now, old)) {
                    p.setSyncStatus(SyncStatus.PRICE_STOCK_UPDATE);
                    return;
                }
                if (productChangePrice(now, old)) {
                    p.setSyncStatus(SyncStatus.PRICE_UPDATE);
                    return;
                }
                if (productChangeStock(now, old)) {
                    p.setSyncStatus(SyncStatus.STOCK_UPDATE);
                    return;
                }
            }
            p.setSyncStatus(SyncStatus.TO_UPDATE);
        } else {
            p.setSyncStatus(SyncStatus.NEW);
        }
    }

    private boolean productChangePrice(ProductOfferLog now, ProductOfferLog old) {
        return !now.getWholesaleNetPrice().equals(old.getWholesaleNetPrice());
    }

    private boolean productChangeStock(ProductOfferLog now, ProductOfferLog old) {
        return !now.getStock().equals(old.getStock());
    }

    private boolean productChangePriceAndStock(ProductOfferLog now, ProductOfferLog old) {
        return productChangePrice(now, old) && productChangeStock(now, old);
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
                .deliveryMethod(42)
                .machineName("WAW430M")
                .build();

        OrderLine orderLine = OrderLine.builder()
                .supplierItemCode(eanOrder)
                .orderedQuantity(1)
                .build();

        OrderDropDto orderDropDto = OrderDropDto.builder()
                .orderNumber(Long.valueOf(idOrder))
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
