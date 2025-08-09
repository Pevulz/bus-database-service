package com.EagleKing.EagleKingTourApp.repositories;

import com.EagleKing.EagleKingTourApp.entities.BusLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BusLogRepository extends MongoRepository<BusLog, String> {

}
