package com.admissionconsultancy.entity;

import com.admissionconsultancy.enums.LeadStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leads", indexes = {
        @Index(name = "idx_lead_email_phone", columnList = "email, phone"),
        @Index(name = "idx_lead_status", columnList = "status")
})
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Column(nullable = false)
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Column(nullable = false)
    private String phone;

    private String city;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String source; // e.g., "Website", "Facebook Ads", "Referral"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus status = LeadStatus.NEW;

    @Column(columnDefinition = "TEXT")
    private String adminNotes; // For counselors to leave internal remarks

    // --- Admissions & Intent Tracking ---
    private String targetIntake;
    private Double preferredBudgetUsd;

    // --- Marketing Attribution (UTM Parameters) ---
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;

    // --- Assignment & Follow-up ---
    private String assignedCounselorEmail;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // RELATIONAL LINKS (Interest Tracking)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Country interestedCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private University interestedUniversity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private College interestedCollege; // Added College Relation

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course interestedCourse;
}