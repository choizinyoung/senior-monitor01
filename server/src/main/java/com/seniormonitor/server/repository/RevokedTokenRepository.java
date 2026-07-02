package com.seniormonitor.server.repository;

import com.seniormonitor.server.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {

    boolean existsByJti(String jti);

    @Modifying
    void deleteByExpiresAtBefore(LocalDateTime cutoff);
}
