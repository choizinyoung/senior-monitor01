package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.ContactHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ContactHistoryRepository extends JpaRepository<ContactHistory, Long> {

    List<ContactHistory> findBySeniorIdOrderByContactedAtDesc(Long seniorId);

    // gu/dong이 null이면 해당 조건 없이 전체(MASTER), 값이 있으면 해당 지역으로 제한(MANAGER)
    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE (:gu IS NULL OR s.gu = :gu)
          AND (:dong IS NULL OR s.dong = :dong)
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllWithSeniorByRegion(@Param("gu") String gu, @Param("dong") String dong);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE ch.resultStatus = :status
          AND (:gu IS NULL OR s.gu = :gu)
          AND (:dong IS NULL OR s.dong = :dong)
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllWithSeniorByStatusAndRegion(@Param("status") String status,
                                                             @Param("gu") String gu,
                                                             @Param("dong") String dong);

    @Query("""
        SELECT COUNT(DISTINCT ch.senior.id) FROM ContactHistory ch
        WHERE ch.resultStatus = '확인완료'
          AND ch.createdAt >= :startOfDay
          AND (:gu IS NULL OR ch.senior.gu = :gu)
          AND (:dong IS NULL OR ch.senior.dong = :dong)
        """)
    long countConfirmedToday(@Param("startOfDay") LocalDateTime startOfDay,
                              @Param("gu") String gu, @Param("dong") String dong);

    @Query("""
        SELECT COUNT(DISTINCT ch.senior.id) FROM ContactHistory ch
        WHERE ch.resultStatus = '응급호출'
          AND ch.createdAt >= :startOfDay
          AND (:gu IS NULL OR ch.senior.gu = :gu)
          AND (:dong IS NULL OR ch.senior.dong = :dong)
        """)
    long countEmergencyToday(@Param("startOfDay") LocalDateTime startOfDay,
                              @Param("gu") String gu, @Param("dong") String dong);
}
