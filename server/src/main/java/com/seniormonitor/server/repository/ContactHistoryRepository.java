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
    // PostgreSQL + Hibernate 7에서 (:param IS NULL OR ...) 패턴은 null 파라미터 타입 추론에 실패하므로
    // (커밋 003e4ec 참고) 지역/상태 조합별로 메서드를 분리한다. from/to는 서비스 단에서 항상 채워서 넘기므로 null이 없다.

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE ch.contactedAt BETWEEN :from AND :to
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE s.city = :city AND ch.contactedAt BETWEEN :from AND :to
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllByCityAndDateRange(@Param("city") String city,
                                                    @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE s.gu = :gu AND ch.contactedAt BETWEEN :from AND :to
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllByGuAndDateRange(@Param("gu") String gu,
                                                  @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE s.gu = :gu AND s.dong = :dong AND ch.contactedAt BETWEEN :from AND :to
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllByGuAndDongAndDateRange(@Param("gu") String gu, @Param("dong") String dong,
                                                         @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE ch.resultStatus = :status AND ch.contactedAt BETWEEN :from AND :to
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllByStatusAndDateRange(@Param("status") String status,
                                                      @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE ch.resultStatus = :status AND s.city = :city AND ch.contactedAt BETWEEN :from AND :to
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllByStatusAndCityAndDateRange(@Param("status") String status, @Param("city") String city,
                                                             @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE ch.resultStatus = :status AND s.gu = :gu AND ch.contactedAt BETWEEN :from AND :to
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllByStatusAndGuAndDateRange(@Param("status") String status, @Param("gu") String gu,
                                                           @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        SELECT ch FROM ContactHistory ch JOIN FETCH ch.senior s
        WHERE ch.resultStatus = :status AND s.gu = :gu AND s.dong = :dong AND ch.contactedAt BETWEEN :from AND :to
        ORDER BY ch.contactedAt DESC
        """)
    List<ContactHistory> findAllByStatusAndGuAndDongAndDateRange(@Param("status") String status,
                                                                  @Param("gu") String gu, @Param("dong") String dong,
                                                                  @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

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
