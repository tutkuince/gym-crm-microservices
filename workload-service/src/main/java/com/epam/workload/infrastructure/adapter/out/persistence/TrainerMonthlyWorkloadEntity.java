package com.epam.workload.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "trainer_monthly_workload", uniqueConstraints = @UniqueConstraint(
        name = "uk_user_year_month",
        columnNames = {"username", "year", "month"}
))
public class TrainerMonthlyWorkloadEntity {

    public TrainerMonthlyWorkloadEntity(String username, int year, int month, String firstName, String lastName, boolean active, int totalMinutes) {
        this.username = username;
        this.year = year;
        this.month = month;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.totalMinutes = totalMinutes;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private int year;
    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private boolean active;
    @Column(nullable = false)
    private int totalMinutes;

}
