package com.admissionconsultancy.entity;

import com.admissionconsultancy.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String applicantEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @Column(columnDefinition = "TEXT")
    private String counselorRemarks;

    // Relational Targets
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private University university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course course;

    // Uploaded Documents URL list (e.g., Passport, 12th Marksheet, IELTS Scorecard)
    @ElementCollection
    @CollectionTable(name = "application_documents", joinColumns = @JoinColumn(name = "application_id"))
    @Column(name = "document_url")
    private List<String> documentUrls = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}