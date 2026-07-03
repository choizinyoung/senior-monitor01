package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.Senior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeniorRepository extends JpaRepository<Senior, Long> {

    Optional<Senior> findByDeviceId(String deviceId);

    boolean existsByPhone(String phone);

    // ─── 전체 대상자 목록 (지역 필터) ───────────────────────────────────────────

    @Query("SELECT s FROM Senior s WHERE s.isDeleted = 'N' ORDER BY s.registeredAt DESC")
    List<Senior> findAllActive();

    @Query("SELECT s FROM Senior s WHERE s.isDeleted = 'N' AND s.gu = :gu ORDER BY s.registeredAt DESC")
    List<Senior> findActiveByGu(@Param("gu") String gu);

    @Query("SELECT s FROM Senior s WHERE s.isDeleted = 'N' AND s.gu = :gu AND s.dong = :dong ORDER BY s.registeredAt DESC")
    List<Senior> findActiveByGuAndDong(@Param("gu") String gu, @Param("dong") String dong);

    // ─── 상태별 대상자 목록 (지역 필터) ─────────────────────────────────────────

    @Query("SELECT s FROM Senior s WHERE s.status = :status AND s.isDeleted = 'N'")
    List<Senior> findByStatus(@Param("status") String status);

    @Query("SELECT s FROM Senior s WHERE s.status = :status AND s.isDeleted = 'N' AND s.gu = :gu")
    List<Senior> findByStatusAndGu(@Param("status") String status, @Param("gu") String gu);

    @Query("SELECT s FROM Senior s WHERE s.status = :status AND s.isDeleted = 'N' AND s.gu = :gu AND s.dong = :dong")
    List<Senior> findByStatusAndGuAndDong(@Param("status") String status, @Param("gu") String gu, @Param("dong") String dong);

    // ─── 카운트 (지역 필터) ──────────────────────────────────────────────────────

    @Query("SELECT COUNT(s) FROM Senior s WHERE s.isDeleted = 'N'")
    long countAllActive();

    @Query("SELECT COUNT(s) FROM Senior s WHERE s.isDeleted = 'N' AND s.gu = :gu")
    long countActiveByGu(@Param("gu") String gu);

    @Query("SELECT COUNT(s) FROM Senior s WHERE s.isDeleted = 'N' AND s.gu = :gu AND s.dong = :dong")
    long countActiveByGuAndDong(@Param("gu") String gu, @Param("dong") String dong);

    @Query("SELECT COUNT(s) FROM Senior s WHERE s.status = :status AND s.isDeleted = 'N'")
    long countByStatus(@Param("status") String status);

    @Query("SELECT COUNT(s) FROM Senior s WHERE s.status = :status AND s.isDeleted = 'N' AND s.gu = :gu")
    long countByStatusAndGu(@Param("status") String status, @Param("gu") String gu);

    @Query("SELECT COUNT(s) FROM Senior s WHERE s.status = :status AND s.isDeleted = 'N' AND s.gu = :gu AND s.dong = :dong")
    long countByStatusAndGuAndDong(@Param("status") String status, @Param("gu") String gu, @Param("dong") String dong);

    // ─── 스케줄러 ────────────────────────────────────────────────────────────────

    @Modifying
    @Query("""
        UPDATE Senior s SET s.status = '확인요망'
        WHERE s.isDeleted = 'N'
          AND s.status = '정상'
          AND s.id NOT IN (
              SELECT sl.senior.id FROM SignalLog sl
              WHERE sl.receivedAt >= :windowStart
                AND sl.receivedAt <= :windowEnd
          )
        """)
    void updateStatusToConfirmRequired(
            @Param("windowStart") LocalDateTime windowStart,
            @Param("windowEnd") LocalDateTime windowEnd
    );
}
