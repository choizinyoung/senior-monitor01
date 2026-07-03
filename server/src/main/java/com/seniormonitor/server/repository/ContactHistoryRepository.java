package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.ContactHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ContactHistoryRepository extends JpaRepository<ContactHistory, Long> {

    List<ContactHistory> findBySeniorIdOrderByContactedAtDesc(Long seniorId);

    // ─── 전체 처리내역 목록 (지역 필터) ─────────────────────────────────────────

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllWithSenior();

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE s.gu = :gu
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllWithSeniorByGu(@Param("gu") String gu);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE s.gu = :gu AND s.dong = :dong
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllWithSeniorByGuAndDong(@Param("gu") String gu, @Param("dong") String dong);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE ch.resultStatus = :status
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllWithSeniorByStatus(@Param("status") String status);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE ch.resultStatus = :status AND s.gu = :gu
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllWithSeniorByStatusAndGu(@Param("status") String status, @Param("gu") String gu);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE ch.resultStatus = :status AND s.gu = :gu AND s.dong = :dong
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllWithSeniorByStatusAndGuAndDong(@Param("status") String status,
                                                                @Param("gu") String gu,
                                                                @Param("dong") String dong);

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
