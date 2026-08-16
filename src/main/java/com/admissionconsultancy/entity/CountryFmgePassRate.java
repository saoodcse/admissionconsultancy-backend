package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data @Entity @Table(name = "country_fmge_rates")
public final class CountryFmgePassRate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String university;
    private String passRate;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "country_id") @JsonBackReference @ToString.Exclude @EqualsAndHashCode.Exclude private Country country;
}
