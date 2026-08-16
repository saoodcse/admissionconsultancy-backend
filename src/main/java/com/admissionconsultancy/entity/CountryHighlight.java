package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @Entity @Table(name = "country_highlights")
public final class CountryHighlight {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String feature;
    private String detail;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "country_id") @JsonBackReference @EqualsAndHashCode.Exclude private Country country;
}
