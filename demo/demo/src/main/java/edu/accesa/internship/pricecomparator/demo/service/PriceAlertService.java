package edu.accesa.internship.pricecomparator.demo.service;

import edu.accesa.internship.pricecomparator.demo.model.PriceAlert;
import edu.accesa.internship.pricecomparator.demo.model.ProductPriceHistory;
import edu.accesa.internship.pricecomparator.demo.repository.PriceAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceAlertService {

    private final PriceAlertRepository priceAlertRepository;

    public void checkAlerts(List<ProductPriceHistory> updatedPrices) {
        for (ProductPriceHistory price : updatedPrices) {
            List<PriceAlert> alerts = priceAlertRepository.findByProductIdAndTriggeredFalse(price.getProductId());
            for (PriceAlert alert : alerts) {
                if (price.getDiscountedPrice() != null && price.getDiscountedPrice() <= alert.getTargetPrice()) {
                    System.out.println("Alert for " + alert.getUserEmail() + ": "
                        + price.getName() + " at " + price.getStore()
                        + " is now " + price.getDiscountedPrice()
                        + "( target: " + alert.getTargetPrice() + ")");
                    alert.setTriggered(true);
                    priceAlertRepository.save(alert);
                }
            }
        }
    }

    public PriceAlert createAlert(PriceAlert alert) {
        return priceAlertRepository.save(alert);
    }

    public List<PriceAlert> getAllAlerts() {
        return priceAlertRepository.findAll();
    }
}
