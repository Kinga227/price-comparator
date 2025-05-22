package edu.accesa.internship.pricecomparator.demo.controller;

import edu.accesa.internship.pricecomparator.demo.dto.ShoppingListDTO;
import edu.accesa.internship.pricecomparator.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/basket")
@RequiredArgsConstructor
public class BasketController {

    private final ProductService productService;

    @PostMapping("/optimize")
    public List<ShoppingListDTO> optimize(@RequestBody List<String> productIds) {
        return productService.getOptimizedShoppingLists(productIds);
    }
}
