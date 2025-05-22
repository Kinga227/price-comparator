package edu.accesa.internship.pricecomparator.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.accesa.internship.pricecomparator.demo.dto.ShoppingListDTO;
import edu.accesa.internship.pricecomparator.demo.model.Product;
import edu.accesa.internship.pricecomparator.demo.repository.DiscountRepository;
import edu.accesa.internship.pricecomparator.demo.repository.ProductRepository;
import edu.accesa.internship.pricecomparator.demo.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BasketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        discountRepository.deleteAll();
        productRepository.deleteAll();

        productRepository.save(new Product("p1", "Lapte", "lactate", "Brand", 1, "l", "RON", 8.0, "lidl"));
        productRepository.save(new Product("p2", "Paine", "panificatie", "Brand", 1, "buc", "RON", 4.0, "lidl"));
        productRepository.save(new Product("p3", "Unt", "lactate", "Brand", 0.5, "kg", "RON", 10.0, "profi"));
    }

    @Test
    void optimize_shouldReturnGroupedShoppingLists() throws Exception {
        List<String> basket = List.of("p1", "p2", "p3");

        mockMvc.perform(post("/api/basket/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basket)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].store").value("lidl"))
                .andExpect(jsonPath("$[0].products", hasSize(2)))
                .andExpect(jsonPath("$[1].store").value("profi"))
                .andExpect(jsonPath("$[1].products", hasSize(1)));
    }
}
