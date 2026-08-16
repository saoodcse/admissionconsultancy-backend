package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @Entity @Table(name = "country_eligibility")
public final class CountryEligibility {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String detail;
    private String requirement;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "country_id") @JsonBackReference @EqualsAndHashCode.Exclude private Country country;
}
