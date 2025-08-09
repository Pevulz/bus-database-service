package com.EagleKing.EagleKingTourApp.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "Product")
public class Product {
    @Id
    private String id;
    private String name;
    private String imgPath;
    private double retailPrice;
    private double resellPrice;
    private int quantity;
    private String unit;
    private String type;


}
