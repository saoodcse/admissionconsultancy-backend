package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data @Entity @Table(name = "country_destinations")
public final class CountryDestination {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name;
    private String slug;
    private String flag;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "country_id") @JsonBackReference @ToString.Exclude @EqualsAndHashCode.Exclude private Country country;
}
