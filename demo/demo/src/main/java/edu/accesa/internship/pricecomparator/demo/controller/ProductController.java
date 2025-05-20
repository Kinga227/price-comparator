package edu.accesa.internship.pricecomparator.demo.controller;

import edu.accesa.internship.pricecomparator.demo.dto.ProductPriceHistoryDTO;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{productId}/price-history")
    public List<ProductPriceHistoryDTO> getPriceHistory(@PathVariable String productId) {
        return productService.getPriceHistory(productId);
    }

    @GetMapping("/price-history")
    public List<ProductPriceHistoryDTO> getPriceHistoryByFilters(
            @RequestParam(required = false) String store,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand
    ) {
        return productService.getPriceHistoryByFilters(store, category, brand);
    }
}
