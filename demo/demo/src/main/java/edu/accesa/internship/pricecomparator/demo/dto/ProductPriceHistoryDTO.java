package edu.accesa.internship.pricecomparator.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProductPriceHistoryDTO {

    private String name;
    private LocalDate date;
    private Double originalPrice;
    private Integer discountPercentage;
    private Double discountedPrice;
    private String currency;
    private String store;
    private String category;
    private String brand;
}
