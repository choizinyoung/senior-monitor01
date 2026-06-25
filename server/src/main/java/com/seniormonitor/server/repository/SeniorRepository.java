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
    long countByIsDeleted(String isDeleted);
    List<Senior> findByIsDeleted(String isDeleted);

    long countByStatusAndIsDeleted(String status, String isDeleted);

    List<Senior> findByStatusAndIsDeleted(String status, String isDeleted);

    List<Senior> findByStatusAndIsDeletedAndGu(String status, String isDeleted, String gu);

    List<Senior> findByStatusAndIsDeletedAndGuAndDong(String status, String isDeleted, String gu, String dong);

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
