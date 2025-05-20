package edu.accesa.internship.pricecomparator.demo.controller;

import edu.accesa.internship.pricecomparator.demo.model.PriceAlert;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.repository.DiscountRepository;
import edu.accesa.internship.pricecomparator.demo.repository.PriceAlertRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PriceAlertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private PriceAlertRepository priceAlertRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        discountRepository.deleteAll();
        priceAlertRepository.deleteAll();
        productRepository.deleteAll();

        Product product = new Product();
        product.setId("p1");
        product.setName("Lapte");
        product.setBrand("Brand Lapte");
        product.setCategory("lactate");
        product.setPackageQuantity(1);
        product.setPackageUnit("l");
        product.setPrice(10.0);
        product.setCurrency("RON");
        product.setStore("kaufland");
        productRepository.save(product);
    }

    @Test
    void createAlert_shouldPersistAndReturnOk() throws Exception {
        String json = """
                {
                    "productId": "p1",
                    "targetPrice": 8.5,
                    "userEmail": "test@example.com"
                }
                """;

        mockMvc.perform(post("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        List<PriceAlert> alerts = priceAlertRepository.findAll();
        assertEquals(1, alerts.size());
        assertEquals("test@example.com", alerts.get(0).getUserEmail());
    }

    @Test
    void getAlerts_shouldReturnSavedAlerts() throws Exception {
        PriceAlert alert = new PriceAlert();
        alert.setProductId("p1");
        alert.setUserEmail("test@example.com");
        alert.setTargetPrice(8.0);
        priceAlertRepository.save(alert);

        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userEmail").value("test@example.com"))
                .andExpect(jsonPath("$[0].targetPrice").value(8.0));

    }
}
