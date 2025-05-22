package edu.accesa.internship.pricecomparator.demo.service;

import edu.accesa.internship.pricecomparator.demo.dto.ProductPriceHistoryDTO;
import edu.accesa.internship.pricecomparator.demo.dto.ShoppingListDTO;
import edu.accesa.internship.pricecomparator.demo.model.Discount;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.model.ProductPriceHistory;
import edu.accesa.internship.pricecomparator.demo.repository.DiscountRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductPriceHistoryRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    @Mock
    private ProductPriceHistoryRepository productPriceHistoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private DiscountRepository discountRepository;

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

    private Discount createDiscount(Product product, int percentage) {
        Discount d = new Discount();
        d.setProduct(product);
        d.setPercentage(percentage);
        d.setStartDate(LocalDate.now().minusDays(1));
        d.setEndDate(LocalDate.now().plusDays(1));
        return d;
    }

    @Test
    void getRecommendedSubstitutes_shouldReturnSortedByEffectiveUnitPrice() {
        Product original = new Product("p1", "Lapte", null, null, 1,
                "l", "RON", 10.0, "kaufland");

        Product cheaper = new Product("p2", "Lapte", null, null, 1,
                "l", "RON", 12.0, "lidl");
        Product bigger = new Product("p3", "Lapte", null, null, 2,
                "l", "RON", 11.5, "kaufland");
        Product moreExpensive = new Product("p4", "Lapte", null, null, 1,
                "l", "RON", 12.5, "profi");

        when(productRepository.findById("p1")).thenReturn(Optional.of(original));
        when(productRepository.findByName("Lapte")).thenReturn(List.of(original, cheaper, bigger, moreExpensive));

        when(discountRepository.findFirstByProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq("p2"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Optional.of(createDiscount(cheaper, 20)));

        when(discountRepository.findFirstByProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq("p3"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        when(discountRepository.findFirstByProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq("p4"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        List<Product> result = productService.getRecommendedSubstitutes("p1");

        assertEquals(3, result.size());
        assertEquals("p3", result.get(0).getId());
        assertEquals("p2", result.get(1).getId());
        assertEquals("p4", result.get(2).getId());
    }

    @Test
    void getOptimizedShoppingLists_shouldGroupProductsByStore() {
        Product p1 = new Product("p1", "Lapte", "lactate", "Brand1", 1,
                "l", "RON", 8.0, "profi");
        Product p2 = new Product("p2", "Paine", "panificatie", "Brand2", 1,
                "buc", "RON", 4.0, "lidl");
        Product p3 = new Product("p3", "Unt", "lactate", "Brand3", 0.5,
                "kg", "RON", 10.0, "lidl");

        Product alt1 = new Product("a1", "Lapte", "lactate", "AltBrand", 1,
                "l", "RON", 7.5, "profi");
        Product alt2 = new Product("a2", "Paine", "panificatie", "AltBrand", 1,
                "buc", "RON", 3.5, "lidl");
        Product alt3 = new Product("a3", "Unt", "lactate", "AltBrand", 0.5,
                "kg", "RON", 9.0, "lidl");

        when(productRepository.findById("p1")).thenReturn(Optional.of(p1));
        when(productRepository.findById("p2")).thenReturn(Optional.of(p2));
        when(productRepository.findById("p3")).thenReturn(Optional.of(p3));

        when(productRepository.findByName("Lapte")).thenReturn(List.of(p1, alt1));
        when(productRepository.findByName("Paine")).thenReturn(List.of(p2, alt2));
        when(productRepository.findByName("Unt")).thenReturn(List.of(p3, alt3));

        when(discountRepository.findFirstByProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                any(), any(), any())).thenReturn(Optional.empty());

        List<String> productIds = List.of("p1", "p2", "p3");

        List<ShoppingListDTO> result = productService.getOptimizedShoppingLists(productIds);

        assertEquals(2, result.size());

        ShoppingListDTO lidlList = result.stream()
                .filter(dto -> dto.getStore().equals("lidl"))
                .findFirst()
                .orElse(null);

        assertNotNull(lidlList);
        assertEquals(2, lidlList.getProducts().size());

        ShoppingListDTO profiList = result.stream()
                .filter(dto -> dto.getStore().equals("profi"))
                .findFirst()
                .orElse(null);

        assertNotNull(profiList);
        assertEquals(1, profiList.getProducts().size());

        verify(productRepository).findById("p1");
        verify(productRepository).findById("p2");
        verify(productRepository).findById("p3");

        verify(productRepository).findByName("Lapte");
        verify(productRepository).findByName("Paine");
        verify(productRepository).findByName("Unt");
    }
}
