package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.SignalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SignalLogRepository extends JpaRepository<SignalLog, Long> {

    List<SignalLog> findTop200ByOrderByReceivedAtDesc();

    List<SignalLog> findBySeniorIdOrderByReceivedAtDesc(Long seniorId);

    List<SignalLog> findBySeniorIdAndReceivedAtBetweenOrderByReceivedAtDesc(
            Long seniorId, LocalDateTime from, LocalDateTime to);

    // gu/dong이 null이면 해당 조건 없이 전체(MASTER), 값이 있으면 해당 지역으로 제한(MANAGER)
    @Query("""
        SELECT sl FROM SignalLog sl JOIN sl.senior s
        WHERE sl.receivedAt BETWEEN :from AND :to
          AND (:gu IS NULL OR s.gu = :gu)
          AND (:dong IS NULL OR s.dong = :dong)
        ORDER BY sl.receivedAt DESC
        """)
    List<SignalLog> findByReceivedAtBetweenAndRegionOrderByReceivedAtDesc(
            @Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
            @Param("gu") String gu, @Param("dong") String dong);
}
