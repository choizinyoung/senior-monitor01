package com.seniormonitor.server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_history")
@Getter
@Setter
@NoArgsConstructor
public class ContactHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    @Column(name = "manager_name", nullable = false, length = 20)
    private String managerName;

    @Column(name = "result_status", nullable = false, length = 10)
    private String resultStatus;

    @Column(length = 500)
    private String memo;

    @Column(name = "contacted_at", nullable = false)
    private LocalDateTime contactedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
