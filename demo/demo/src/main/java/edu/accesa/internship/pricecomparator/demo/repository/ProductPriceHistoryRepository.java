package edu.accesa.internship.pricecomparator.demo.repository;

import edu.accesa.internship.pricecomparator.demo.model.ProductPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPriceHistoryRepository extends JpaRepository<ProductPriceHistory, Long> {

    List<ProductPriceHistory> findByProductIdOrderByDate(String productId);

    @Query("""
            SELECT pph FROM ProductPriceHistory pph
            JOIN Product p ON pph.productId = p.id
            WHERE (:store IS NULL OR pph.store = :store)
                    AND (:category IS NULL OR pph.category = :category)
                    AND (:brand IS NULL OR pph.brand = :brand)
            ORDER BY pph.date
            """)
    List<ProductPriceHistory> findByFilters(
            @Param("store") String store,
            @Param("category") String category,
            @Param("brand") String brand
    );
}
