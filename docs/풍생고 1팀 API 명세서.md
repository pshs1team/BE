# 풍생고 1팀 API 명세서 (초안)

기준: `풍생고 1팀 명세서.md`, `풍생고 1팀 ERD.md`

## 1. 공통

| 항목 | 내용 |
|---|---|
| Base URL | `/api` |
| 데이터 형식 | JSON (사진 업로드만 `multipart/form-data`) |
| 인증 | 로그인 후 받은 JWT를 `Authorization: Bearer <accessToken>` 헤더로 전송 |
| JWT 내용 | `userId`, `role`, 만료 시각 |
| 날짜 형식 | ISO 8601 문자열 (예: `2026-09-25T14:30:00`) |
| 상태값 | `처리대기` / `처리중` / `처리완료` (DB 값과 동일) |

### 공통 에러 응답

```json
{ "code": "REPORT_NOT_FOUND", "message": "존재하지 않는 신고입니다." }
```

| HTTP | 의미 |
|---|---|
| 400 | 요청값 오류 (필수값 누락, 형식 오류) |
| 401 | 로그인 안 됨 / 토큰 만료·오류 |
| 403 | 권한 없음 (학생이 관리자 API 호출, 타인의 신고 조회 등) |
| 404 | 대상 없음 |
| 409 | 중복 (이미 가입된 이메일) |

## 2. 엔드포인트 목록

| # | 메서드 | URL | 설명 | 권한 | 명세서 대응 |
|---|---|---|---|---|---|
| 1 | POST | `/api/auth/signup` | 회원가입 (학생) | 없음 | 로그인 > 회원가입 |
| 2 | POST | `/api/auth/login` | 로그인 | 없음 | 로그인 > 로그인 |
| 3 | POST | `/api/reports` | 신고 작성 | 학생 | 신고 작성 |
| 4 | GET | `/api/reports/me` | 내 신고 목록 | 학생 | 내 신고 |
| 5 | GET | `/api/reports/{reportId}` | 신고 상세 (학생/관리자 공용) | 학생(본인 것만), 관리자 | 신고 상세(학생), 신고상세(관리자) |
| 6 | GET | `/api/admin/reports/summary` | 신고 건수 통계 | 관리자 | 신고 대시보드 > 신고 현황 요약 |
| 7 | GET | `/api/admin/reports` | 전체 신고 목록 (상태 필터) | 관리자 | 신고 대시보드 > 신고 목록 |
| 8 | PATCH | `/api/admin/reports/{reportId}/status` | 신고 상태 변경 | 관리자 | 신고상세(관리자) > 처리 |

- 로그아웃은 JWT 방식이라 서버 API가 없음. 프론트에서 저장한 토큰을 지우면 됨.
- 장소/시설 드롭다운 목록은 프론트 상수로 관리하므로 조회 API 없음.

---

## 3. 인증

### 3-1. 회원가입 `POST /api/auth/signup`

학생 전용. `role`은 요청으로 받지 않고 서버가 항상 `STUDENT`로 저장.

**Request Body**

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| email | string | O | 학교 이메일 (`@ps.hs.kr`만 허용) |
| password | string | O | 비밀번호 (서버에서 해시 후 저장) |
| name | string | O | 이름 |

**Response 201**

```json
{ "userId": 12 }
```

**Errors**: 400 (이메일 도메인 불일치, 필수값 누락), 409 (이미 가입된 이메일)

### 3-2. 로그인 `POST /api/auth/login`

학생/관리자 공통. 로그인 후 화면 분기는 응답의 `role`로 프론트에서 처리.

**Request Body**

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| email | string | O | 학교 이메일 (로그인 ID) |
| password | string | O | 비밀번호 |

**Response 200**

```json
{
  "accessToken": "eyJhbGciOi...",
  "user": { "userId": 12, "name": "홍길동", "role": "STUDENT" }
}
```

**Errors**: 400 (필수값 누락), 401 (이메일 또는 비밀번호 불일치)

---

## 4. 신고 (학생)

### 4-1. 신고 작성 `POST /api/reports`

권한: 학생. `Content-Type: multipart/form-data`

**Request (form fields)**

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| location | string | O | 장소 |
| facility | string | O | 시설 |
| content | string | O | 신고 내용 |
| images | file[] | X | 사진 (여러 장 가능) |

서버 처리: `user_id`는 토큰에서 꺼냄, `status`는 `처리대기`, `created_at`은 서버 시각으로 자동 저장, 사진은 저장 후 `ReportImage`에 경로 기록.

**Response 201**

```json
{
  "reportId": 24,
  "status": "처리대기",
  "createdAt": "2026-09-25T14:30:00"
}
```

**Errors**: 400 (필수값 누락, 사진 형식/용량 오류), 401, 403 (관리자 계정)

### 4-2. 내 신고 목록 `GET /api/reports/me`

권한: 학생. 본인이 작성한 신고만, 신고일 최신순.

**Response 200**

```json
[
  {
    "reportId": 24,
    "location": "2학년 3반",
    "facility": "냉난방",
    "status": "처리대기",
    "createdAt": "2026-09-25T14:30:00"
  }
]
```

### 4-3. 신고 상세 `GET /api/reports/{reportId}`

권한: 학생(본인 신고만), 관리자(전체). 하나의 API에서 권한에 따라 응답이 다름.

**Response 200 (학생)**

```json
{
  "reportId": 24,
  "location": "2학년 3반",
  "facility": "냉난방",
  "content": "에어컨이 켜지지 않습니다.",
  "images": ["/uploads/reports/24_1.jpg"],
  "status": "처리대기",
  "createdAt": "2026-09-25T14:30:00"
}
```

**Response 200 (관리자)**: 위 내용에 신고자 정보 `reporter` 추가

```json
{
  "reportId": 24,
  "location": "2학년 3반",
  "facility": "냉난방",
  "content": "에어컨이 켜지지 않습니다.",
  "images": ["/uploads/reports/24_1.jpg"],
  "status": "처리대기",
  "createdAt": "2026-09-25T14:30:00",
  "reporter": { "name": "홍길동", "email": "hong@ps.hs.kr" }
}
```

**Errors**: 401, 403 (학생이 타인의 신고 조회), 404 (없는 신고)

---

## 5. 신고 관리 (관리자)

### 5-1. 신고 건수 통계 `GET /api/admin/reports/summary`

**Response 200**

```json
{ "total": 42, "처리대기": 8, "처리중": 5, "처리완료": 29 }
```

### 5-2. 전체 신고 목록 `GET /api/admin/reports`

모든 학생의 신고를 최신순으로 반환.

**Query**

| 파라미터 | 필수 | 설명 |
|---|---|---|
| status | X | `처리대기` / `처리중` / `처리완료`. 없으면 전체 |

**Response 200**: 4-2와 같은 형태에 신고자 이름 추가

```json
[
  {
    "reportId": 24,
    "location": "2학년 3반",
    "facility": "냉난방",
    "status": "처리대기",
    "createdAt": "2026-09-25T14:30:00",
    "reporterName": "홍길동"
  }
]
```

### 5-3. 신고 상태 변경 `PATCH /api/admin/reports/{reportId}/status`

**Request Body**

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| status | string | O | `처리중` 또는 `처리완료` |

**Response 200**

```json
{ "reportId": 24, "status": "처리중", "updatedAt": "2026-09-25T15:00:00" }
```

**Errors**: 400 (허용되지 않는 상태값), 403, 404

---

## 6. 권한 정리

| API | 비로그인 | 학생 | 관리자 |
|---|---|---|---|
| 회원가입, 로그인 | O | - | - |
| 신고 작성 | X | O | X |
| 내 신고 목록 | X | O | X |
| 신고 상세 | X | 본인 것만 | 전체 |
| 관리자 API (통계, 전체 목록, 상태 변경) | X | X | O |

## 7. 확인이 필요한 항목

| # | 내용 | 임시 기본값 |
|---|---|---|
| 1 | 상태 전환 규칙: 처리대기에서 바로 처리완료로 가도 되는지, 처리완료를 되돌릴 수 있는지 | 처리중/처리완료 어느 값으로든 변경 가능, 처리대기로는 되돌리기 불가 |
| 2 | 사진 제한: 최대 장수, 파일 용량, 허용 형식 | 최대 5장, 장당 5MB, jpg/png |
| 3 | 목록 페이지네이션: 신고가 많아질 때 나눠서 불러올지 | 처음에는 전체 반환 (필요 시 `page`, `size` 추가) |
| 4 | 토큰 만료 시간, 재발급(refresh) 사용 여부 | 만료 시 재로그인, refresh 없음 |
| 5 | 비밀번호 규칙 | 8자 이상 |
