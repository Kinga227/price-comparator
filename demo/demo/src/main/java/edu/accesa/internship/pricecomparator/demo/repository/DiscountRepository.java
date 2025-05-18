package edu.accesa.internship.pricecomparator.demo.repository;

import edu.accesa.internship.pricecomparator.demo.model.Discount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query("""
        SELECT d FROM Discount d
        WHERE :now BETWEEN d.startDate AND d.endDate
        ORDER BY d.percentage DESC
    """)
    List<Discount> findBestDiscounts(@Param("now") LocalDate now, Pageable pageable);
}
