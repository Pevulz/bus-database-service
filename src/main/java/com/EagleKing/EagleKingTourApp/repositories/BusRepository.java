package com.EagleKing.EagleKingTourApp.repositories;

import com.EagleKing.EagleKingTourApp.entities.Bus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BusRepository extends MongoRepository<Bus, String> {
    List<Bus> findByCompany(String company);
    Optional<Bus> findByCompanyAndBusNumber(String company, String busNumber);

    boolean existsByCompanyAndBusNumber(String company, String busNumber);


}
