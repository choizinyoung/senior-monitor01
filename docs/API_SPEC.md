# 독거노인 안전 관리 시스템 — API 명세서

> 최종 수정: 2026-07-02  
> Base URL: `http://localhost:8082` (로컬 dev)  
> 공통 응답 래퍼: `{ "success": true, "data": ..., "error": null }`

---

## 목차

| No | API | Method | URL | 설명 | 인증 |
|----|-----|--------|-----|------|------|
| 1 | [대시보드 통계](#1-대시보드-통계) | GET | `/api/dashboard/stats` | 4대 지표 집계 | 로그인 필요 |
| 2 | [확인요망 리스트](#2-확인요망-리스트) | GET | `/api/alerts` | 미기상 대상자 목록 (필터링) | 로그인 필요 |
| 3 | [확인 처리](#3-확인-처리) | POST | `/api/alerts/:id/confirm` | 상태 변경 + 연락 이력 기록 | 로그인 필요 |
| 4 | [대상자 전체 목록](#4-대상자-전체-목록) | GET | `/api/seniors` | 활성 대상자 목록 | 로그인 필요 |
| 5 | [대상자 상세](#5-대상자-상세) | GET | `/api/seniors/:id` | 개별 대상자 정보 | 로그인 필요 |
| 6 | [대상자 등록](#6-대상자-등록) | POST | `/api/seniors/register` | APK 대상자 등록 | 공개 (APK) |
| 7 | [대상자 수정](#7-대상자-수정) | POST | `/api/seniors/:id/update` | 대상자 정보 수정 | 공개 (APK) |
| 8 | [대상자 삭제](#8-대상자-삭제-소프트) | DELETE | `/api/seniors/:id` | 소프트 삭제 (is_deleted = Y) | 로그인 필요 |
| 9 | [연락 이력 조회](#9-연락-이력-조회) | GET | `/api/seniors/:id/contacts` | 대상자별 연락 이력 | 로그인 필요 |
| 10 | [신호 이력 조회](#10-신호-이력-조회) | GET | `/api/seniors/:id/signals` | 대상자별 기상 신호 이력 | 로그인 필요 |
| 11 | [기상 신호 수신](#11-기상-신호-수신) | POST | `/api/signal` | APK → 서버 신호 수신 | 공개 (APK) |
| 12 | [전체 신호 목록](#12-전체-신호-목록) | GET | `/api/signals` | 최근 200건 신호 로그 | 로그인 필요 |
| 13 | [회원가입 신청](#13-회원가입-신청) | POST | `/api/auth/signup` | 담당자 계정 신청 | 공개 |
| 14 | [로그인](#14-로그인) | POST | `/api/auth/login` | JWT 토큰 발급 | 공개 |
| 15 | [로그아웃](#15-로그아웃) | POST | `/api/auth/logout` | 현재 토큰 무효화 | 로그인 필요 |
| 16 | [담당자 목록 조회](#16-담당자-목록-조회) | GET | `/api/managers` | 승인 전/후 담당자 목록 | MASTER 전용 |
| 17 | [담당자 정보 변경](#17-담당자-정보-변경) | POST | `/api/managers/:id/update` | 승인/승인해제, 관할지역 변경 | MASTER 전용 |
| 18 | [크래시 로그 제출](#18-크래시-로그-제출) | POST | `/api/crash-logs` | APK 크래시 로그 수신 | 공개 (APK) |

---

## 공통 사항

### 인증

로그인이 필요한 API는 `Authorization: Bearer {token}` 헤더로 로그인 시 발급받은 JWT를 전달해야 합니다.

- 담당자(MANAGER)가 대상자 조회 API를 호출하면 **본인의 관할지역(gu/dong)에 속한 대상자만** 조회됩니다. 관할지역을 아직 배정받지 못한 담당자는 빈 목록/0건 통계를 받습니다.
- MASTER는 지역 제한 없이 전체 데이터를 조회합니다.
- 담당자가 관할지역 밖의 대상자를 단건 조회/삭제/확인처리 시도하면 `ERR_FORBIDDEN_REGION` (403)을 반환합니다.
- `/api/managers/**`는 MASTER 권한만 호출 가능하며, 그 외 역할로 호출 시 `ERR_FORBIDDEN` (403)을 반환합니다.
- `/api/seniors/register`, `/api/seniors/:id/update`, `/api/signal`, `/api/crash-logs` (및 레거시 `/register`, `/signal`)은 APK 기기가 호출하는 엔드포인트로 인증 없이 공개되어 있습니다. 로그인 없이 호출되면 관할지역 제한도 적용되지 않습니다.

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
| `ERR_DUPLICATE_USERNAME` | 409 | 이미 사용 중인 아이디 |
| `ERR_DUPLICATE_EMAIL` | 409 | 이미 사용 중인 이메일 |
| `ERR_NOT_FOUND` | 404 | 대상자/담당자를 찾을 수 없음 |
| `ERR_UNREGISTERED_DEVICE` | 404 | 등록되지 않은 기기 |
| `ERR_UNAUTHORIZED` | 401 | 로그인(토큰)이 필요함 |
| `ERR_INVALID_CREDENTIALS` | 401 | 아이디/비밀번호 불일치 |
| `ERR_NOT_APPROVED` | 403 | 아직 승인되지 않은 계정으로 로그인 시도 |
| `ERR_FORBIDDEN` | 403 | 권한 없는 API 접근 (예: MANAGER가 /api/managers 호출) |
| `ERR_FORBIDDEN_REGION` | 403 | 담당자의 관할지역 밖의 대상자 접근 |

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

대상자의 기본 정보를 수정합니다. APK에서 호출하는 공개 API로, 로그인이 필요하지 않습니다.

| 항목 | 내용 |
|------|------|
| **Method** | `POST` |
| **URL** | `/api/seniors/{seniorId}/update` |
| **인증** | 불필요 (공개) |

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
  "deviceId": "DEVICE-ABC-123",
  "status": "정상"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `deviceId` | `string` | Y | APK 기기 고유값 |
| `status` | `string` | N | 대상자 상태 변경값. `정상` / `확인요망` / `확인완료` / `확인요망유지` / `응급호출` 중 하나 |

### 처리 흐름

1. `deviceId`로 Senior 조회 → 미등록 시 `ERR_UNREGISTERED_DEVICE` (404)
2. `status`가 함께 전달되면 대상자의 `SENIOR.status`를 해당 값으로 변경 (미전달 시 상태는 그대로 유지)
3. `SIGNAL_LOG` 생성 (`senior_id`, `received_at`, `signal_date`)

### 에러

- `ERR_UNREGISTERED_DEVICE` (404): 등록되지 않은 기기
- `ERR_INVALID_VALUE` (400): `status`가 허용된 값이 아님

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

## 13. 회원가입 신청

담당자가 계정을 신청합니다. 신청 직후에는 `status = PENDING`이며, MASTER가 승인해야 로그인할 수 있습니다.

| 항목 | 내용 |
|------|------|
| **Method** | `POST` |
| **URL** | `/api/auth/signup` |
| **HTTP Status** | `201 Created` |

### Request Body

```json
{
  "name": "홍길동",
  "username": "hong123",
  "password": "P@ssw0rd!",
  "phone": "010-1234-5678",
  "email": "hong@example.com"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `name` | `string` | Y | 담당자 이름 |
| `username` | `string` | Y | 로그인 아이디 (중복 불가) |
| `password` | `string` | Y | 비밀번호 (해시 저장) |
| `phone` | `string` | Y | 연락처 |
| `email` | `string` | Y | 이메일 (중복 불가) |

관할 지역(city/gu/dong)과 권한(role)은 신청 시 입력받지 않으며, role은 `MANAGER`, status는 `PENDING`으로 자동 설정됩니다.

### 응답

```json
{
  "success": true,
  "data": {
    "id": 4,
    "name": "홍길동",
    "username": "hong123",
    "phone": "010-1234-5678",
    "email": "hong@example.com",
    "city": null,
    "gu": null,
    "dong": null,
    "role": "MANAGER",
    "status": "PENDING",
    "createdAt": "2026-07-02T10:00:00",
    "updatedAt": "2026-07-02T10:00:00"
  }
}
```

### 에러

- `ERR_MISSING_FIELD` (400): 필수 필드 누락
- `ERR_DUPLICATE_USERNAME` (409): 이미 사용 중인 아이디
- `ERR_DUPLICATE_EMAIL` (409): 이미 사용 중인 이메일

---

## 14. 로그인

아이디/비밀번호를 검증하고 JWT 토큰을 발급합니다.

| 항목 | 내용 |
|------|------|
| **Method** | `POST` |
| **URL** | `/api/auth/login` |

### Request Body

```json
{
  "username": "hong123",
  "password": "P@ssw0rd!"
}
```

### 응답

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "manager": {
      "id": 4,
      "name": "홍길동",
      "username": "hong123",
      "phone": "010-1234-5678",
      "email": "hong@example.com",
      "city": "서울특별시",
      "gu": "노원구",
      "dong": null,
      "role": "MANAGER",
      "status": "APPROVED",
      "createdAt": "2026-07-02T10:00:00",
      "updatedAt": "2026-07-02T11:00:00"
    }
  }
}
```

이후 요청은 `Authorization: Bearer {token}` 헤더로 인증합니다.

### 에러

- `ERR_MISSING_FIELD` (400): 필수 필드 누락
- `ERR_INVALID_CREDENTIALS` (401): 아이디 또는 비밀번호 불일치
- `ERR_NOT_APPROVED` (403): 아직 승인되지 않은 계정 (`status != APPROVED`)

---

## 15. 로그아웃

현재 사용 중인 토큰을 즉시 무효화합니다. 로그아웃 이후 같은 토큰으로 다시 요청하면 `ERR_UNAUTHORIZED` (401)이 반환됩니다.

| 항목 | 내용 |
|------|------|
| **Method** | `POST` |
| **URL** | `/api/auth/logout` |
| **인증** | `Authorization: Bearer {token}` 필요 |

### Request Body

없음 (헤더의 토큰만 사용)

### 응답

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

### 동작 방식

JWT는 서버에 상태를 저장하지 않는 방식이라 발급 후에는 만료 전까지 유효한 것이 원칙이지만, 이 API는 호출된 토큰의 고유값(jti)을 서버의 무효화 목록(REVOKED_TOKEN 테이블)에 기록해 즉시 재사용을 막습니다. 무효화된 토큰은 자연 만료 시점이 지나면 배치로 정리됩니다.

### 에러

- `ERR_UNAUTHORIZED` (401): 로그인하지 않은 상태(토큰 없음/만료/이미 로그아웃됨)로 호출

---

## 16. 담당자 목록 조회

승인 전/후 담당자를 모두 포함한 목록을 조회합니다. MASTER 전용 API입니다.

| 항목 | 내용 |
|------|------|
| **Method** | `GET` |
| **URL** | `/api/managers` |

### Query Parameters

| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `status` | `string` | N | `PENDING` / `APPROVED` 필터 |

### 응답

ManagerResponse 배열 (비밀번호 제외, 14번과 동일한 형태)

### 에러

- `ERR_FORBIDDEN` (403): MASTER가 아닌 계정으로 호출

---

## 17. 담당자 정보 변경

담당자의 승인 상태(승인/승인해제)와 관할 지역을 변경합니다. MASTER 전용 API입니다.

| 항목 | 내용 |
|------|------|
| **Method** | `POST` |
| **URL** | `/api/managers/{managerId}/update` |

### Request Body

변경할 필드만 전송합니다.

```json
{
  "status": "APPROVED",
  "city": "서울특별시",
  "gu": "노원구",
  "dong": "상계동"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `status` | `string` | N | `PENDING`(승인해제) / `APPROVED`(승인) |
| `city` | `string` | N | 관할 시/도 |
| `gu` | `string` | N | 관할 시/군/구 |
| `dong` | `string` | N | 관할 읍/면/동 (미입력 시 gu 전체 관할) |

### 응답

수정된 ManagerResponse 반환

### 에러

- `ERR_NOT_FOUND` (404): 담당자를 찾을 수 없음
- `ERR_INVALID_VALUE` (400): status가 PENDING / APPROVED가 아님
- `ERR_FORBIDDEN` (403): MASTER가 아닌 계정으로 호출

---

## 18. 크래시 로그 제출

APK 앱이 크래시 났을 때 로그를 서버에 기록합니다. 로그인이 필요하지 않은 공개 API입니다. (조회용 API는 아직 없습니다.)

| 항목 | 내용 |
|------|------|
| **Method** | `POST` |
| **URL** | `/api/crash-logs` |
| **HTTP Status** | `201 Created` |
| **인증** | 불필요 (공개) |

### Request Body

```json
{
  "deviceId": "DEVICE-ABC-123",
  "message": "java.lang.NullPointerException: ...\n\tat com.example...",
  "appVersion": "1.2.0",
  "occurredAt": "2026-07-03T09:12:00"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `deviceId` | `string` | Y | APK 기기 고유값 |
| `message` | `string` | Y | 크래시 로그/스택트레이스 내용 |
| `appVersion` | `string` | N | 크래시 발생 당시 앱 버전 |
| `occurredAt` | `string` | N | 크래시 발생 시각 (ISO 형식). 미입력 시 기록 안 함 |

`deviceId`는 `SENIOR` 테이블과 연결(FK)되지 않습니다. 대상자 등록 전에 발생한 크래시도 그대로 기록할 수 있도록 별도 문자열로만 저장합니다.

### 응답

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

### 에러

- `ERR_MISSING_FIELD` (400): deviceId 또는 message 누락

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

### MANAGER (담당자 계정)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | 고유 ID |
| `name` | VARCHAR(20) NOT NULL | 담당자 이름 |
| `username` | VARCHAR(50) UNIQUE NOT NULL | 로그인 아이디 |
| `password` | VARCHAR NOT NULL | 비밀번호 해시 (BCrypt) |
| `phone` | VARCHAR(20) NOT NULL | 연락처 |
| `email` | VARCHAR(100) UNIQUE NOT NULL | 이메일 |
| `city` | VARCHAR(20) | 관할 시/도 (미배정 시 NULL) |
| `gu` | VARCHAR(20) | 관할 시/군/구 (미배정 시 NULL) |
| `dong` | VARCHAR(20) | 관할 읍/면/동 (NULL이면 gu 전체 관할) |
| `role` | VARCHAR(10) NOT NULL | 권한 (`MASTER` / `MANAGER`) |
| `status` | VARCHAR(10) NOT NULL | 승인 상태 (`PENDING` / `APPROVED`) |
| `created_at` | TIMESTAMP NOT NULL | 가입 신청일시 |
| `updated_at` | TIMESTAMP NOT NULL | 수정일시 |

MASTER 계정은 회원가입 API로 생성되지 않으며, 별도로 발급합니다.

### REVOKED_TOKEN (로그아웃된 토큰 목록)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | 고유 ID |
| `jti` | VARCHAR(100) UNIQUE NOT NULL | 무효화된 토큰의 고유값 (JWT `jti` 클레임) |
| `expires_at` | TIMESTAMP NOT NULL | 원래 토큰의 만료 시각 (이후 배치로 정리됨) |

### CRASH_LOG (크래시 로그)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | 고유 ID |
| `device_id` | VARCHAR(100) NOT NULL | APK 기기 고유값 (SENIOR와 FK 아님) |
| `message` | TEXT NOT NULL | 크래시 로그/스택트레이스 |
| `app_version` | VARCHAR(20) | 크래시 발생 당시 앱 버전 |
| `occurred_at` | TIMESTAMP | 크래시 발생 시각 (클라이언트 제공) |
| `received_at` | TIMESTAMP NOT NULL | 서버 수신 시각 |
