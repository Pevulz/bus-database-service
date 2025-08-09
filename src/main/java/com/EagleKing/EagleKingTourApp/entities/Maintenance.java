package com.EagleKing.EagleKingTourApp.entities;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "Maintenance")
public class Maintenance {
    @Id
    private String id;
    private String type;
    private String description;
    private double cost;
}
