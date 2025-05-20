package edu.accesa.internship.pricecomparator.demo.service;

import edu.accesa.internship.pricecomparator.demo.model.PriceAlert;
import edu.accesa.internship.pricecomparator.demo.repository.PriceAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PriceAlertServiceTest {

    @Mock
    private PriceAlertRepository priceAlertRepository;

    @InjectMocks
    private PriceAlertService priceAlertService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllAlerts_shouldReturnAllAlerts() {
        PriceAlert alert = new PriceAlert();
        alert.setUserEmail("user@example.com");
        alert.setTargetPrice(7.0);

        when(priceAlertRepository.findAll()).thenReturn(List.of(alert));

        List<PriceAlert> result = priceAlertService.getAllAlerts();

        assertEquals(1, result.size());
        assertEquals("user@example.com", result.get(0).getUserEmail());
        verify(priceAlertRepository).findAll();
    }

    @Test
    void createAlert_shouldSaveAlert() {
        PriceAlert alert = new PriceAlert();
        alert.setProductId("p1");
        alert.setUserEmail("test@example.com");
        alert.setTargetPrice(9.0);

        when(priceAlertRepository.save(alert)).thenReturn(alert);

        PriceAlert result = priceAlertService.createAlert(alert);

        assertEquals("test@example.com", result.getUserEmail());
        verify(priceAlertRepository).save(alert);
    }
}
