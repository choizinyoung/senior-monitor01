package com.seniormonitor.server.service;

import com.seniormonitor.server.dto.ConfirmRequest;
import com.seniormonitor.server.dto.ContactHistoryResponse;
import com.seniormonitor.server.entity.ContactHistory;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.exception.BadRequestException;
import com.seniormonitor.server.exception.NotFoundException;
import com.seniormonitor.server.repository.ContactHistoryRepository;
import com.seniormonitor.server.repository.SeniorRepository;
import com.seniormonitor.server.security.CurrentManager;
import com.seniormonitor.server.security.RegionAccess;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public List<ContactHistory> getHistory(Long seniorId, CurrentManager manager) {
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new NotFoundException("ERR_NOT_FOUND", "대상자를 찾을 수 없습니다."));
        RegionAccess.assertAccessible(senior, manager);
        return contactHistoryRepository.findBySeniorIdOrderByContactedAtDesc(seniorId);
    }

    public List<ContactHistoryResponse> getAllHistory(String resultStatus, String city, String gu, String dong,
                                                        LocalDate from, LocalDate to, CurrentManager manager) {
        if (RegionAccess.isUnassigned(manager)) {
            return List.of();
        }
        // MANAGER는 본인 관할지역을 강제 적용하고, 배정 안 된 하위 단계만 조회 파라미터로 좁힐 수 있다.
        // MASTER는 조회 파라미터가 없으면 전체 지역을 기본값으로 조회한다.
        String effectiveCity = manager.isMaster() ? emptyToNull(city) : manager.city();
        String effectiveGu   = manager.isMaster() ? emptyToNull(gu)
                : (manager.gu() != null ? manager.gu() : emptyToNull(gu));
        String effectiveDong = manager.isMaster() ? emptyToNull(dong)
                : (manager.dong() != null ? manager.dong() : emptyToNull(dong));

        LocalDate fromDate = from != null ? from : LocalDate.now();
        LocalDate toDate   = to   != null ? to   : LocalDate.now();
        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end   = toDate.atTime(LocalTime.MAX);

        return queryHistory(resultStatus, effectiveCity, effectiveGu, effectiveDong, start, end)
                .stream()
                .map(ContactHistoryResponse::new)
                .toList();
    }

    private List<ContactHistory> queryHistory(String status, String city, String gu, String dong,
                                               LocalDateTime start, LocalDateTime end) {
        if (status != null) {
            if (gu != null && dong != null) return contactHistoryRepository.findAllByStatusAndGuAndDongAndDateRange(status, gu, dong, start, end);
            if (gu != null)                 return contactHistoryRepository.findAllByStatusAndGuAndDateRange(status, gu, start, end);
            if (city != null)               return contactHistoryRepository.findAllByStatusAndCityAndDateRange(status, city, start, end);
            return contactHistoryRepository.findAllByStatusAndDateRange(status, start, end);
        }
        if (gu != null && dong != null) return contactHistoryRepository.findAllByGuAndDongAndDateRange(gu, dong, start, end);
        if (gu != null)                 return contactHistoryRepository.findAllByGuAndDateRange(gu, start, end);
        if (city != null)               return contactHistoryRepository.findAllByCityAndDateRange(city, start, end);
        return contactHistoryRepository.findAllByDateRange(start, end);
    }

    private static String emptyToNull(String value) {
        return (value == null || value.isEmpty()) ? null : value;
    }

    @Transactional
    public Senior confirm(Long seniorId, ConfirmRequest req, CurrentManager manager) {
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new NotFoundException("ERR_NOT_FOUND", "대상자를 찾을 수 없습니다."));
        RegionAccess.assertAccessible(senior, manager);

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

        String seniorStatus = switch (req.getResultStatus()) {
            case "확인완료" -> "정상";
            case "확인요망유지" -> "확인요망유지";
            default -> req.getResultStatus();
        };
        senior.setStatus(seniorStatus);
        return seniorRepository.save(senior);
    }
}
