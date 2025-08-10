package com.EagleKing.EagleKingTourApp.services;

import com.EagleKing.EagleKingTourApp.entities.Bus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.EagleKing.EagleKingTourApp.repositories.BusRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus getBusById(String id) {
        return busRepository.findById(id).orElse(null);
    }

    public Bus createBus(Bus bus) {
        //1. Validate inputs
        if(bus.getCompany().isEmpty() || bus.getBusTag().isEmpty()) {
            throw new IllegalArgumentException("Company or BusNumber is invalid.");
        }

        //2. Check if bus exist in db
        boolean exist = busRepository.existsByCompanyAndBusTag( bus.getCompany(), bus.getBusTag());
        if(exist) {
            throw new IllegalArgumentException("Bus with company " +  bus.getCompany() + " and number " + bus.getBusTag() + " already exists");
        }

        //2. Add to db if new
        Bus newBus = new Bus();
        bus.setCompany( bus.getCompany());
        bus.setBusTag(bus.getBusTag());
        bus.setBusLogs(new ArrayList<>());
        bus.setImagePath(bus.getImagePath());
        return busRepository.save(newBus);
    }

    public Bus updateBus(String busId, Bus updatedBusDetails) {
        // 1. Validate essential inputs
        if (updatedBusDetails == null) {
            throw new IllegalArgumentException("Updated bus details cannot be null.");
        }

        // 2. Check if bus exists in db
        Optional<Bus> existingBusOptional = busRepository.findById(busId);
        if (existingBusOptional.isEmpty()) {
            throw new IllegalArgumentException("Bus with ID " + busId + " does not exist.");
        }
        Bus existingBus = existingBusOptional.get();

        // 3. Check for duplicate company/busNumber combination only if those fields are changing
        String newCompany = updatedBusDetails.getCompany();
        String newBusTag = updatedBusDetails.getBusTag();

        if (newCompany != null && newBusTag != null &&
                (!newCompany.equals(existingBus.getCompany()) || !newBusTag.equals(existingBus.getBusTag()))) {
            boolean busNumberTaken = busRepository.existsByCompanyAndBusTag(newCompany, newBusTag);
            if (busNumberTaken) {
                throw new IllegalArgumentException(newCompany + " has " + newBusTag + " already. Please choose a different number.");
            }
        }

        // 4. Update all fields if they are not null
        if (newCompany != null && !newCompany.isEmpty()) {
            existingBus.setCompany(newCompany);
        }

        if (newBusTag != null && !newBusTag.isEmpty()) {
            existingBus.setBusTag(newBusTag);
        }

        // Handle image path updates
        if (updatedBusDetails.getImagePath() != null) {
            existingBus.setImagePath(updatedBusDetails.getImagePath());
        }

        // Handle service log updates
        if (updatedBusDetails.getBusLogs() != null) {
            existingBus.setBusLogs(updatedBusDetails.getBusLogs());
        }

        // 5. Save and return the updated bus
        return busRepository.save(existingBus);
    }
}
