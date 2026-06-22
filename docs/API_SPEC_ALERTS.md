# GET /alerts — 확인요망(미기상 Danger) 대상자 목록 조회

## 기본 정보

| 항목 | 내용 |
|------|------|
| **Method** | `GET` |
| **URL** | `/alerts` |
| **설명** | 당일 기상 윈도우(05:00~10:00) 내 기상 신호(SIGNAL_LOG)가 없는 활성 대상자 목록을 조회한다. |
| **인증** | 불필요 (추후 JWT 인증 추가 예정) |
| **담당자** | 서호정 |
| **우선순위** | 1 |

---

## 비즈니스 로직

### 미기상(Danger) 판단 기준

1. `SENIOR` 테이블에서 `is_deleted = 'N'`인 활성 대상자를 대상으로 한다.
2. 당일 **05:00 ~ 10:00** 사이에 `SIGNAL_LOG`가 **1건도 없는** 대상자를 "확인요망(Danger)"으로 분류한다.
3. 기상 윈도우 시간대(05:00~10:00) 이전에는 아직 판단 불가이므로, 전체 활성 대상자가 반환될 수 있다.
4. 기상 신호를 수신(`POST /signal`)하면 해당 대상자는 목록에서 자동 제외된다.

### severity(중증정도) 결정 로직

> 현재 `high`로 하드코딩. 추후 미기상 연속 일수 기반 로직으로 변경 예정.

| 값 | 의미 | 기준 (예정) |
|----|------|------------|
| `high` | 상 | 3일 이상 연속 미기상 |
| `mid` | 중 | 2일 연속 미기상 |
| `low` | 하 | 당일 미기상 |

---

## 요청 (Request)

### Query Parameters

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| `severity` | `string` | N | - | 중증정도 필터. `high` / `mid` / `low` 중 택1. 미입력 시 전체 조회. |
| `district` | `string` | N | - | 관할구역 필터. 주소(address)에 해당 문자열이 포함된 대상자만 조회. 예: `종로구`, `중구` |

### 요청 예시

```
GET /alerts
GET /alerts?severity=high
GET /alerts?district=종로구
GET /alerts?severity=high&district=종로구
```

---

## 응답 (Response)

### 성공 (200 OK)

`Content-Type: application/json`

**응답 바디:** `AlertResponse[]`

| 필드 | 타입 | Nullable | 설명 |
|------|------|----------|------|
| `id` | `number` | N | 대상자 고유 ID (SENIOR.id) |
| `name` | `string` | N | 대상자 이름 |
| `age` | `number` | N | 나이 |
| `phone` | `string` | N | 연락처 (예: `010-1234-5678`) |
| `address` | `string` | N | 주소 (관할구역 판별 기준) |
| `status` | `string` | N | 현재 상태 (정상 / 확인요망 / 확인완료 / 확인요망유지 / 응급호출) |
| `severity` | `string` | N | 중증정도 (`high` / `mid` / `low`) |
| `registeredAt` | `string` | N | 등록일 (`yyyy.MM.dd` 형식) |

### 응답 예시

```json
[
  {
    "id": 1,
    "name": "김영희",
    "age": 78,
    "phone": "010-1234-5678",
    "address": "서울 종로구 삼청동 12-3",
    "status": "정상",
    "severity": "high",
    "registeredAt": "2026.03.15"
  },
  {
    "id": 2,
    "name": "이순자",
    "age": 82,
    "phone": "010-2345-6789",
    "address": "서울 중구 을지로 45",
    "status": "정상",
    "severity": "high",
    "registeredAt": "2026.01.08"
  }
]
```

### 빈 결과 (200 OK)

미기상 대상자가 없거나 필터 조건에 해당하는 대상자가 없을 때:

```json
[]
```

### 에러 (500 Internal Server Error)

DB 연결 실패 등 서버 내부 오류 시:

```json
{
  "timestamp": "2026-06-22T02:40:14.487Z",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/alerts"
}
```

---

## 관련 테이블

### 사용 테이블

| 테이블 | 역할 |
|--------|------|
| `SENIOR` | 활성 대상자 조회 (`is_deleted = 'N'`) |
| `SIGNAL_LOG` | 당일 기상 윈도우 내 신호 존재 여부 판별 |

### SQL 쿼리 (핵심 로직)

```sql
-- 필터 없이 전체 조회
SELECT s.* FROM senior s
WHERE s.is_deleted = 'N'
  AND s.id NOT IN (
      SELECT sl.senior_id FROM signal_log sl
      WHERE sl.received_at >= '2026-06-22 05:00:00'
        AND sl.received_at <= '2026-06-22 10:00:00'
  );

-- 관할구역 필터 적용
SELECT s.* FROM senior s
WHERE s.is_deleted = 'N'
  AND s.address LIKE '%종로구%'
  AND s.id NOT IN (
      SELECT sl.senior_id FROM signal_log sl
      WHERE sl.received_at >= '2026-06-22 05:00:00'
        AND sl.received_at <= '2026-06-22 10:00:00'
  );
```

---

## 프론트엔드 연동

### 호출 위치

`/alert-list` 페이지 (확인요망 리스트)

### 동작

| 기능 | 동작 |
|------|------|
| **초기 로드** | 페이지 진입 시 `GET /alerts` 호출 → 전체 미기상 대상자 표시 |
| **중증정도 필터** | 셀렉트박스 변경 시 `?severity=high/mid/low` 파라미터 추가 후 재호출 |
| **관할구역 필터** | 셀렉트박스 변경 시 `?district=종로구` 파라미터 추가 후 재호출 |
| **복합 필터** | 중증정도 + 관할구역 동시 적용 가능 |
| **새로고침** | "전체 새로고침" 버튼 클릭 시 현재 필터 유지한 채 API 재호출 |

### 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `NEXT_PUBLIC_API_URL` | `http://localhost:8080` | 백엔드 API 기본 URL |

---

## 연관 API

| No | API | 관계 |
|----|-----|------|
| 5 | `GET /alerts/:id` | 목록의 대상자 클릭 → 상세 조회 |
| 6 | `POST /alerts/:id/confirm` | "확인" 버튼 → 확인 결과 기록 |
| 15 | `POST /signal` | APK 기상 신호 수신 → alerts 목록에서 제외됨 |
| 3 | `GET /dashboard/stats` | 대시보드 alertCount에 이 목록 건수 반영 |
