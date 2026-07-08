package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.ContactHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ContactHistoryRepository extends JpaRepository<ContactHistory, Long> {

    List<ContactHistory> findBySeniorIdOrderByContactedAtDesc(Long seniorId);

    // ─── 전체 처리내역 목록 (상태/지역/기간 필터) ─────────────────────────────────

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE (:status IS NULL OR ch.resultStatus = :status)
          AND (:city IS NULL OR s.city = :city)
          AND (:gu IS NULL OR s.gu = :gu)
          AND (:dong IS NULL OR s.dong = :dong)
          AND (:from IS NULL OR ch.contactedAt >= :from)
          AND (:to IS NULL OR ch.contactedAt <= :to)
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> search(@Param("status") String status,
                                 @Param("city") String city,
                                 @Param("gu") String gu,
                                 @Param("dong") String dong,
                                 @Param("from") LocalDateTime from,
                                 @Param("to") LocalDateTime to);

    // ─── 오늘 처리 카운트 (대시보드) ─────────────────────────────────────────────

    @Query("""
        SELECT COUNT(DISTINCT ch.senior.id) FROM ContactHistory ch
        WHERE ch.resultStatus = :status AND ch.createdAt >= :startOfDay
        """)
    long countByResultStatusToday(@Param("status") String status,
                                   @Param("startOfDay") LocalDateTime startOfDay);

    @Query("""
        SELECT COUNT(DISTINCT ch.senior.id) FROM ContactHistory ch
        WHERE ch.resultStatus = :status AND ch.createdAt >= :startOfDay
          AND ch.senior.gu = :gu
        """)
    long countByResultStatusAndGuToday(@Param("status") String status,
                                        @Param("startOfDay") LocalDateTime startOfDay,
                                        @Param("gu") String gu);

    @Query("""
        SELECT COUNT(DISTINCT ch.senior.id) FROM ContactHistory ch
        WHERE ch.resultStatus = :status AND ch.createdAt >= :startOfDay
          AND ch.senior.gu = :gu AND ch.senior.dong = :dong
        """)
    long countByResultStatusAndRegionToday(@Param("status") String status,
                                            @Param("startOfDay") LocalDateTime startOfDay,
                                            @Param("gu") String gu,
                                            @Param("dong") String dong);
}
