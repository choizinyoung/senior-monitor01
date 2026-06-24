package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.ConfirmRequest;
import com.seniormonitor.server.entity.ContactHistory;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.exception.BadRequestException;
import com.seniormonitor.server.exception.NotFoundException;
import com.seniormonitor.server.repository.ContactHistoryRepository;
import com.seniormonitor.server.repository.SeniorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ContactHistoryService {

    private static final Set<String> ALLOWED_STATUS =
            Set.of("확인완료", "확인요망유지", "응급호출");

    private final ContactHistoryRepository contactHistoryRepository;
    private final SeniorRepository seniorRepository;

    public ContactHistoryService(ContactHistoryRepository contactHistoryRepository,
                                  SeniorRepository seniorRepository) {
        this.contactHistoryRepository = contactHistoryRepository;
        this.seniorRepository = seniorRepository;
    }

    public List<ContactHistory> getHistory(Long seniorId) {
        return contactHistoryRepository.findBySeniorIdOrderByContactedAtDesc(seniorId);
    }

    @Transactional
    public Senior confirm(Long seniorId, ConfirmRequest req) {
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new NotFoundException("ERR_NOT_FOUND", "대상자를 찾을 수 없습니다."));

        if ("정상".equals(senior.getStatus())) {
            throw new BadRequestException("ERR_INVALID_STATUS", "정상 상태인 대상자는 처리 결과를 변경할 수 없습니다.");
        }

        if (!ALLOWED_STATUS.contains(req.getResultStatus())) {
            throw new BadRequestException("ERR_INVALID_VALUE",
                    "resultStatus는 확인완료 / 확인요망유지 / 응급호출 중 하나여야 합니다.");
        }

        ContactHistory history = new ContactHistory();
        history.setSenior(senior);
        history.setManagerName(req.getManagerName());
        history.setResultStatus(req.getResultStatus());
        history.setMemo(req.getMemo());
        history.setContactedAt(req.getContactedAt() != null ? req.getContactedAt() : LocalDateTime.now());
        contactHistoryRepository.save(history);

        senior.setStatus(req.getResultStatus());
        return seniorRepository.save(senior);
    }
}
