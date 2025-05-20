package edu.accesa.internship.pricecomparator.demo.importer;

import edu.accesa.internship.pricecomparator.demo.model.Discount;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.model.ProductPriceHistory;
import edu.accesa.internship.pricecomparator.demo.repository.DiscountRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductPriceHistoryRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductRepository;
import edu.accesa.internship.pricecomparator.demo.service.PriceAlertService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CSVImporter {

    private final ResourceLoader resourceLoader;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final PriceAlertService priceAlertService;

    private final String[] productFiles = {
            "classpath:data/kaufland_2025-05-01.csv",
            "classpath:data/kaufland_2025-05-08.csv",
            "classpath:data/kaufland_2025-05-15.csv",
            "classpath:data/kaufland_2025-05-22.csv",
            "classpath:data/lidl_2025-05-01.csv",
            "classpath:data/lidl_2025-05-08.csv",
            "classpath:data/lidl_2025-05-15.csv",
            "classpath:data/lidl_2025-05-22.csv",
            "classpath:data/profi_2025-05-01.csv",
            "classpath:data/profi_2025-05-08.csv",
            "classpath:data/profi_2025-05-15.csv",
            "classpath:data/profi_2025-05-22.csv"
    };

    private final String[] discountFiles = {
            "classpath:data/kaufland_discounts_2025-05-01.csv",
            "classpath:data/kaufland_discounts_2025-05-08.csv",
            "classpath:data/kaufland_discounts_2025-05-15.csv",
            "classpath:data/kaufland_discounts_2025-05-22.csv",
            "classpath:data/lidl_discounts_2025-05-01.csv",
            "classpath:data/lidl_discounts_2025-05-08.csv",
            "classpath:data/lidl_discounts_2025-05-15.csv",
            "classpath:data/lidl_discounts_2025-05-22.csv",
            "classpath:data/profi_discounts_2025-05-01.csv",
            "classpath:data/profi_discounts_2025-05-08.csv",
            "classpath:data/profi_discounts_2025-05-15.csv",
            "classpath:data/profi_discounts_2025-05-22.csv"
    };

    @PostConstruct
    @Transactional
    public void importCSVData() throws Exception {
        Map<String, Product> productMap = new HashMap<>();
        Map<String, List<Discount>> discountMap = new HashMap<>();

        // read and save products
        for (String productFile : productFiles) {
            String store = extractStoreName(productFile);
            LocalDate fileDate = extractDateFromFilename(productFile);
            Resource resource = resourceLoader.getResource(productFile);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                // first line is the header
                reader.readLine();

                CSVParser csvParser = CSVFormat.DEFAULT
                        .withDelimiter(';')
                        .withTrim()
                        .parse(reader);

                for (CSVRecord record : csvParser) {
                    String id = record.get(0);
                    if (id.isBlank()) continue; // empty line

                    Product product = getProduct(record, id, store);

                    productRepository.save(product);
                    String key = store + "_" + id;
                    productMap.put(key, product);
                }
            }
        }

        // read and save discounts
        for (String discountFile : discountFiles) {
            String store = extractStoreName(discountFile);
            Resource resource = resourceLoader.getResource(discountFile);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                // first line is the header
                reader.readLine();

                CSVParser csvParser = CSVFormat.DEFAULT
                        .withDelimiter(';')
                        .withTrim()
                        .parse(reader);

                for (CSVRecord record : csvParser) {
                    String productId = record.get(0);
                    String key = store + "_" + productId;
                    Product product = productMap.get(key);
                    if (product == null) {
                        System.out.println("Unknown product ID in discounts: " + productId);
                        continue;
                    }

                    Discount discount = new Discount();
                    discount.setProduct(product);
                    discount.setStore(store);
                    discount.setStartDate(LocalDate.parse(record.get(6)));
                    discount.setEndDate(LocalDate.parse(record.get(7)));
                    discount.setPercentage(Integer.parseInt(record.get(8)));

                    discountRepository.save(discount);
                    discountMap.computeIfAbsent(key, k -> new ArrayList<>()).add(discount);
                }
            }
        }

        System.out.println("CSV data imported.");

        List<ProductPriceHistory> priceHistoryList = new ArrayList<>();

        // save price history with discounts
        for (String productFile : productFiles) {
            String store = extractStoreName(productFile);
            LocalDate fileDate = extractDateFromFilename(productFile);
            Resource resource = resourceLoader.getResource(productFile);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                reader.readLine();

                CSVParser csvParser = CSVFormat.DEFAULT
                        .withDelimiter(';')
                        .withTrim()
                        .parse(reader);

                for (CSVRecord record : csvParser) {
                    String id = record.get(0);
                    if (id.isBlank()) continue;

                    String key = store + "_" + id;
                    Product product = productMap.get(key);
                    if (product == null) continue;

                    double price = Double.parseDouble(record.get(6));
                    String currency = record.get(7);
                    String name = record.get(1);
                    String category = record.get(2);
                    String brand = record.get(3);

                    // check valid discount
                    Discount validDiscount = discountMap.getOrDefault(key, new ArrayList<>()).stream()
                            .filter(d -> !d.getStartDate().isAfter(fileDate) && !d.getEndDate().isBefore(fileDate))
                            .findFirst()
                            .orElse(null);

                    // save price in history
                    ProductPriceHistory history = new ProductPriceHistory();
                    history.setProductId(product.getId());
                    history.setName(name);
                    history.setStore(store);
                    history.setDate(fileDate);
                    history.setOriginalPrice(price);
                    history.setCurrency(currency);
                    history.setCategory(category);
                    history.setBrand(brand);

                    if (validDiscount != null) {
                        double discountedPrice = price * (1 - validDiscount.getPercentage() / 100.0);
                        history.setDiscountPercentage(validDiscount.getPercentage());
                        history.setDiscountedPrice(discountedPrice);
                    } else {
                        history.setDiscountPercentage(null);
                        history.setDiscountedPrice(null);
                    }

                    priceHistoryList.add(history);
                    productPriceHistoryRepository.save(history);
                }
            }
        }

        priceAlertService.checkAlerts(priceHistoryList);
    }

    private static Product getProduct(CSVRecord record, String id, String store) {
        Product product = new Product();
        product.setId(id);
        product.setStore(store);
        product.setName(record.get(1));
        product.setCategory(record.get(2));
        product.setBrand(record.get(3));
        product.setPackageQuantity(Double.parseDouble(record.get(4)));
        product.setPackageUnit(record.get(5));
        product.setPrice(Double.parseDouble(record.get(6)));
        product.setCurrency(record.get(7));
        return product;
    }

    private String extractStoreName(String path) {
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        return fileName.substring(0, fileName.indexOf('_'));
    }

    private LocalDate extractDateFromFilename(String path) {
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        String datePart = fileName.replaceAll("[^0-9\\-]", "");
        return LocalDate.parse(datePart);
    }
}
