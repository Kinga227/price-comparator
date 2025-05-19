package edu.accesa.internship.pricecomparator.demo.service;

import edu.accesa.internship.pricecomparator.demo.dto.ProductPriceHistoryDTO;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.model.ProductPriceHistory;
import edu.accesa.internship.pricecomparator.demo.repository.ProductPriceHistoryRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    @Mock
    private ProductPriceHistoryRepository productPriceHistoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts_shouldReturnProductsFromRepository() {
        Product product = new Product("p1", "Test Product", "category", "brand", 1.0, "kg", "RON", 10.0, "kaufland");

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("p1", result.get(0).getId());

        verify(productRepository).findAll();
    }

    @Test
    void getPriceHistory_shouldReturnHistoryForGivenProduct() {
        ProductPriceHistory history = new ProductPriceHistory();
        history.setProductId("p1");
        history.setName("Test Product");
        history.setDate(LocalDate.now());
        history.setOriginalPrice(10.0);
        history.setDiscountPercentage(5);
        history.setDiscountedPrice(9.5);
        history.setCurrency("RON");
        history.setStore("kaufland");
        history.setCategory("category");
        history.setBrand("brand");

        when(productPriceHistoryRepository.findByProductIdOrderByDate("p1"))
                .thenReturn(List.of(history));

        List<ProductPriceHistoryDTO> result = productService.getPriceHistory("p1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("kaufland", result.get(0).getStore());
        assertEquals(5, result.get(0).getDiscountPercentage());

        verify(productPriceHistoryRepository).findByProductIdOrderByDate("p1");
    }

    @Test
    void getPriceHistoryByFilters_shouldReturnFilteredHistory() {
        ProductPriceHistory history = new ProductPriceHistory();
        history.setProductId("p1");
        history.setName("Test Product");
        history.setDate(LocalDate.now());
        history.setOriginalPrice(10.0);
        history.setDiscountPercentage(0);
        history.setDiscountedPrice(10.0);
        history.setCurrency("RON");
        history.setStore("kaufland");
        history.setCategory("category");
        history.setBrand("brand");

        when(productPriceHistoryRepository.findByFilters("kaufland", null, null))
                .thenReturn(List.of(history));

        List<ProductPriceHistoryDTO> result = productService.getPriceHistoryByFilters("kaufland", null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("kaufland", result.get(0).getStore());

        verify(productPriceHistoryRepository).findByFilters("kaufland", null, null);
    }

    @Test
    void getPriceHistoryByFilters_shouldReturnEmptyListWhenNoMatch() {
        when(productPriceHistoryRepository.findByFilters("lidl", null, null))
                .thenReturn(List.of());

        List<ProductPriceHistoryDTO> result = productService.getPriceHistoryByFilters("lidl", null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productPriceHistoryRepository).findByFilters("lidl", null, null);
    }
}
