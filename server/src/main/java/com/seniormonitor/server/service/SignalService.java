package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.SignalRequest;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.entity.SignalLog;
import com.seniormonitor.server.repository.SeniorRepository;
import com.seniormonitor.server.repository.SignalLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SignalService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final SignalLogRepository signalLogRepository;
    private final SeniorRepository seniorRepository;

    public SignalService(SignalLogRepository signalLogRepository, SeniorRepository seniorRepository) {
        this.signalLogRepository = signalLogRepository;
        this.seniorRepository = seniorRepository;
    }

    public Map<String, Object> receive(SignalRequest req) {
        Optional<Senior> seniorOpt = seniorRepository.findByDeviceId(req.getDeviceId());
        if (seniorOpt.isEmpty()) {
            return Map.of("error", "등록되지 않은 기기입니다");
        }

        Senior senior = seniorOpt.get();

        SignalLog log = new SignalLog();
        log.setSenior(senior);
        log.setSignalDate(LocalDate.now().format(DATE_FMT));
        signalLogRepository.save(log);

        if ("확인요망".equals(senior.getStatus())) {
            senior.setStatus("정상");
            seniorRepository.save(senior);
        }

        return Map.of("success", true);
    }

    @Transactional(readOnly = true)
    public List<SignalLog> getRecent() {
        return signalLogRepository.findTop200ByOrderByReceivedAtDesc();
    }
}
