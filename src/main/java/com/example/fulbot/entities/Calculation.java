package com.example.fulbot.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity(name= "calculations")
@AllArgsConstructor
@NoArgsConstructor
public class Calculation {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private int boxQuantity;
    private int itemQuantity;
    private boolean delivery;
    private boolean defective;
    private boolean simple;
    private boolean barcode;
    private boolean packaging;
    private boolean shipment;
    private boolean prepareBoxes;
    private int packagingPrice;

    private int totalPrice;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @Override
    public String toString() {
        return String.format("№ %s%nКоличество коробок: %d%nКоличество штук: %d%nИтоговая цена: %d%n",
                getId(), getBoxQuantity(), getItemQuantity(), getTotalPrice());
    }

    public Calculation(int boxQuantity, int itemQuantity, boolean delivery, boolean defective, boolean simple,
                       boolean barcode, boolean packaging, boolean shipment, boolean prepareBoxes, int packagingPrice,
                       int totalPrice, User user) {
        this.boxQuantity = boxQuantity;
        this.itemQuantity = itemQuantity;
        this.delivery = delivery;
        this.defective = defective;
        this.simple = simple;
        this.barcode = barcode;
        this.packaging = packaging;
        this.shipment = shipment;
        this.prepareBoxes = prepareBoxes;
        this.packagingPrice = packagingPrice;
        this.totalPrice = totalPrice;
        this.user = user;
    }

}