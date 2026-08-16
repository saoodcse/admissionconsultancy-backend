package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data @Entity @Table(name = "country_admission_steps")
public final class CountryAdmissionStep {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private int stepOrder;
    private String title;
    @Column(columnDefinition = "TEXT") private String content;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "country_id") @JsonBackReference @ToString.Exclude @EqualsAndHashCode.Exclude private Country country;
}
