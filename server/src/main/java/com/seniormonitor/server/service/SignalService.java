package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.SignalRequest;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.entity.SignalLog;
import com.seniormonitor.server.exception.BadRequestException;
import com.seniormonitor.server.exception.NotFoundException;
import com.seniormonitor.server.repository.SeniorRepository;
import com.seniormonitor.server.repository.SignalLogRepository;
import com.seniormonitor.server.security.CurrentManager;
import com.seniormonitor.server.security.RegionAccess;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class SignalService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Set<String> ALLOWED_STATUS =
            Set.of("정상", "확인요망", "확인완료", "확인요망유지", "응급호출");

    private final SignalLogRepository signalLogRepository;
    private final SeniorRepository seniorRepository;

    public SignalService(SignalLogRepository signalLogRepository, SeniorRepository seniorRepository) {
        this.signalLogRepository = signalLogRepository;
        this.seniorRepository = seniorRepository;
    }

    public void receive(SignalRequest req) {
        Senior senior = seniorRepository.findByDeviceId(req.getDeviceId())
                .orElseThrow(() -> new NotFoundException("ERR_UNREGISTERED_DEVICE", "등록되지 않은 기기입니다."));

        if (req.getStatus() != null) {
            if (!ALLOWED_STATUS.contains(req.getStatus())) {
                throw new BadRequestException("ERR_INVALID_VALUE",
                        "status는 정상 / 확인요망 / 확인완료 / 확인요망유지 / 응급호출 중 하나여야 합니다.");
            }
            senior.setStatus(req.getStatus());
            seniorRepository.save(senior);
        } else if ("확인요망".equals(senior.getStatus())) {
            // APK가 status를 별도로 보내지 않아도 신호 수신 시 확인요망 → 정상 자동 해제
            senior.setStatus("정상");
            seniorRepository.save(senior);
        }
        // 확인요망유지는 신호 수신과 무관하게 상태 유지

        SignalLog log = new SignalLog();
        log.setSenior(senior);
        log.setSignalDate(LocalDate.now().format(DATE_FMT));
        signalLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<SignalLog> getRecent(LocalDate from, LocalDate to, CurrentManager manager) {
        if (RegionAccess.isUnassigned(manager)) {
            return List.of();
        }
        LocalDateTime start = (from != null ? from : LocalDate.now()).atStartOfDay();
        LocalDateTime end   = (to   != null ? to   : LocalDate.now()).atTime(LocalTime.MAX);
        String gu   = RegionAccess.guFilter(manager);
        String dong = RegionAccess.dongFilter(manager);
        if (gu != null && dong != null)
            return signalLogRepository.findByReceivedAtBetweenAndGuAndDongOrderByReceivedAtDesc(start, end, gu, dong);
        if (gu != null)
            return signalLogRepository.findByReceivedAtBetweenAndGuOrderByReceivedAtDesc(start, end, gu);
        return signalLogRepository.findByReceivedAtBetweenOrderByReceivedAtDesc(start, end);
    }

    @Transactional(readOnly = true)
    public List<SignalLog> getSignalsBySenior(Long seniorId, LocalDate from, LocalDate to, CurrentManager manager) {
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new NotFoundException("ERR_NOT_FOUND", "대상자를 찾을 수 없습니다."));
        RegionAccess.assertAccessible(senior, manager);

        LocalDateTime start = (from != null ? from : LocalDate.now()).atStartOfDay();
        LocalDateTime end   = (to   != null ? to   : LocalDate.now()).atTime(LocalTime.MAX);
        return signalLogRepository.findBySeniorIdAndReceivedAtBetweenOrderByReceivedAtDesc(seniorId, start, end);
    }
}
