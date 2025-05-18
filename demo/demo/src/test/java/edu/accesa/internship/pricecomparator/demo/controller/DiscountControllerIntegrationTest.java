package edu.accesa.internship.pricecomparator.demo.controller;

import edu.accesa.internship.pricecomparator.demo.model.Discount;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.repository.DiscountRepository;
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
public class DiscountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        discountRepository.deleteAll();
        productRepository.deleteAll();

        Product product = new Product();
        product.setId("p1");
        product.setName("Test Product");
        product.setBrand("BrandX");
        product.setCategory("CategoryY");
        product.setPackageQuantity(1);
        product.setPackageUnit("pcs");
        product.setPrice(100);
        product.setCurrency("RON");
        product.setStore("kaufland");
        productRepository.save(product);

        Discount discount = new Discount();
        discount.setProduct(product);
        discount.setStore("kaufland");
        discount.setStartDate(LocalDate.now().minusDays(1));
        discount.setEndDate(LocalDate.now().plusDays(1));
        discount.setPercentage(40);
        discountRepository.save(discount);
    }

    @Test
    void getAllDiscounts_shouldReturnDiscountList() throws Exception {
        mockMvc.perform(get("/api/discounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("p1"))
                .andExpect(jsonPath("$[0].percentage").value(40))
                .andExpect(jsonPath("$[0].productName").value("Test Product"));
    }

    @Test
    void getBestDiscounts_shouldReturnActiveDiscounts() throws Exception {
        mockMvc.perform(get("/api/discounts/best")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].percentage").value(40))
                .andExpect(jsonPath("$[0].productId").value("p1"));
    }

    @Test
    void getNewDiscounts_shouldReturnRecentlyStartedDiscounts() throws Exception {
        mockMvc.perform(get("/api/discounts/new")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("p1"))
                .andExpect(jsonPath("$[0].percentage").value(40))
                .andExpect(jsonPath("$[0].startDate").value(LocalDate.now().minusDays(1).toString()));
    }
}
