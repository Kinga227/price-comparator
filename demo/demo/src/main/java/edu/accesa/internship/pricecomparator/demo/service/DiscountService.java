package edu.accesa.internship.pricecomparator.demo.service;

import edu.accesa.internship.pricecomparator.demo.dto.DiscountDTO;
import edu.accesa.internship.pricecomparator.demo.mapper.DiscountMapper;
import edu.accesa.internship.pricecomparator.demo.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    public List<DiscountDTO> getAllDiscounts() {
        return discountRepository.findAll()
                .stream()
                .map(discountMapper::modelToDto)
                .toList();
    }

    public List<DiscountDTO> getBestDiscounts() {
        return discountRepository.findBestDiscounts(LocalDate.now())
                .stream()
                .map(discountMapper::modelToDto)
                .toList();
    }

    public List<DiscountDTO> getNewDiscounts() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        return discountRepository.findNewDiscounts(yesterday, today)
                .stream()
                .map(discountMapper::modelToDto)
                .toList();
    }
}
