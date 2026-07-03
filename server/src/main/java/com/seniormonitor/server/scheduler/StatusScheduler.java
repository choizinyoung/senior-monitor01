package com.seniormonitor.server.scheduler;

import com.seniormonitor.server.repository.SeniorRepository;
import com.seniormonitor.server.repository.SignalLogRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class StatusScheduler {

    private final SeniorRepository seniorRepository;
    private final SignalLogRepository signalLogRepository;

    public StatusScheduler(SeniorRepository seniorRepository,
                           SignalLogRepository signalLogRepository) {
        this.seniorRepository = seniorRepository;
        this.signalLogRepository = signalLogRepository;
    }

    // 매일 KST 11:00:30 실행
    @Scheduled(cron = "30 0 11 * * *", zone = "Asia/Seoul")
    @Transactional
    public void markNoSignalSeniorsAsDanger() {
        LocalDate today = LocalDate.now();
        LocalDateTime windowStart = LocalDateTime.of(today, LocalTime.of(5, 0));
        LocalDateTime windowEnd   = LocalDateTime.of(today, LocalTime.of(11, 0));

        seniorRepository.updateStatusToConfirmRequired(windowStart, windowEnd);
    }
}
