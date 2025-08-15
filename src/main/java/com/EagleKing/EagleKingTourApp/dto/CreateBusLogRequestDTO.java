package com.EagleKing.EagleKingTourApp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBusLogRequestDTO {
    private String busId;
    private LocalDate date;

    @Builder.Default
    private List<MaintenanceRequest> maintenances = new ArrayList<>();

    @Builder.Default
    private List<PartRequest> parts = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MaintenanceRequest {
        private String maintenanceId;
        private String performedBy;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PartRequest {
        private String productId;
        private int quantity;
    }
}