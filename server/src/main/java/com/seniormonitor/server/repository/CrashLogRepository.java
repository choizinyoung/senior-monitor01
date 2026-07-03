package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.CrashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrashLogRepository extends JpaRepository<CrashLog, Long> {
}
