package com.admissionconsultancy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data @Entity @Table(name = "country_semesters")
public final class CountrySemester {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String semester;
    @ElementCollection @CollectionTable(name = "country_semester_subjects", joinColumns = @JoinColumn(name = "country_semester_id"))
    @Column(name = "subject")
    private List<String> subjects = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "country_id") @JsonBackReference @ToString.Exclude @EqualsAndHashCode.Exclude private Country country;
}
