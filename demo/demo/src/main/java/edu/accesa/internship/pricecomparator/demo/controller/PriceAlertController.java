package edu.accesa.internship.pricecomparator.demo.controller;

import edu.accesa.internship.pricecomparator.demo.model.PriceAlert;
import edu.accesa.internship.pricecomparator.demo.service.PriceAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class PriceAlertController {

    private final PriceAlertService priceAlertService;

    @GetMapping
    public List<PriceAlert> getAllAlerts() {
        return priceAlertService.getAllAlerts();
    }

    @PostMapping
    public PriceAlert createAlert(@RequestBody PriceAlert alert) {
        return priceAlertService.createAlert(alert);
    }
}
