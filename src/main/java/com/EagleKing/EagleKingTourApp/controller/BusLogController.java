package com.EagleKing.EagleKingTourApp.controller;

import com.EagleKing.EagleKingTourApp.entities.BusLog;
import com.EagleKing.EagleKingTourApp.entities.Maintenance;
import com.EagleKing.EagleKingTourApp.entities.Product;
import com.EagleKing.EagleKingTourApp.services.BusLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/busLogs")

public class BusLogController {

    @Autowired
    private BusLogService busLogService;

    @PostMapping("/{id}")
    public ResponseEntity<?> createBusLog(
            @PathVariable("id") String busId,
            @RequestBody List<Maintenance> maintenances, List<Product> products) {

        try {
            BusLog createdLog = busLogService.createBusLog(busId, maintenances, products);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLog);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/paid")
    public ResponseEntity<?> markAsPaid(@PathVariable("id") String busLogId) {
        try {
            BusLog updatedBusLog = busLogService.markAsPaid(busLogId);
            return ResponseEntity.ok(updatedBusLog);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to mark as paid"));
        }
    }
}
