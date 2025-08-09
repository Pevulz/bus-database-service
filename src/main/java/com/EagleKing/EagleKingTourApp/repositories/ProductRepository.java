package com.EagleKing.EagleKingTourApp.repositories;

import com.EagleKing.EagleKingTourApp.entities.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByNameIgnoreCase(String name);

}
