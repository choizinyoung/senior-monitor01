package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.Senior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeniorRepository extends JpaRepository<Senior, Long> {

    Optional<Senior> findByDeviceId(String deviceId);

    boolean existsByPhone(String phone);
    long countByIsDeleted(String isDeleted);

    @Query("""
        SELECT s FROM Senior s
        WHERE s.isDeleted = 'N'
          AND s.id NOT IN (
              SELECT sl.senior.id FROM SignalLog sl
              WHERE sl.receivedAt >= :windowStart
                AND sl.receivedAt <= :windowEnd
          )
        """)
    List<Senior> findDangerSeniors(
            @Param("windowStart") LocalDateTime windowStart,
            @Param("windowEnd") LocalDateTime windowEnd
    );

    @Query("""
        SELECT s FROM Senior s
        WHERE s.isDeleted = 'N'
          AND s.gu = :gu
          AND s.id NOT IN (
              SELECT sl.senior.id FROM SignalLog sl
              WHERE sl.receivedAt >= :windowStart
                AND sl.receivedAt <= :windowEnd
          )
        """)
    List<Senior> findDangerSeniorsByGu(
            @Param("windowStart") LocalDateTime windowStart,
            @Param("windowEnd") LocalDateTime windowEnd,
            @Param("gu") String gu
    );

    @Query("""
        SELECT s FROM Senior s
        WHERE s.isDeleted = 'N'
          AND s.gu = :gu
          AND s.dong = :dong
          AND s.id NOT IN (
              SELECT sl.senior.id FROM SignalLog sl
              WHERE sl.receivedAt >= :windowStart
                AND sl.receivedAt <= :windowEnd
          )
        """)
    List<Senior> findDangerSeniorsByGuAndDong(
            @Param("windowStart") LocalDateTime windowStart,
            @Param("windowEnd") LocalDateTime windowEnd,
            @Param("gu") String gu,
            @Param("dong") String dong
    );
}
