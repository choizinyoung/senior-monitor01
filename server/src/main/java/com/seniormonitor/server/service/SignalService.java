package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.SignalRequest;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.entity.SignalLog;
import com.seniormonitor.server.exception.NotFoundException;
import com.seniormonitor.server.repository.SeniorRepository;
import com.seniormonitor.server.repository.SignalLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public void receive(SignalRequest req) {
        Senior senior = seniorRepository.findByDeviceId(req.getDeviceId())
                .orElseThrow(() -> new NotFoundException("ERR_UNREGISTERED_DEVICE", "등록되지 않은 기기입니다."));

        SignalLog log = new SignalLog();
        log.setSenior(senior);
        log.setSignalDate(LocalDate.now().format(DATE_FMT));
        signalLogRepository.save(log);

        if ("확인요망".equals(senior.getStatus())) {
            senior.setStatus("정상");
            seniorRepository.save(senior);
        }
    }

    @Transactional(readOnly = true)
    public List<SignalLog> getRecent() {
        return signalLogRepository.findTop200ByOrderByReceivedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<SignalLog> getSignalsBySenior(Long seniorId) {
        return signalLogRepository.findBySeniorIdOrderByReceivedAtDesc(seniorId);
    }
}
