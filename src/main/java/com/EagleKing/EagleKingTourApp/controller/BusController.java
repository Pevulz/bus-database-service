package com.EagleKing.EagleKingTourApp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.EagleKing.EagleKingTourApp.services.BusService;
import com.EagleKing.EagleKingTourApp.entities.Bus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/buses")
public class BusController {

    @Autowired
    private BusService busService;

    @PostMapping()
    public ResponseEntity<?> createBus(@RequestBody Bus bus) {
        try {
            Bus createdBus = busService.createBus(bus.getCompany(), bus.getBusTag());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBus);
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getBusById(@PathVariable("id") String id) {
        try {
            Bus bus = busService.getBusById(id);
            if (bus != null) {
                return ResponseEntity.ok(bus);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch bus details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllBuses() {
        try {
            List<Bus> buses = busService.getAllBuses();
            return ResponseEntity.ok(buses);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBus(@PathVariable("id") String busId,
                                       @RequestBody Bus updatedBusDetails) {
        try {
            Bus updated = busService.updateBus(busId, updatedBusDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update bus");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}