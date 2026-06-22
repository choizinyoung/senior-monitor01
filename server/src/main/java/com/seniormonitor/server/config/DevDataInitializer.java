package com.seniormonitor.server.config;

import com.seniormonitor.server.entity.ContactHistory;
import com.seniormonitor.server.entity.Senior;
import com.seniormonitor.server.entity.SignalLog;
import com.seniormonitor.server.repository.ContactHistoryRepository;
import com.seniormonitor.server.repository.SeniorRepository;
import com.seniormonitor.server.repository.SignalLogRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    private final SeniorRepository seniorRepository;
    private final SignalLogRepository signalLogRepository;
    private final ContactHistoryRepository contactHistoryRepository;

    public DevDataInitializer(SeniorRepository seniorRepository,
                               SignalLogRepository signalLogRepository,
                               ContactHistoryRepository contactHistoryRepository) {
        this.seniorRepository = seniorRepository;
        this.signalLogRepository = signalLogRepository;
        this.contactHistoryRepository = contactHistoryRepository;
    }

    @Override
    public void run(String... args) {
        String today     = LocalDate.now().toString();
        String yesterday = LocalDate.now().minusDays(1).toString();
        String twoDaysAgo = LocalDate.now().minusDays(2).toString();

        // ── 정상 (25명) ───────────────────────────────────────────────
        List<Senior> normal = List.of(
            saveSenior("DEV-001", "김영순", 78, "010-1111-0001", "서울 노원구 상계동 102-3", "정상"),
            saveSenior("DEV-002", "이복순", 82, "010-1111-0002", "서울 노원구 월계동 55-1", "정상"),
            saveSenior("DEV-003", "박정자", 75, "010-1111-0003", "서울 노원구 공릉동 8-12", "정상"),
            saveSenior("DEV-004", "강옥분", 80, "010-1111-0004", "서울 도봉구 쌍문동 201-4", "정상"),
            saveSenior("DEV-005", "윤말례", 76, "010-1111-0005", "서울 도봉구 방학동 37-9", "정상"),
            saveSenior("DEV-006", "조순희", 73, "010-1111-0006", "서울 도봉구 창동 88-2", "정상"),
            saveSenior("DEV-007", "한명자", 79, "010-1111-0007", "서울 강북구 미아동 14-5", "정상"),
            saveSenior("DEV-008", "신옥자", 84, "010-1111-0008", "서울 강북구 번동 93-7", "정상"),
            saveSenior("DEV-009", "임순남", 77, "010-1111-0009", "서울 강북구 수유동 210-1", "정상"),
            saveSenior("DEV-010", "오정순", 81, "010-1111-0010", "서울 성북구 길음동 45-3", "정상"),
            saveSenior("DEV-011", "권말순", 74, "010-1111-0011", "서울 성북구 하월곡동 6-8", "정상"),
            saveSenior("DEV-012", "류순자", 86, "010-1111-0012", "서울 성북구 종암동 112-2", "정상"),
            saveSenior("DEV-013", "나복례", 72, "010-1111-0013", "서울 중랑구 면목동 33-6", "정상"),
            saveSenior("DEV-014", "문금순", 83, "010-1111-0014", "서울 중랑구 상봉동 77-4", "정상"),
            saveSenior("DEV-015", "서말례", 78, "010-1111-0015", "서울 중랑구 묵동 19-11", "정상"),
            saveSenior("DEV-016", "홍순분", 76, "010-1111-0016", "서울 동대문구 회기동 55-9", "정상"),
            saveSenior("DEV-017", "전옥희", 80, "010-1111-0017", "서울 동대문구 이문동 38-2", "정상"),
            saveSenior("DEV-018", "남정순", 71, "010-1111-0018", "서울 동대문구 장안동 101-5", "정상"),
            saveSenior("DEV-019", "하말순", 85, "010-1111-0019", "서울 광진구 중곡동 67-3", "정상"),
            saveSenior("DEV-020", "배복순", 77, "010-1111-0020", "서울 광진구 군자동 22-8", "정상"),
            saveSenior("DEV-021", "장정희", 82, "010-1111-0021", "서울 광진구 화양동 48-1", "정상"),
            saveSenior("DEV-022", "민순자", 74, "010-1111-0022", "서울 성동구 마장동 91-6", "정상"),
            saveSenior("DEV-023", "곽영자", 79, "010-1111-0023", "서울 성동구 행당동 15-3", "정상"),
            saveSenior("DEV-024", "유말례", 83, "010-1111-0024", "서울 성동구 금호동 53-7", "정상"),
            saveSenior("DEV-025", "석옥분", 76, "010-1111-0025", "서울 성동구 응봉동 29-4", "정상")
        );

        // ── 확인요망 (10명) ───────────────────────────────────────────
        List<Senior> needCheck = List.of(
            saveSenior("DEV-026", "김말순", 87, "010-2222-0001", "서울 노원구 하계동 44-2", "확인요망"),
            saveSenior("DEV-027", "이영자", 83, "010-2222-0002", "서울 노원구 중계동 178-5", "확인요망"),
            saveSenior("DEV-028", "박순희", 79, "010-2222-0003", "서울 도봉구 도봉동 62-1", "확인요망"),
            saveSenior("DEV-029", "최복례", 85, "010-2222-0004", "서울 강북구 인수동 7-9", "확인요망"),
            saveSenior("DEV-030", "정순분", 77, "010-2222-0005", "서울 성북구 정릉동 33-4", "확인요망"),
            saveSenior("DEV-031", "강정자", 91, "010-2222-0006", "서울 중랑구 신내동 204-8", "확인요망"),
            saveSenior("DEV-032", "윤복순", 80, "010-2222-0007", "서울 동대문구 전농동 56-2", "확인요망"),
            saveSenior("DEV-033", "조옥희", 74, "010-2222-0008", "서울 광진구 자양동 83-6", "확인요망"),
            saveSenior("DEV-034", "한말례", 88, "010-2222-0009", "서울 성동구 사근동 11-3", "확인요망"),
            saveSenior("DEV-035", "신정순", 82, "010-2222-0010", "서울 성동구 옥수동 77-1", "확인요망")
        );

        // ── 확인완료 (7명) ────────────────────────────────────────────
        List<Senior> checkDone = List.of(
            saveSenior("DEV-036", "임옥자", 76, "010-3333-0001", "서울 노원구 월계동 130-3", "확인완료"),
            saveSenior("DEV-037", "오순자", 81, "010-3333-0002", "서울 도봉구 창동 22-7", "확인완료"),
            saveSenior("DEV-038", "권정희", 73, "010-3333-0003", "서울 강북구 우이동 45-9", "확인완료"),
            saveSenior("DEV-039", "류말순", 84, "010-3333-0004", "서울 성북구 돈암동 67-2", "확인완료"),
            saveSenior("DEV-040", "나영자", 78, "010-3333-0005", "서울 중랑구 망우동 38-5", "확인완료"),
            saveSenior("DEV-041", "문옥분", 86, "010-3333-0006", "서울 광진구 중곡동 91-4", "확인완료"),
            saveSenior("DEV-042", "서정순", 72, "010-3333-0007", "서울 성동구 왕십리동 14-8", "확인완료")
        );

        // ── 확인요망유지 (5명) ────────────────────────────────────────
        List<Senior> keepCheck = List.of(
            saveSenior("DEV-043", "홍말례", 89, "010-4444-0001", "서울 노원구 공릉동 55-1", "확인요망유지"),
            saveSenior("DEV-044", "전복순", 83, "010-4444-0002", "서울 도봉구 방학동 120-6", "확인요망유지"),
            saveSenior("DEV-045", "남순희", 77, "010-4444-0003", "서울 강북구 미아동 33-2", "확인요망유지"),
            saveSenior("DEV-046", "하정자", 91, "010-4444-0004", "서울 중랑구 면목동 88-7", "확인요망유지"),
            saveSenior("DEV-047", "배옥희", 80, "010-4444-0005", "서울 광진구 화양동 21-3", "확인요망유지")
        );

        // ── 응급호출 (3명) ────────────────────────────────────────────
        List<Senior> emergency = List.of(
            saveSenior("DEV-048", "장순자", 92, "010-5555-0001", "서울 노원구 상계동 77-4", "응급호출"),
            saveSenior("DEV-049", "민말순", 85, "010-5555-0002", "서울 도봉구 쌍문동 44-9", "응급호출"),
            saveSenior("DEV-050", "곽복례", 88, "010-5555-0003", "서울 성북구 길음동 102-6", "응급호출")
        );

        // ── 기상신호 ──────────────────────────────────────────────────
        // 정상: 오늘 신호 수신
        String[] normalTimes = {"06:52","07:05","07:18","06:41","07:33","07:09","06:58","07:22","07:44","06:35",
                                 "07:11","07:28","06:47","07:03","07:55","06:39","07:16","07:42","06:53","07:07",
                                 "07:31","06:44","07:19","07:48","06:56"};
        for (int i = 0; i < normal.size(); i++) {
            saveSignal(normal.get(i), today, normalTimes[i]);
        }

        // 확인요망: 오늘 신호 없음, 어제까지 수신
        String[] needCheckTimes = {"07:14","06:48","07:32","07:05","06:59","07:23","06:44","07:51","07:08","06:37"};
        for (int i = 0; i < needCheck.size(); i++) {
            saveSignal(needCheck.get(i), yesterday, needCheckTimes[i]);
        }

        // 확인완료: 오늘 신호 없음, 이틀 전까지 수신
        String[] checkDoneTimes = {"07:21","06:55","07:39","07:02","06:46","07:17","07:58"};
        for (int i = 0; i < checkDone.size(); i++) {
            saveSignal(checkDone.get(i), twoDaysAgo, checkDoneTimes[i]);
        }

        // 확인요망유지: 어제까지 수신
        String[] keepCheckTimes = {"07:06","06:52","07:28","07:41","06:33"};
        for (int i = 0; i < keepCheck.size(); i++) {
            saveSignal(keepCheck.get(i), yesterday, keepCheckTimes[i]);
        }

        // 응급호출: 어제까지 수신
        saveSignal(emergency.get(0), yesterday, "07:13");
        saveSignal(emergency.get(1), yesterday, "06:49");
        saveSignal(emergency.get(2), yesterday, "07:36");

        // ── 연락 이력 ─────────────────────────────────────────────────
        // 확인요망 → 연락 시도 이력
        saveContact(needCheck.get(0), "김담당", "확인요망",    "전화 부재 중, 문자 남김",                  LocalDateTime.now().minusHours(3));
        saveContact(needCheck.get(1), "이담당", "확인요망",    "수신 없음, 이웃에게 확인 요청",             LocalDateTime.now().minusHours(4));
        saveContact(needCheck.get(2), "김담당", "확인요망",    "2회 시도 모두 부재",                       LocalDateTime.now().minusHours(2));
        saveContact(needCheck.get(3), "박담당", "확인요망",    "외출 중인 것으로 이웃 전달",               LocalDateTime.now().minusHours(5));
        saveContact(needCheck.get(4), "이담당", "확인요망",    "연락 안 됨, 내일 오전 재시도 예정",        LocalDateTime.now().minusHours(1));

        // 확인완료 → 연락 성공 이력
        saveContact(checkDone.get(0), "박담당", "확인완료",    "전화 통화, 건강 양호 확인",                LocalDateTime.now().minusHours(2));
        saveContact(checkDone.get(1), "김담당", "확인완료",    "직접 방문, 수면 중이셨음",                 LocalDateTime.now().minusHours(6));
        saveContact(checkDone.get(2), "이담당", "확인완료",    "통화 완료, 병원 다녀오신 것 확인",         LocalDateTime.now().minusHours(3));
        saveContact(checkDone.get(3), "박담당", "확인완료",    "자녀가 방문 중이라 안전 확인",             LocalDateTime.now().minusHours(5));
        saveContact(checkDone.get(4), "김담당", "확인완료",    "목욕탕 다녀오심, 건강 이상 없음",          LocalDateTime.now().minusHours(4));
        saveContact(checkDone.get(5), "이담당", "확인완료",    "전화 통화 완료, 식사도 하셨다고 함",       LocalDateTime.now().minusHours(2));
        saveContact(checkDone.get(6), "박담당", "확인완료",    "통화 완료, 이상 없음",                     LocalDateTime.now().minusHours(7));

        // 확인요망유지 → 여러 차례 시도 이력
        saveContact(keepCheck.get(0), "김담당", "확인요망",    "1차 시도 부재",                            LocalDateTime.now().minusHours(8));
        saveContact(keepCheck.get(0), "김담당", "확인요망유지", "2차 시도도 연락 안 됨, 계속 모니터링",   LocalDateTime.now().minusHours(4));
        saveContact(keepCheck.get(1), "이담당", "확인요망",    "전화 연결 안 됨",                          LocalDateTime.now().minusHours(7));
        saveContact(keepCheck.get(1), "이담당", "확인요망유지", "이웃 통해 확인 시도했으나 미확인",        LocalDateTime.now().minusHours(3));
        saveContact(keepCheck.get(2), "박담당", "확인요망유지", "가족에게 연락, 직접 확인 요청",           LocalDateTime.now().minusHours(5));
        saveContact(keepCheck.get(3), "김담당", "확인요망유지", "반복 부재, 관할 파출소에 확인 요청",      LocalDateTime.now().minusHours(2));
        saveContact(keepCheck.get(4), "이담당", "확인요망유지", "3회 시도 모두 불통, 내일 방문 예정",      LocalDateTime.now().minusHours(1));

        // 응급호출 이력
        saveContact(emergency.get(0), "박담당", "확인요망",   "전화 부재",                                 LocalDateTime.now().minusHours(6));
        saveContact(emergency.get(0), "박담당", "응급호출",   "방문 시 쓰러져 계심, 119 출동 요청",       LocalDateTime.now().minusHours(5));
        saveContact(emergency.get(1), "김담당", "응급호출",   "자녀 신고, 의식 불명으로 병원 이송",       LocalDateTime.now().minusHours(3));
        saveContact(emergency.get(2), "이담당", "확인요망",   "수신 없음",                                 LocalDateTime.now().minusHours(8));
        saveContact(emergency.get(2), "이담당", "응급호출",   "이웃 신고, 낙상으로 119 출동",             LocalDateTime.now().minusHours(7));
    }

    private Senior saveSenior(String deviceId, String name, int age,
                               String phone, String address, String status) {
        Senior s = new Senior();
        s.setDeviceId(deviceId);
        s.setName(name);
        s.setAge(age);
        s.setPhone(phone);
        s.setAddress(address);
        s.setStatus(status);
        return seniorRepository.save(s);
    }

    private void saveSignal(Senior senior, String date, String time) {
        SignalLog log = new SignalLog();
        log.setSenior(senior);
        log.setSignalDate(date);
        log.setReceivedAt(LocalDateTime.parse(date + "T" + time + ":00"));
        signalLogRepository.save(log);
    }

    private void saveContact(Senior senior, String managerName, String resultStatus,
                              String memo, LocalDateTime contactedAt) {
        ContactHistory h = new ContactHistory();
        h.setSenior(senior);
        h.setManagerName(managerName);
        h.setResultStatus(resultStatus);
        h.setMemo(memo);
        h.setContactedAt(contactedAt);
        contactHistoryRepository.save(h);
    }
}
