package com.admissionconsultancy.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "wishlists", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_email", "university_id", "course_id"})
})
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentEmail; // Identifies the student (can also link to User ID if auth exists)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private University university;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course course;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}