package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.SignalLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SignalLogRepository extends JpaRepository<SignalLog, Long> {

    List<SignalLog> findTop200ByOrderByReceivedAtDesc();

    List<SignalLog> findBySeniorIdOrderByReceivedAtDesc(Long seniorId);

    List<SignalLog> findByReceivedAtBetweenOrderByReceivedAtDesc(
            LocalDateTime from, LocalDateTime to);

    List<SignalLog> findBySeniorIdAndReceivedAtBetweenOrderByReceivedAtDesc(
            Long seniorId, LocalDateTime from, LocalDateTime to);
}
