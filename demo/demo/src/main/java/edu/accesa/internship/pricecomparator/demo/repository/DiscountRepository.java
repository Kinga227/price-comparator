package edu.accesa.internship.pricecomparator.demo.repository;

import edu.accesa.internship.pricecomparator.demo.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, String> {

    @Query("""
        SELECT d FROM Discount d
        WHERE :now BETWEEN d.startDate AND d.endDate
        ORDER BY d.percentage DESC
    """)
    List<Discount> findBestDiscounts(@Param("now") LocalDate now);

    @Query("""
        SELECT d FROM Discount d
        WHERE d.startDate = :yesterday OR d.startDate = :now
        ORDER BY d.startDate DESC
    """)
    List<Discount> findNewDiscounts(@Param("yesterday") LocalDate yesterday, @Param("now") LocalDate now);
}
