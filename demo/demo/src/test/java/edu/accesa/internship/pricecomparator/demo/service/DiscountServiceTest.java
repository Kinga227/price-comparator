package edu.accesa.internship.pricecomparator.demo.service;

import edu.accesa.internship.pricecomparator.demo.dto.DiscountDTO;
import edu.accesa.internship.pricecomparator.demo.mapper.DiscountMapper;
import edu.accesa.internship.pricecomparator.demo.model.Discount;
import edu.accesa.internship.pricecomparator.demo.repository.DiscountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private DiscountMapper discountMapper;

    @InjectMocks
    private DiscountService discountService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllDiscounts_shouldReturnMappedDTOs() {
        Discount discount = new Discount();
        discount.setPercentage(20);

        DiscountDTO dto = new DiscountDTO();
        dto.setPercentage(20);

        when(discountRepository.findAll()).thenReturn(List.of(discount));
        when(discountMapper.modelToDto(discount)).thenReturn(dto);

        List<DiscountDTO> result = discountService.getAllDiscounts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(20, result.get(0).getPercentage());

        verify(discountRepository).findAll();
        verify(discountMapper).modelToDto(discount);
    }

    @Test
    void getBestDiscounts_shouldReturnMappedDTOsForCurrentDate() {
        Discount discount = new Discount();
        discount.setPercentage(50);

        DiscountDTO dto = new DiscountDTO();
        dto.setPercentage(50);

        when(discountRepository.findBestDiscounts(any(LocalDate.class))).thenReturn(List.of(discount));
        when(discountMapper.modelToDto(discount)).thenReturn(dto);

        List<DiscountDTO> result = discountService.getBestDiscounts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(50, result.get(0).getPercentage());

        verify(discountRepository).findBestDiscounts(any(LocalDate.class));
        verify(discountMapper).modelToDto(discount);
    }

    @Test
    void getNewDiscounts_shouldReturnDiscountsStartedTodayOfYesterday() {
        Discount discount = new Discount();
        discount.setPercentage(30);
        discount.setStartDate(LocalDate.now());

        DiscountDTO dto = new DiscountDTO();
        dto.setPercentage(30);
        dto.setStartDate(LocalDate.now());

        when(discountRepository.findNewDiscounts(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(discount));
        when(discountMapper.modelToDto(discount)).thenReturn(dto);

        List<DiscountDTO> result = discountService.getNewDiscounts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(30, result.get(0).getPercentage());

        verify(discountRepository).findNewDiscounts(any(LocalDate.class), any(LocalDate.class));
        verify(discountMapper).modelToDto(discount);
    }
}
