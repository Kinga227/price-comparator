package edu.accesa.internship.pricecomparator.demo.dto;

import edu.accesa.internship.pricecomparator.demo.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ShoppingListDTO {

    private String store;
    private List<Product> products;
}
