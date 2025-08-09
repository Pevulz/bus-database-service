package com.EagleKing.EagleKingTourApp.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "Bus")
public class Bus {
    @Id
    private String id;
    private String company;
    private String busTag;
    private String imagePath;
    private List<BusLog> busLogs = new ArrayList<>();

    // Add these calculated fields
    @Transient
    @JsonProperty
    public Double getTotalUnpaid() {
        return busLogs.stream()
                .filter(log -> !log.isPaid())
                .mapToDouble(BusLog::getTotalCost)
                .sum();
    }

    @Transient
    @JsonProperty
    public Double getTotalPaid() {
        return busLogs.stream()
                .filter(BusLog::isPaid)
                .mapToDouble(BusLog::getTotalCost)
                .sum();
    }

    @Transient
    @JsonProperty
    public Double getTotalCost() {
        return busLogs.stream()
                .mapToDouble(BusLog::getTotalCost)
                .sum();
    }
}