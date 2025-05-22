package edu.accesa.internship.pricecomparator.demo.controller;

import edu.accesa.internship.pricecomparator.demo.model.Discount;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.model.ProductPriceHistory;
import edu.accesa.internship.pricecomparator.demo.repository.DiscountRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductPriceHistoryRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductPriceHistoryRepository productPriceHistoryRepository;

    @BeforeEach
    void setup() {
        productPriceHistoryRepository.deleteAll();
        discountRepository.deleteAll();
        productRepository.deleteAll();

        Product product = new Product("p123", "TestProduct", "testCategory", "brandA", 1.0, "pcs", "RON", 10.0, "kaufland");
        productRepository.save(product);

        ProductPriceHistory history = new ProductPriceHistory();
        history.setProductId("p123");
        history.setStore("kaufland");
        history.setDate(LocalDate.now());
        history.setOriginalPrice(10.0);
        history.setCurrency("RON");
        history.setDiscountPercentage(20);
        history.setDiscountedPrice(8.0);
        productPriceHistoryRepository.save(history);
    }

    @Test
    void getPriceHistory_withStoreFilter_shouldReturnCorrectData() throws Exception {
        mockMvc.perform(get("/api/products/price-history")
                        .param("store", "kaufland")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].store").value("kaufland"))
                .andExpect(jsonPath("$[0].discountPercentage").value(20));
    }

    @Test
    void getPriceHistory_withWrongStore_shouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/products/price-history")
                        .param("store", "nonexistent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getRecommendedSubstitutes_shouldReturnSortedByEffectivePrice() throws Exception {
        Product original = new Product("p0", "Lapte", "lactate", "brand1",
                1.0, "l", "RON", 10.0, "kaufland");

        Product p1 = new Product("p1", "Lapte", "lactate", "brand2",
                1.0, "l", "RON", 9.5, "kaufland");
        Product p2 = new Product("p2", "Lapte", "lactate", "brand2",
                1.0, "l", "RON", 12.0, "kaufland");
        Product p3 = new Product("p3", "Lapte", "lactate", "brand2",
                1.0, "l", "RON", 15.0, "kaufland");

        productRepository.save(original);
        productRepository.save(p1);
        productRepository.save(p2);
        productRepository.save(p3);

        Discount d = new Discount();
        d.setProduct(p2);
        d.setPercentage(20);
        d.setStartDate(LocalDate.now().minusDays(1));
        d.setEndDate(LocalDate.now().plusDays(1));
        discountRepository.save(d);

        mockMvc.perform(get("/api/products/p0/recommendations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("p1"))
                .andExpect(jsonPath("$[1].id").value("p2"))
                .andExpect(jsonPath("$[2].id").value("p3"));
    }
}
