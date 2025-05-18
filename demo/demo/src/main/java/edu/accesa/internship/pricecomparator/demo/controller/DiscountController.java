package edu.accesa.internship.pricecomparator.demo.controller;

import edu.accesa.internship.pricecomparator.demo.dto.DiscountDTO;
import edu.accesa.internship.pricecomparator.demo.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping
    public List<DiscountDTO> getAllDiscounts() {
        return discountService.getAllDiscounts();
    }

    @GetMapping("/best")
    public List<DiscountDTO> getBestDiscounts() {
        return discountService.getBestDiscounts();
    }
}
