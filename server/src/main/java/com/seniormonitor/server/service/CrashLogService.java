package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.CrashLogRequest;
import com.seniormonitor.server.entity.CrashLog;
import com.seniormonitor.server.exception.BadRequestException;
import com.seniormonitor.server.repository.CrashLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrashLogService {

    private final CrashLogRepository crashLogRepository;

    public CrashLogService(CrashLogRepository crashLogRepository) {
        this.crashLogRepository = crashLogRepository;
    }

    public void report(CrashLogRequest req) {
        if (req.getDeviceId() == null || req.getMessage() == null) {
            throw new BadRequestException("ERR_MISSING_FIELD", "deviceId, message는 필수 항목입니다.");
        }

        CrashLog crashLog = new CrashLog();
        crashLog.setDeviceId(req.getDeviceId());
        crashLog.setMessage(req.getMessage());
        crashLog.setAppVersion(req.getAppVersion());
        crashLog.setOccurredAt(req.getOccurredAt());
        crashLogRepository.save(crashLog);
    }
}
