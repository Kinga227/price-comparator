package edu.accesa.internship.pricecomparator.demo.service;

import edu.accesa.internship.pricecomparator.demo.dto.ProductPriceHistoryDTO;
import edu.accesa.internship.pricecomparator.demo.dto.ShoppingListDTO;
import edu.accesa.internship.pricecomparator.demo.model.Discount;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.repository.DiscountRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductPriceHistoryRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final DiscountRepository discountRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductPriceHistoryDTO> getPriceHistory(String productId) {
        return productPriceHistoryRepository.findByProductIdOrderByDate(productId)
                .stream()
                .map(history -> new ProductPriceHistoryDTO(
                        history.getName(),
                        history.getDate(),
                        history.getOriginalPrice(),
                        history.getDiscountPercentage(),
                        history.getDiscountedPrice(),
                        history.getCurrency(),
                        history.getStore(),
                        history.getCategory(),
                        history.getBrand()
                ))
                .toList();
    }

    public List<ProductPriceHistoryDTO> getPriceHistoryByFilters(String store, String category, String brand) {
        return productPriceHistoryRepository.findByFilters(store, category, brand)
                .stream()
                .map(history -> new ProductPriceHistoryDTO(
                        history.getName(),
                        history.getDate(),
                        history.getOriginalPrice(),
                        history.getDiscountPercentage(),
                        history.getDiscountedPrice(),
                        history.getCurrency(),
                        history.getStore(),
                        history.getCategory(),
                        history.getBrand()
                ))
                .toList();
    }

    private double getEffectivePrice(Product product) {
        LocalDate now = LocalDate.now();
        return discountRepository.findFirstByProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        product.getId(), now, now)
                .map(discount -> product.getPrice() * (1 - discount.getPercentage() / 100.0))
                .orElse(product.getPrice());
    }

    private double getUnitPrice(Product product) {
        if (product.getPackageQuantity() > 0) {
            return getEffectivePrice(product) / product.getPackageQuantity();
        }
        return Double.MAX_VALUE;
    }

    public List<Product> getRecommendedSubstitutes(String productId) {
        Product original = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Product> sameNameProducts = productRepository.findByName(original.getName());

        return sameNameProducts.stream()
                .filter(p -> !p.getId().equals(original.getId()))
                .filter(p -> p.getPackageUnit().equalsIgnoreCase(original.getPackageUnit()))
                .sorted(Comparator.comparingDouble(this::getUnitPrice))
                .limit(5)
                .toList();
    }

    public List<ShoppingListDTO> getOptimizedShoppingLists(List<String> basketProductIds) {
        Map<String, List<Product>> groupedByStore = new HashMap<>();

        for (String productId : basketProductIds) {
            Product original = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

            List<Product> alternatives = productRepository.findByName(original.getName());

            Product best = alternatives.stream()
                    .map(p -> {
                        double price = p.getPrice();
                        Optional<Discount> discountOptional = discountRepository
                                .findFirstByProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                        p.getId(), LocalDate.now(), LocalDate.now());
                        if (discountOptional.isPresent()) {
                            price = price * (1 - discountOptional.get().getPercentage() / 100.0);
                        }
                        return new AbstractMap.SimpleEntry<>(p, price);
                    })
                    .min(Comparator.comparingDouble(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .orElseThrow();

            groupedByStore.computeIfAbsent(best.getStore(), k -> new ArrayList<>()).add(best);
        }

        return groupedByStore.entrySet().stream()
                .map(e -> new ShoppingListDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
