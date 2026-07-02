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

    // gu/dong이 null이면 해당 조건 없이 전체(MASTER), 값이 있으면 해당 지역으로 제한(MANAGER)
    @Query("""
        SELECT COUNT(s) FROM Senior s
        WHERE s.isDeleted = 'N'
          AND (:gu IS NULL OR s.gu = :gu)
          AND (:dong IS NULL OR s.dong = :dong)
        """)
    long countActiveByRegion(@Param("gu") String gu, @Param("dong") String dong);

    @Query("""
        SELECT COUNT(s) FROM Senior s
        WHERE s.status = :status AND s.isDeleted = 'N'
          AND (:gu IS NULL OR s.gu = :gu)
          AND (:dong IS NULL OR s.dong = :dong)
        """)
    long countByStatusAndRegion(@Param("status") String status, @Param("gu") String gu, @Param("dong") String dong);

    @Query("""
        SELECT s FROM Senior s
        WHERE s.isDeleted = 'N'
          AND (:gu IS NULL OR s.gu = :gu)
          AND (:dong IS NULL OR s.dong = :dong)
        ORDER BY s.registeredAt DESC
        """)
    List<Senior> findActiveByRegion(@Param("gu") String gu, @Param("dong") String dong);

    @Query("""
        SELECT s FROM Senior s
        WHERE s.status = :status AND s.isDeleted = 'N'
          AND (:gu IS NULL OR s.gu = :gu)
          AND (:dong IS NULL OR s.dong = :dong)
        """)
    List<Senior> findByStatusAndRegion(@Param("status") String status, @Param("gu") String gu, @Param("dong") String dong);

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
