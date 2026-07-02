package com.seniormonitor.server.scheduler;

import com.seniormonitor.server.repository.RevokedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class TokenCleanupScheduler {

    private final RevokedTokenRepository revokedTokenRepository;

    public TokenCleanupScheduler(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    // 매일 KST 04:00:00 (UTC 19:00:00) 실행 — 이미 만료된 토큰은 블랙리스트에 남겨둘 필요가 없다.
    @Scheduled(cron = "0 0 19 * * *")
    @Transactional
    public void purgeExpiredRevokedTokens() {
        revokedTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
