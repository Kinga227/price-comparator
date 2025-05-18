package edu.accesa.internship.pricecomparator.demo.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "products")
public class Product {

    @Id
    private String id;

    private String name;
    private String category;
    private String brand;
    private double packageQuantity;
    private String packageUnit;
    private String currency;
    private double price;

    private String store;
}
