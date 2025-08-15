package com.EagleKing.EagleKingTourApp.services;


import com.EagleKing.EagleKingTourApp.dto.CreateBusLogRequestDTO;
import com.EagleKing.EagleKingTourApp.entities.Bus;
import com.EagleKing.EagleKingTourApp.entities.BusLog;
import com.EagleKing.EagleKingTourApp.entities.Maintenance;
import com.EagleKing.EagleKingTourApp.entities.Product;
import com.EagleKing.EagleKingTourApp.repositories.BusLogRepository;
import com.EagleKing.EagleKingTourApp.repositories.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BusLogService {
    @Autowired
    private BusLogRepository busLogRepository;
    @Autowired
    private BusRepository busRepository;

    public List<BusLog> findAllBusLog() {
        return busLogRepository.findAll();
    }

    public BusLog createBusLog(CreateBusLogRequestDTO request) {
        //validate request
        validateRequest(request);

        //check if bus exist
        Optional<Bus> existingBus = busRepository.findById(request.getBusId());
        if (existingBus.isEmpty()) {
            throw new IllegalArgumentException("Bus not found with ID: " + request.getBusId());
        }

        //get logs of the bus
        //check if date exist
        List<BusLog> existingBusLog = existingBus.get().getBusLogs();
        Optional<BusLog> matchingLog = existingBusLog.stream().filter(log -> log.getDate().equals(request.getDate())).findFirst();

        //add to log if match
        if(matchingLog.isPresent()) {
            BusLog updatingLog = matchingLog.get();
            BusLog.MaintenanceSnapshot maintenance = new BusLog.MaintenanceSnapshot()
        }



    }

    public BusLog createBusLog(String busId, List<Maintenance> maintenances, Map<Product, Integer> productQuantityMap) {
        if(maintenances.isEmpty() && productQuantityMap.isEmpty()) {
            throw new IllegalArgumentException("Nothing to add to bus logs.");
        }

        // Fetch the bus
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with id: " + busId));

        LocalDate today = LocalDate.now();

        // Check if a log already exists for today
        BusLog existingLog = bus.getBusLogs().stream()
                .filter(log -> log.getDate().equals(today))
                .findFirst()
                .orElse(null);

        if (existingLog != null) {
            // Handle existing log - update maintenances
            Set<String> existingTypes = existingLog.getMaintenances().stream()
                    .map(BusLog.MaintenanceSnapshot::getType)
                    .collect(Collectors.toSet());

            // Convert new maintenances to snapshots and filter out duplicates
            List<BusLog.MaintenanceSnapshot> newMaintenanceSnapshots = maintenances.stream()
                    .filter(m -> !existingTypes.contains(m.getType()))
                    .map(BusLog.MaintenanceSnapshot::new)  // Convert to snapshot
                    .collect(Collectors.toList());

            existingLog.getMaintenances().addAll(newMaintenanceSnapshots);

            // Handle parts using the map
            for (Map.Entry<Product, Integer> entry : productQuantityMap.entrySet()) {
                Product product = entry.getKey();
                Integer quantity = entry.getValue();

                // Check if this product already exists in today's log
                Optional<BusLog.PartSnapshot> existingPart = existingLog.getParts().stream()
                        .filter(part -> part.getProductId().equals(product.getId()))
                        .findFirst();

                if (existingPart.isPresent()) {
                    // Update quantity if product exists
                    existingPart.get().setQuantity(existingPart.get().getQuantity() + quantity);
                } else {
                    // Add new part snapshot
                    BusLog.PartSnapshot snapshot = new BusLog.PartSnapshot(product, quantity);
                    existingLog.getParts().add(snapshot);
                }
            }

            // Recalculate total cost
            double maintenanceCost = existingLog.getMaintenances().stream()
                    .mapToDouble(BusLog.MaintenanceSnapshot::getCostAtTime)
                    .sum();

            double partsCost = existingLog.getParts().stream()
                    .mapToDouble(part -> part.getPriceAtTime() * part.getQuantity())
                    .sum();

            existingLog.setTotalCost(maintenanceCost + partsCost);

            // Save updated log
            BusLog savedLog = busLogRepository.save(existingLog);
            busRepository.save(bus);

            return savedLog;
        } else {
            // Create new log
            BusLog busLog = new BusLog();
            busLog.setBusId(busId);

            // Convert maintenances to snapshots
            List<BusLog.MaintenanceSnapshot> maintenanceSnapshots = maintenances.stream()
                    .map(BusLog.MaintenanceSnapshot::new)
                    .collect(Collectors.toList());
            busLog.setMaintenances(maintenanceSnapshots);

            busLog.setDate(today);
            busLog.setPaid(false);

            // Create part snapshots from map
            List<BusLog.PartSnapshot> partSnapshots = productQuantityMap.entrySet().stream()
                    .map(entry -> new BusLog.PartSnapshot(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            busLog.setParts(partSnapshots);

            // Calculate total cost
            double maintenanceCost = maintenanceSnapshots.stream()
                    .mapToDouble(BusLog.MaintenanceSnapshot::getCostAtTime)
                    .sum();

            double partsCost = partSnapshots.stream()
                    .mapToDouble(part -> part.getPriceAtTime() * part.getQuantity())
                    .sum();

            busLog.setTotalCost(maintenanceCost + partsCost);

            // Save bus log
            BusLog savedBusLog = busLogRepository.save(busLog);

            // Add the log to the bus and update the bus
            bus.getBusLogs().add(savedBusLog);
            busRepository.save(bus);

            return savedBusLog;
        }
    }



    @Transactional
    public BusLog markAsPaid(String busLogId) {
        BusLog busLog = busLogRepository.findById(busLogId)
                .orElseThrow(() -> new IllegalArgumentException("Bus log not found with id: " + busLogId));

        if (busLog.isPaid()) {
            throw new IllegalStateException("Bus log is already paid");
        }

        busLog.setPaid(true);
        return busLogRepository.save(busLog);
    }

    private double calculateTotalCost(BusLog busLog) {
        double maintenanceCost = busLog.getMaintenances().stream()
                .mapToDouble(BusLog.MaintenanceSnapshot::getCostAtTime)
                .sum();

        double partsCost = busLog.getParts().stream()
                .mapToDouble(p -> p.getPriceAtTime() * p.getQuantity())
                .sum();

        return maintenanceCost + partsCost;
    }

    private void validateRequest(CreateBusLogRequestDTO request) {
        if (request.getBusId() == null || request.getBusId().trim().isEmpty()) {
            throw new IllegalArgumentException("Bus ID is required");
        }

        if (request.getDate() == null) {
            throw new IllegalArgumentException("Date is required");
        }

        // Check if at least one maintenance or part is provided
        if ((request.getMaintenances() == null || request.getMaintenances().isEmpty()) &&
                (request.getParts() == null || request.getParts().isEmpty())) {
            throw new IllegalArgumentException("At least one maintenance or part must be provided");
        }
    }
}
