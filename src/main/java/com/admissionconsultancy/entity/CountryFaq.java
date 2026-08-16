package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @Entity @Table(name = "country_faqs")
public final class CountryFaq {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(columnDefinition = "TEXT") private String question;
    @Column(columnDefinition = "TEXT") private String answer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "country_id") @JsonBackReference @EqualsAndHashCode.Exclude private Country country;
}
