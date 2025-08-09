package com.EagleKing.EagleKingTourApp.repositories;

import com.EagleKing.EagleKingTourApp.entities.Maintenance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MaintenanceRepository extends MongoRepository<Maintenance, String> {
    boolean existsByTypeAndDescription(String type, String description);
    Optional<Maintenance> findByTypeAndDescription(String type, String description);
}
