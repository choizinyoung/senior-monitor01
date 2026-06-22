package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.SignalLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SignalLogRepository extends JpaRepository<SignalLog, Long> {

    List<SignalLog> findTop200ByOrderByReceivedAtDesc();

    List<SignalLog> findBySeniorIdOrderByReceivedAtDesc(Long seniorId);
}
