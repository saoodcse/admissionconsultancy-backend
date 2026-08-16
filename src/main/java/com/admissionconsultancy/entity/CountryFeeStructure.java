package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "country_fee_structure")
public final class CountryFeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String universityName;

    // Native/Local Currency Fields
    private String tuitionFeeNative;
    private String hostelFeeNative;
    @Column(length = 10)
    private String nativeCurrencyCode; // e.g., "RUB", "GEL", "USD", "EUR"

    // Normalized USD Values for Global Filtering
    private Double annualTuitionUsd;
    private Double annualHostelUsd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Country country;
}