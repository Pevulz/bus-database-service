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

    public Bus createBus(String company, String busNumber) {
        //1. Validate inputs
        if(company.isEmpty() || busNumber.isEmpty()) {
            throw new IllegalArgumentException("Company or BusNumber is invalid.");
        }

        //2. Check if bus exist in db
        boolean exist = busRepository.existsByCompanyAndBusNumber(company, busNumber);
        if(exist) {
            throw new IllegalArgumentException("Bus with company " + company + " and number " + busNumber + " already exists");
        }

        //2. Add to db if new
        Bus bus = new Bus();
        bus.setCompany(company);
        bus.setBusTag(busNumber);
        bus.setBusLogs(new ArrayList<>());
        bus.setImagePath("");
        return busRepository.save(bus);
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
        String newBusNumber = updatedBusDetails.getBusTag();

        if (newCompany != null && newBusNumber != null &&
                (!newCompany.equals(existingBus.getCompany()) || !newBusNumber.equals(existingBus.getBusTag()))) {
            boolean busNumberTaken = busRepository.existsByCompanyAndBusNumber(newCompany, newBusNumber);
            if (busNumberTaken) {
                throw new IllegalArgumentException(newCompany + " has " + newBusNumber + " already. Please choose a different number.");
            }
        }

        // 4. Update all fields if they are not null
        if (newCompany != null && !newCompany.isEmpty()) {
            existingBus.setCompany(newCompany);
        }

        if (newBusNumber != null && !newBusNumber.isEmpty()) {
            existingBus.setBusTag(newBusNumber);
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
