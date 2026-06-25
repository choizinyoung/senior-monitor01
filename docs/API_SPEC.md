# 독거노인 안전 관리 시스템 — API 명세서

> 최종 수정: 2026-06-25  
> Base URL: `http://localhost:8082` (로컬 dev)  
> 공통 응답 래퍼: `{ "success": true, "data": ..., "error": null }`

---

## 목차

| No | API | Method | URL | 설명 |
|----|-----|--------|-----|------|
| 1 | [대시보드 통계](#1-대시보드-통계) | GET | `/api/dashboard/stats` | 4대 지표 집계 |
| 2 | [확인요망 리스트](#2-확인요망-리스트) | GET | `/api/alerts` | 미기상 대상자 목록 (필터링) |
| 3 | [확인 처리](#3-확인-처리) | POST | `/api/alerts/:id/confirm` | 상태 변경 + 연락 이력 기록 |
| 4 | [대상자 전체 목록](#4-대상자-전체-목록) | GET | `/api/seniors` | 활성 대상자 목록 |
| 5 | [대상자 상세](#5-대상자-상세) | GET | `/api/seniors/:id` | 개별 대상자 정보 |
| 6 | [대상자 등록](#6-대상자-등록) | POST | `/api/seniors/register` | APK 대상자 등록 |
| 7 | [대상자 수정](#7-대상자-수정) | POST | `/api/seniors/:id/update` | 대상자 정보 수정 |
| 8 | [대상자 삭제](#8-대상자-삭제-소프트) | DELETE | `/api/seniors/:id` | 소프트 삭제 (is_deleted = Y) |
| 9 | [연락 이력 조회](#9-연락-이력-조회) | GET | `/api/seniors/:id/contacts` | 대상자별 연락 이력 |
| 10 | [신호 이력 조회](#10-신호-이력-조회) | GET | `/api/seniors/:id/signals` | 대상자별 기상 신호 이력 |
| 11 | [기상 신호 수신](#11-기상-신호-수신) | POST | `/api/signal` | APK → 서버 신호 수신 |
| 12 | [전체 신호 목록](#12-전체-신호-목록) | GET | `/api/signals` | 최근 200건 신호 로그 |

---

## 공통 사항

### 응답 형식

모든 API는 아래 래퍼로 응답합니다.

```json
{
  "success": true,
  "data": { ... },
  "error": null
}
```

에러 시:

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERR_NOT_FOUND",
    "message": "대상자를 찾을 수 없습니다."
  }
}
```

### 에러 코드

| 코드 | HTTP | 설명 |
|------|------|------|
| `ERR_MISSING_FIELD` | 400 | 필수 필드 누락 |
| `ERR_INVALID_STATUS` | 400 | 정상 상태 대상자는 확인 처리 불가 |
| `ERR_INVALID_VALUE` | 400 | 허용되지 않는 값 |
| `ERR_CANNOT_DELETE` | 400 | 확인요망 상태 대상자 삭제 불가 |
| `ERR_DUPLICATE` | 409 | 이미 등록된 기기 |
| `ERR_DUPLICATE_PHONE` | 409 | 이미 등록된 연락처 |
| `ERR_NOT_FOUND` | 404 | 대상자를 찾을 수 없음 |
| `ERR_UNREGISTERED_DEVICE` | 404 | 등록되지 않은 기기 |

### SENIOR 테이블 상태값

| status | 설명 |
|--------|------|
| `정상` | 기상 신호 정상 수신 |
| `확인요망` | 미기상 — 아직 처리되지 않음 |
| `확인완료` | 담당자가 확인 처리 완료 |
| `확인요망유지` | 확인 시도했으나 미해결 |
| `응급호출` | 응급 상황으로 이관 |

---

## 1. 대시보드 통계

대시보드 상단 4대 지표를 집계합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `GET` |
| **URL** | `/api/dashboard/stats` |

### 응답

```json
{
  "success": true,
  "data": {
    "totalSeniors": 50,
    "alertCount": 18,
    "confirmedTodayCount": 7,
    "emergencyTodayCount": 2
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `totalSeniors` | `number` | 활성 대상자 수 (`is_deleted = 'N'`) |
| `alertCount` | `number` | 확인요망 대상자 수 (미기상 + 미처리) |
| `confirmedTodayCount` | `number` | 오늘 확인완료 처리된 고유 대상자 수 (`DISTINCT senior_id`) |
| `emergencyTodayCount` | `number` | 오늘 응급호출 처리된 고유 대상자 수 (`DISTINCT senior_id`) |

### 비즈니스 로직

- `alertCount`: 당일 05:00~10:00 신호 없음 + `is_deleted = 'N'` + `status NOT IN ('확인완료', '응급호출')`
- `confirmedTodayCount` / `emergencyTodayCount`: 같은 대상자를 여러 번 처리해도 **1로만** 카운트

---

## 2. 확인요망 리스트

당일 기상 윈도우(05:00~10:00) 내 신호가 없는 미처리 대상자 목록을 조회합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `GET` |
| **URL** | `/api/alerts` |

### Query Parameters

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `severity` | `string` | N | 중증정도 필터 (`high` / `mid` / `low`) |
| `gu` | `string` | N | 관할구역 필터 (예: `노원구`) |
| `dong` | `string` | N | 세부지역 필터 (예: `상계동`). `gu`와 함께 사용 |

### 요청 예시

```
GET /api/alerts
GET /api/alerts?gu=노원구
GET /api/alerts?gu=노원구&dong=상계동
GET /api/alerts?severity=high&gu=성동구
```

### 응답

```json
{
  "success": true,
  "data": [
    {
      "id": 26,
      "name": "김말순",
      "age": 87,
      "phone": "010-2222-0001",
      "city": "서울특별시",
      "gu": "노원구",
      "dong": "하계동",
      "status": "확인요망",
      "severity": "high",
      "registeredAt": "2026.06.25"
    }
  ]
}
```

### 조회 제외 조건

- `is_deleted = 'Y'` (삭제된 대상자)
- `status = '확인완료'` (확인 처리 완료)
- `status = '응급호출'` (응급 이관 완료)
- 당일 05:00~10:00 사이에 `SIGNAL_LOG` 존재 (기상 확인됨)

---

## 3. 확인 처리

확인요망 대상자의 상태를 변경하고 연락 이력을 기록합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `POST` |
| **URL** | `/api/alerts/{seniorId}/confirm` |

### Request Body

```json
{
  "managerName": "박담당",
  "resultStatus": "확인완료",
  "memo": "전화 통화 완료, 건강 상태 양호",
  "contactedAt": "2026-06-25T10:30:00"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `managerName` | `string` | Y | 처리 담당자 이름 |
| `resultStatus` | `string` | Y | `확인완료` / `확인요망유지` / `응급호출` 중 택1 |
| `memo` | `string` | N | 연락 결과 메모 |
| `contactedAt` | `string` | N | 연락 시각 (ISO 형식). 미입력 시 현재 시각 |

### 응답

Senior 엔티티 (상태 변경 후) 반환

### 처리 흐름

1. `CONTACT_HISTORY` 테이블에 연락 이력 INSERT
2. `SENIOR.status`를 `resultStatus` 값으로 UPDATE
3. `status = '정상'`인 대상자는 처리 불가 → 400 에러

---

## 4. 대상자 전체 목록

활성 대상자 전체 목록을 조회합니다. (`is_deleted = 'N'`만 반환)

| 항목 | 내용 |
|------|------|
| **Method** | `GET` |
| **URL** | `/api/seniors` |

### 응답

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "deviceId": "DEV-001",
      "name": "김영순",
      "age": 78,
      "phone": "010-1111-0001",
      "city": "서울특별시",
      "gu": "노원구",
      "dong": "상계동",
      "status": "정상",
      "isDeleted": "N",
      "registeredAt": "2026-06-25T07:00:00",
      "updatedAt": "2026-06-25T07:00:00"
    }
  ]
}
```

---

## 5. 대상자 상세

개별 대상자의 상세 정보를 조회합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `GET` |
| **URL** | `/api/seniors/{seniorId}` |

### 응답

Senior 엔티티 단건 반환 (4번과 동일 형태)

---

## 6. 대상자 등록

APK에서 대상자를 등록합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `POST` |
| **URL** | `/api/seniors/register` |
| **HTTP Status** | `201 Created` |

### Request Body

```json
{
  "deviceId": "DEVICE-ABC-123",
  "name": "홍길동",
  "age": 78,
  "phone": "010-1234-5678",
  "city": "서울특별시",
  "gu": "노원구",
  "dong": "상계동"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `deviceId` | `string` | Y | APK 기기 고유값 |
| `name` | `string` | Y | 이름 |
| `age` | `number` | N | 나이 |
| `phone` | `string` | N | 연락처 |
| `city` | `string` | N | 시/도 |
| `gu` | `string` | N | 시/군/구 |
| `dong` | `string` | N | 읍/면/동 |

### 에러

- `ERR_DUPLICATE` (409): 동일 deviceId가 이미 등록됨
- `ERR_DUPLICATE_PHONE` (409): 동일 연락처가 이미 등록됨

---

## 7. 대상자 수정

대상자의 기본 정보를 수정합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `POST` |
| **URL** | `/api/seniors/{seniorId}/update` |

### Request Body

변경할 필드만 전송합니다.

```json
{
  "name": "홍길동",
  "age": 79,
  "phone": "010-9876-5432",
  "city": "서울특별시",
  "gu": "도봉구",
  "dong": "쌍문동"
}
```

### 응답

수정된 Senior 엔티티 반환

---

## 8. 대상자 삭제 (소프트)

대상자를 소프트 삭제합니다. DB에서 물리적으로 삭제하지 않고 `is_deleted = 'Y'`로 변경합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `DELETE` |
| **URL** | `/api/seniors/{seniorId}` |

### 응답

```json
{
  "success": true,
  "data": null
}
```

### 삭제 불가 조건

| 상태 | 삭제 가능 |
|------|----------|
| 정상 | O |
| 확인완료 | O |
| 확인요망유지 | O |
| 응급호출 | O |
| **확인요망** | **X** — `ERR_CANNOT_DELETE` (400) |

---

## 9. 연락 이력 조회

대상자의 연락 이력을 최신순으로 조회합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `GET` |
| **URL** | `/api/seniors/{seniorId}/contacts` |

### 응답

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "senior": { ... },
      "managerName": "박담당",
      "resultStatus": "확인완료",
      "memo": "전화 통화 완료, 건강 상태 양호",
      "contactedAt": "2026-06-25T10:30:00",
      "createdAt": "2026-06-25T10:31:00"
    }
  ]
}
```

---

## 10. 신호 이력 조회

대상자의 기상 신호 이력을 최신순으로 조회합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `GET` |
| **URL** | `/api/seniors/{seniorId}/signals` |

### 응답

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "receivedAt": "2026-06-25T07:15:00",
      "signalDate": "2026-06-25"
    }
  ]
}
```

---

## 11. 기상 신호 수신

APK에서 기상 신호를 서버로 전송합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `POST` |
| **URL** | `/api/signal` |
| **HTTP Status** | `201 Created` |

### Request Body

```json
{
  "deviceId": "DEVICE-ABC-123"
}
```

### 처리 흐름

1. `deviceId`로 Senior 조회 → 미등록 시 `ERR_UNREGISTERED_DEVICE` (404)
2. `SIGNAL_LOG` 생성 (`senior_id`, `received_at`, `signal_date`)
3. 해당 대상자 `status`가 `확인요망`이면 → `정상`으로 자동 전환

### 레거시 호환

`POST /signal` 경로도 동일하게 동작합니다.

---

## 12. 전체 신호 목록

최근 수신된 기상 신호 200건을 최신순으로 조회합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `GET` |
| **URL** | `/api/signals` |

### 응답

```json
{
  "success": true,
  "data": [
    {
      "id": 51,
      "receivedAt": "2026-06-25T07:15:00",
      "signalDate": "2026-06-25"
    }
  ]
}
```

---

## ERD 테이블 요약

### SENIOR (대상자)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | 고유 ID |
| `device_id` | VARCHAR(100) UNIQUE | APK 기기 고유값 |
| `name` | VARCHAR(20) NOT NULL | 이름 |
| `age` | INTEGER NOT NULL | 나이 |
| `phone` | VARCHAR(20) NOT NULL | 연락처 |
| `city` | VARCHAR(20) NOT NULL | 시/도 |
| `gu` | VARCHAR(20) NOT NULL | 시/군/구 |
| `dong` | VARCHAR(20) NOT NULL | 읍/면/동 |
| `status` | VARCHAR(10) NOT NULL | 상태 (정상/확인요망/확인완료/확인요망유지/응급호출) |
| `is_deleted` | VARCHAR(1) NOT NULL | 소프트 삭제 (Y/N) |
| `registered_at` | TIMESTAMP NOT NULL | 등록일시 |
| `updated_at` | TIMESTAMP NOT NULL | 수정일시 |

### SIGNAL_LOG (신호 로그)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | 고유 ID |
| `senior_id` | BIGINT FK | 대상자 ID |
| `received_at` | TIMESTAMP NOT NULL | 신호 수신 시각 |
| `signal_date` | VARCHAR(10) NOT NULL | 신호 날짜 (YYYY-MM-DD) |

### CONTACT_HISTORY (연락 이력)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | 고유 ID |
| `senior_id` | BIGINT FK | 대상자 ID |
| `manager_name` | VARCHAR(20) NOT NULL | 처리 담당자 |
| `result_status` | VARCHAR(10) NOT NULL | 처리결과 (확인완료/확인요망유지/응급호출) |
| `memo` | VARCHAR(500) | 메모 |
| `contacted_at` | TIMESTAMP NOT NULL | 연락 시각 |
| `created_at` | TIMESTAMP NOT NULL | 기록 생성 시각 |
