package edu.accesa.internship.pricecomparator.demo.controller;

import edu.accesa.internship.pricecomparator.demo.dto.ProductPriceHistoryDTO;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.repository.ProductPriceHistoryRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{productId}/price-history")
    public List<ProductPriceHistoryDTO> getPriceHistory(@PathVariable String productId) {
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

    @GetMapping("/price-history")
    public List<ProductPriceHistoryDTO> getPriceHistoryByFilters(
            @RequestParam(required = false) String store,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand
    ) {
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
