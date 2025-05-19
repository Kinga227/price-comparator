package edu.accesa.internship.pricecomparator.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class ProductPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String name;
    private String store;
    private LocalDate date;
    private Double originalPrice;
    private Integer discountPercentage;
    private Double discountedPrice;
    private String currency;
    private String category;
    private String brand;
}
