package com.EagleKing.EagleKingTourApp.services;

import com.EagleKing.EagleKingTourApp.entities.Maintenance;
import com.EagleKing.EagleKingTourApp.repositories.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceService {
    @Autowired
    private MaintenanceRepository maintenanceRepository;

    public List<Maintenance> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }

    public Optional<Maintenance> getMaintenanceById(String id) {
        return maintenanceRepository.findById(id);
    }

    public Maintenance createMaintenance(Maintenance maintenance) {
        // 1. Validate inputs
        if (maintenance.getType() == null || maintenance.getType().isEmpty() ||
                maintenance.getDescription() == null || maintenance.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Type or description is invalid.");
        }

        if (maintenance.getCost() < 0) {
            throw new IllegalArgumentException("Cost cannot be negative.");
        }

        // 2. Check if maintenance with same type and description exists
        boolean exists = maintenanceRepository.existsByTypeAndDescription(
                maintenance.getType(),
                maintenance.getDescription()
        );
        if (exists) {
            throw new IllegalArgumentException("Maintenance with type " + maintenance.getType() +
                    " and description " + maintenance.getDescription() + " already exists");
        }

        // 3. Add to db
        return maintenanceRepository.save(maintenance);
    }

    public Maintenance updateMaintenance(String id, Maintenance updatedMaintenance) {
        if(updatedMaintenance == null) {
            throw new IllegalArgumentException("Updated maintenance inputs are null.");
        }

        Optional<Maintenance> exist = maintenanceRepository.findById(id);
        if(exist.isEmpty()) {
            throw new IllegalArgumentException("Service with ID: " + id + " does not exist.");
        }

        // Check if another maintenance exists with the same type and description
        // (excluding the current maintenance being updated)
        Optional<Maintenance> duplicate = maintenanceRepository
                .findByTypeAndDescription(updatedMaintenance.getType(), updatedMaintenance.getDescription());

        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new IllegalArgumentException("Another maintenance already exists with type '" +
                    updatedMaintenance.getType() + "' and description '" +
                    updatedMaintenance.getDescription() + "'");
        }

        // Update the maintenance
        Maintenance maintenance = exist.get();

        if (updatedMaintenance.getType() != null) {
            maintenance.setType(updatedMaintenance.getType());
        }
        if (updatedMaintenance.getDescription() != null) {
            maintenance.setDescription(updatedMaintenance.getDescription());
        }
        if (updatedMaintenance.getCost() >= 0) {
            maintenance.setCost(updatedMaintenance.getCost());
        }

        return maintenanceRepository.save(maintenance);
    }
}
