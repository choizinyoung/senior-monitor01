package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.Elder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ElderRepository extends JpaRepository<Elder, Long> {
    Optional<Elder> findByDeviceId(String deviceId);
}