package com.EagleKing.EagleKingTourApp.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "BusLogs")
public class BusLog {
    @Id
    private String id;
    private String busId;
    private LocalDate date;
    private List<MaintenanceSnapshot> maintenances = new ArrayList<>();
    private List<PartSnapshot> parts = new ArrayList<>();
    private double totalCost;
    private boolean paid;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MaintenanceSnapshot {
        private String maintenanceId;
        private String type;
        private String description;
        private double costAtTime;
        private String performedBy;
        private String notes;

        public MaintenanceSnapshot(Maintenance maintenance) {
            this.maintenanceId = maintenance.getId();
            this.type = maintenance.getType();
            this.description = maintenance.getDescription();
            this.costAtTime = maintenance.getCost();
        }

        public MaintenanceSnapshot(Maintenance maintenance, String performedBy, String notes) {
            this(maintenance);
            this.performedBy = performedBy;
            this.notes = notes;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PartSnapshot {
        private String productId;
        private String name;
        private double priceAtTime;
        private int quantity;
        private String unit;


        public PartSnapshot(Product product, int quantity) {
            this.productId = product.getId();
            this.name = product.getName();
            this.priceAtTime = product.getResellPrice();
            this.quantity = quantity;
            this.unit = product.getUnit();
        }
    }
}