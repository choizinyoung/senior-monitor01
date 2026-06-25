package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.ContactHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ContactHistoryRepository extends JpaRepository<ContactHistory, Long> {

    List<ContactHistory> findBySeniorIdOrderByContactedAtDesc(Long seniorId);

    @Query("SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior ORDER BY ch.contactedAt DESC")
    List<ContactHistory> findAllWithSenior();

    @Query("SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior WHERE ch.resultStatus = :status ORDER BY ch.contactedAt DESC")
    List<ContactHistory> findAllWithSeniorByStatus(@Param("status") String status);

    @Query("""
        SELECT COUNT(DISTINCT ch.senior.id) FROM ContactHistory ch
        WHERE ch.resultStatus = '확인완료'
          AND ch.createdAt >= :startOfDay
        """)
    long countConfirmedToday(@Param("startOfDay") LocalDateTime startOfDay);

    @Query("""
        SELECT COUNT(DISTINCT ch.senior.id) FROM ContactHistory ch
        WHERE ch.resultStatus = '응급호출'
          AND ch.createdAt >= :startOfDay
        """)
    long countEmergencyToday(@Param("startOfDay") LocalDateTime startOfDay);
}
