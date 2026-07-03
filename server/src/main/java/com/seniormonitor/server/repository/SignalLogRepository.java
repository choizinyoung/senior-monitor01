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

    // 전체 신호 (지역 필터 없음 — MASTER)
    List<SignalLog> findByReceivedAtBetweenOrderByReceivedAtDesc(LocalDateTime from, LocalDateTime to);

    // 구 기준 필터
    @Query("""
        SELECT sl FROM SignalLog sl JOIN sl.senior s
        WHERE sl.receivedAt BETWEEN :from AND :to
          AND s.gu = :gu
        ORDER BY sl.receivedAt DESC
        """)
    List<SignalLog> findByReceivedAtBetweenAndGuOrderByReceivedAtDesc(
            @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("gu") String gu);

    // 구+동 기준 필터
    @Query("""
        SELECT sl FROM SignalLog sl JOIN sl.senior s
        WHERE sl.receivedAt BETWEEN :from AND :to
          AND s.gu = :gu AND s.dong = :dong
        ORDER BY sl.receivedAt DESC
        """)
    List<SignalLog> findByReceivedAtBetweenAndGuAndDongOrderByReceivedAtDesc(
            @Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
            @Param("gu") String gu, @Param("dong") String dong);
}
