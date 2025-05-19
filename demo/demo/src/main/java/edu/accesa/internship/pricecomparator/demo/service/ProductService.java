package edu.accesa.internship.pricecomparator.demo.service;

import edu.accesa.internship.pricecomparator.demo.dto.ProductPriceHistoryDTO;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.repository.ProductPriceHistoryRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPriceHistoryRepository productPriceHistoryRepository;

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
}
