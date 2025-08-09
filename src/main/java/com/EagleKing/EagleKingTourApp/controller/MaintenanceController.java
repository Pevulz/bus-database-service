package com.EagleKing.EagleKingTourApp.controller;


import com.EagleKing.EagleKingTourApp.entities.Maintenance;
import com.EagleKing.EagleKingTourApp.services.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/maintenances")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @GetMapping()
    public ResponseEntity<?> getAllMaintenance() {
        try {
            List<Maintenance> maintenances = maintenanceService.getAllMaintenance();
            return ResponseEntity.ok(maintenances);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping()
    public ResponseEntity<?> createMaintenance(@RequestBody Maintenance maintenance) {
        try {
            Maintenance createdMaintenance = maintenanceService.createMaintenance(maintenance);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMaintenance);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMaintenance(@PathVariable("id") String maintenanceId,
                                               @RequestBody Maintenance updatedMaintenance) {
        try {
            Maintenance maintenance = maintenanceService.updateMaintenance(maintenanceId, updatedMaintenance);
            return ResponseEntity.ok(maintenance);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
