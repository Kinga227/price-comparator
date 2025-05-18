package edu.accesa.internship.pricecomparator.demo.repository;

import edu.accesa.internship.pricecomparator.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByIdAndStore(String id, String store);
}
