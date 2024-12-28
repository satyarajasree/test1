package com.rajasreeit.backend.customer.entities;

import com.rajasreeit.backend.customer.enums.LandsAvailble;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lands {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "land_id")
    private String property_id;

    @ManyToOne
    @JoinColumn(name = "property_id", referencedColumnName = "id")
    private Properties properties;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    private Customer customer;

    @Column(name = "passbook_number", unique = true, nullable = true, updatable = false)
    private Long passbookNumber;  // No @GeneratedValue here

    @Column(name = "land_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LandsAvailble landsAvailble;

    @Column(name = "area")
    private double area;

    @Column(name = "location")
    private String location;

    @Column(name = "price")
    private double price;

    @Column(name = "booking_date")
    private String bookingDate;

    @Column(name = "plot_facing")
    private String plotFacing;

    @Column(name = "minimum_amount")
    private double minAmount;

    @Column(name = "thirty_percent")
    private double thirtyPercentAmout;

    @Column(name = "full_amount")
    private double fullAmount;

    @Column(name = "paid_amount")
    private double paidAmount;

    @Column(name = "size", nullable = false)
    private double size;

    @Column(name = "available", nullable = false)
    private boolean isAvailable;

    @Column(name = "agreement_document")
    private String agreement_document;

    @Column(name = "term_and_conditions_document")
    private String termAndConditionsDocument;
}
