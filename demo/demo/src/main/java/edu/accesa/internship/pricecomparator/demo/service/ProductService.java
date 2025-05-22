package edu.accesa.internship.pricecomparator.demo.service;

import edu.accesa.internship.pricecomparator.demo.dto.ProductPriceHistoryDTO;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.repository.DiscountRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductPriceHistoryRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

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
}
