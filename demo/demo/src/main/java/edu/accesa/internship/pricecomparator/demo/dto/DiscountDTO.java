package edu.accesa.internship.pricecomparator.demo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DiscountDTO {

    private String productId;
    private String productName;
    private String brand;
    private String category;
    private double price;
    private int percentage;
    private LocalDate startDate;
    private LocalDate endDate;
}
