# 풍생고 교내 시설 고장 신고 시스템 - BE

Spring Boot + MySQL 백엔드 프로젝트입니다. 기획 문서는 [`docs/`](docs)에 있습니다.

## 1. 준비물

| 프로그램 | 버전 | 용도 |
|---|---|---|
| JDK | **17** | Java 코드 실행 |
| IntelliJ IDEA | Community 이상 | 코드 작성/실행 |
| MySQL + Workbench | 8.x | DB 서버와 DB 확인 도구 |

설치 후 `cmd`에서 `java -version`을 입력해 `17.x.x`가 나오면 됩니다.

## 2. 실행 순서

**1) 코드 받기**

```bash
git clone https://github.com/pshs1team/BE.git
```

**2) IntelliJ로 열기**

`Open`으로 `BE` 폴더를 선택하고, 우측 하단의 라이브러리 다운로드가 끝날 때까지 기다립니다.
Java 버전 오류가 나면 `File → Project Structure`의 SDK와 `Settings → Build Tools → Gradle`의 Gradle JVM을 17로 바꿉니다.

**3) DB 만들기**

MySQL Workbench에서 아래 쿼리를 실행합니다.

```sql
CREATE DATABASE pungsaeng DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

**4) 비밀 설정 파일 만들기**

`src/main/resources/application-secret.example.yaml`을 같은 폴더에 `application-secret.yaml`로 복사하고, 내 MySQL 비밀번호를 적습니다. 이 파일은 Git에 올라가지 않습니다.

**5) 서버 실행**

`BeApplication`의 `main`을 실행(▶)합니다. 콘솔에 `Started BeApplication`이 나오면 성공입니다.

## 3. 확인

- 브라우저에서 `http://localhost:8080/swagger-ui/index.html`을 열어 API 목록이 보이는지 확인합니다.
- `GET /api/ping`을 실행해 `pong`이 오는지 확인합니다.
- Workbench에서 `pungsaeng` DB에 `user`, `report`, `report_image` 테이블이 생겼는지 확인합니다.

## 4. 자주 나는 오류

| 증상 | 원인 |
|---|---|
| `Failed to determine a suitable driver class` | `application-secret.yaml`이 없음 |
| `Access denied for user` | secret 파일의 MySQL 비밀번호가 틀림 |
| `Unknown database 'pungsaeng'` | 3번의 DB 생성을 안 함 |
| `Communications link failure` | MySQL 서버가 꺼져 있음 |
| 한글이 `???`로 깨짐 | DB를 `utf8mb4`로 만들지 않음 |
