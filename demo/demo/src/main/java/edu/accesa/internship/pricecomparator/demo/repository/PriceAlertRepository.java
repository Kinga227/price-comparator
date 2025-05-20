package edu.accesa.internship.pricecomparator.demo.repository;

import edu.accesa.internship.pricecomparator.demo.model.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {

    List<PriceAlert> findByTriggeredFalse();
    List<PriceAlert> findByProductIdAndTriggeredFalse(String productId);
}
