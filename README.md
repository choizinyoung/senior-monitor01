# Senior Monitor Server (Spring Boot)

독거노인 모니터링 앱을 위한 신호 수신 서버 + 대시보드입니다.
안드로이드 앱에서 화면 켜짐 신호를 받아 PostgreSQL에 저장하고, 웹 대시보드로 실시간 확인할 수 있습니다.

기존에는 Node.js + Express 기반이었으나, 정식 서비스 확장을 고려해 **Java + Spring Boot + PostgreSQL**로 전환했습니다.

## 사용 기술 / 프로그램

| 구분 | 이름 | 용도 |
|---|---|---|
| 코드 편집기 | VSCode | 서버 코드 작성 (Extension Pack for Java, Spring Boot Extension Pack 설치) |
| 언어 | Java 21 | 서버 로직 작성 언어 |
| 프레임워크 | Spring Boot 4.1.0 | REST API, DB 연동 등을 처리하는 웹 프레임워크 |
| 빌드 도구 | Gradle | 라이브러리 관리 + 빌드 실행 (Node.js의 npm 역할) |
| ORM | Spring Data JPA (Hibernate) | Java 객체 ↔ DB 테이블 자동 매핑 |
| 데이터베이스 | PostgreSQL | 회원(어르신), 신호 로그 등 영구 저장 |
| 버전관리 | Git | 코드 변경 이력 관리, 공동 작업 동기화 |
| 코드 저장소 | GitHub | 코드 원격 저장, Railway 연동용 |
| 배포 플랫폼 | Railway | 서버 + DB를 인터넷에 올려서 외부 접속 가능하게 함 |

## 폴더 구조

```
C:/senior-monitor/                                    (Git 레포 루트)
├── .gitignore                                        # 루트용 (.vscode/launch.json 등 제외)
├── README.md
├── dashboard/                                         # 참고용 프론트엔드 원본 (현재는 server/static으로 복사해서 사용)
│   ├── index.html
│   ├── monitoring.html
│   ├── alert-list.html
│   ├── history.html
│   ├── logs.html
│   ├── seniors.html
│   ├── settings.html
│   ├── common.css
│   └── common.js
├── .vscode/
│   ├── launch.json                                   # 실제 DB 접속 정보 포함, Git에 올라가지 않음
│   └── launch.json.example                            # 빈 양식, Git에 올라감
└── server/                                            # 백엔드 (Spring Boot / Gradle)
    ├── .gitignore                                     # build/ 등 빌드 산물 제외 (Spring Initializr 자동 생성)
    ├── build.gradle                                   # 라이브러리 목록 (npm의 package.json 역할)
    ├── settings.gradle
    ├── gradlew / gradlew.bat                          # Gradle 실행 스크립트
    ├── build/                                         # 빌드 결과물 (자동 생성, Git에 안 올라감)
    └── src/main/
        ├── java/com/seniormonitor/server/
        │   ├── ServerApplication.java                 # 앱 시작점, 여기서 Run 버튼 눌러서 서버 실행
        │   ├── entity/
        │   │   ├── Elder.java                         # 어르신(회원) 테이블 매핑
        │   │   └── SignalLog.java                     # 화면 켜짐 신호 테이블 매핑
        │   ├── repository/
        │   │   ├── ElderRepository.java                # Elder 테이블 조회/저장
        │   │   └── SignalLogRepository.java            # SignalLog 테이블 조회/저장
        │   ├── service/
        │   │   ├── ElderService.java                  # 회원 관련 비즈니스 로직
        │   │   └── SignalService.java                 # 신호 관련 비즈니스 로직
        │   ├── dto/
        │   │   ├── RegisterRequest.java                # 앱이 보내는 등록 요청 데이터 형태
        │   │   └── SignalRequest.java                  # 앱이 보내는 신호 요청 데이터 형태
        │   └── controller/
        │       └── MonitorController.java              # REST API 엔드포인트 (/register, /signal, /signals, /users)
        └── resources/
            ├── application.properties                  # 포트, DB 접속 설정 (환경변수 참조 방식)
            └── static/                                  # 대시보드 HTML/CSS/JS (Spring Boot가 자동으로 서빙)
                ├── index.html
                ├── monitoring.html
                ├── ...
                ├── common.css
                └── common.js
```

## 개발 환경 세팅 (이 레포로 작업을 처음 시작할 때)

### 1. 필수 프로그램 설치

**JDK 21 설치**

https://adoptium.net 접속 → `21 - LTS` 선택 → OS에 맞는 설치 파일 다운로드 → 설치

설치 확인 (VSCode 터미널에서):
```bash
java -version
```
`openjdk version "21.0.x"` 형태로 출력되면 성공.

**VSCode 설치**

https://code.visualstudio.com 접속 → 다운로드 → 설치

**VSCode 확장 설치**

VSCode 왼쪽 사이드바 확장(블록 아이콘) 클릭 → 아래 두 개 검색해서 설치:
```
Extension Pack for Java
Spring Boot Extension Pack
```

**Git 설치**

https://git-scm.com 접속 → 다운로드 → 설치

설치 확인:
```bash
git --version
```

### 2. 레포 클론

```bash
cd Desktop
git clone https://github.com/[깃허브아이디]/senior-monitor.git
cd senior-monitor
```

### 3. VSCode로 server 폴더 열기

VSCode 상단 메뉴 `File` → `Open Folder` → `C:/senior-monitor/server` 폴더 선택

처음 열면 오른쪽 아래에 `Importing Java project...` 또는 `Loading Gradle build...` 알림이 뜹니다. 이게 끝날 때까지 기다립니다 (인터넷에서 필요한 라이브러리를 받는 중이라 1~5분 정도 걸릴 수 있음).

**정상적으로 인식됐는지 확인하는 법**
- `src/main/java/com/seniormonitor/server/ServerApplication.java` 파일을 열었을 때 코드에 문법 강조(색깔)가 입혀져 있는지 확인
- VSCode 하단 상태 바에 `Java: Ready` 표시가 뜨는지 확인

### 4. .vscode/launch.json 생성 (DB 접속 정보, 각자 로컬에서 설정)

이 파일은 DB 비밀번호가 들어가기 때문에 Git에 올라가지 않습니다. 클론한 사람은 직접 만들어야 합니다.

`senior-monitor/.vscode/launch.json.example` 파일을 복사해서 같은 위치에 `launch.json`으로 저장합니다.

```bash
cd .vscode
cp launch.json.example launch.json
```

(Windows PowerShell이라면 `copy launch.json.example launch.json`)

`launch.json` 파일을 열어서 아래 형태로 채웁니다. 실제 값은 "Railway DB 접속 정보 확인하는 법" 섹션을 참고합니다.

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot-ServerApplication",
      "request": "launch",
      "mainClass": "com.seniormonitor.server.ServerApplication",
      "projectName": "server",
      "env": {
        "DB_URL": "jdbc:postgresql://[호스트]:[포트]/railway",
        "DB_USERNAME": "postgres",
        "DB_PASSWORD": "[비밀번호]",
        "PORT": "8080"
      }
    }
  ]
}
```

## Railway에서 DB 접속 정보 확인하는 법

1. https://railway.app 접속 → 로그인 → 해당 프로젝트(`melodious-adaptation` 등) 클릭
2. PostgreSQL 서비스 박스(보통 코끼리 아이콘) 클릭
3. 상단 탭 중 `Variables` 클릭
4. 아래 항목들이 보입니다.
   ```
   PGHOST            ← Railway 내부용 (로컬에서는 사용 불가)
   PGPORT
   PGUSER
   PGPASSWORD
   PGDATABASE
   DATABASE_URL          ← 내부용
   DATABASE_PUBLIC_URL   ← 외부(로컬)에서 접속 가능한 주소, 이걸 사용
   ```
5. `DATABASE_PUBLIC_URL` 값을 확인합니다. 형태는 이렇습니다.
   ```
   postgresql://postgres:비밀번호@호스트.proxy.rlwy.net:포트번호/railway
   ```
6. 이 값을 분리해서 `launch.json`에 넣습니다.

   | URL 안의 값 | launch.json 항목 |
   |---|---|
   | `호스트.proxy.rlwy.net` | `DB_URL`의 호스트 부분 |
   | `포트번호` | `DB_URL`의 포트 부분 |
   | `postgres` (URL의 `://` 뒤, `:` 앞) | `DB_USERNAME` |
   | `:` 와 `@` 사이 문자열 | `DB_PASSWORD` |

   최종 `DB_URL` 형태:
   ```
   jdbc:postgresql://호스트.proxy.rlwy.net:포트번호/railway
   ```
   (일반 `postgresql://`와 다르게 앞에 `jdbc:`를 붙여야 합니다.)

**주의**: `DATABASE_PUBLIC_URL`에는 비밀번호가 그대로 노출되어 있으므로, 캡처나 채팅으로 공유한 적이 있다면 Railway에서 비밀번호를 재발급(reset)하는 것을 권장합니다.

## 서버 실행 (로컬)

### 방법 1 — VSCode Run 버튼 (권장)

1. `src/main/java/com/seniormonitor/server/ServerApplication.java` 파일을 엽니다.
2. `public class ServerApplication` 위쪽, 또는 `main` 메서드 위쪽에 작게 표시되는 `Run | Debug` 버튼을 클릭합니다. (Spring Boot Extension이 표시해주는 버튼)
3. `Run`을 클릭하면 `launch.json`에 적어둔 환경변수(DB_URL 등)가 자동으로 적용되어 실행됩니다.

또는 VSCode 왼쪽 사이드바 `Run and Debug` (벌레+삼각형 아이콘) 클릭 → 상단 드롭다운에서 `Spring Boot-ServerApplication` 선택 → 초록색 ▶ 버튼 클릭.

### 방법 2 — 터미널에서 직접 실행

`server` 폴더 경로에서 실행해야 합니다.

```bash
cd server
```

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://호스트:포트/railway"; $env:DB_USERNAME="postgres"; $env:DB_PASSWORD="비밀번호"; .\gradlew.bat bootRun
```

**Mac/Linux:**
```bash
DB_URL="jdbc:postgresql://호스트:포트/railway" DB_USERNAME="postgres" DB_PASSWORD="비밀번호" ./gradlew bootRun
```

### 실행 성공 확인

콘솔/터미널에 아래와 같은 로그가 보이면 성공입니다.

```
Hibernate: create table elder (...)
Hibernate: create table signal_log (...)
Tomcat started on port 8080
Started ServerApplication in 3.xxx seconds
```

`Hibernate: create table ...` 로그는 JPA(`spring.jpa.hibernate.ddl-auto=update` 설정)가 Entity 클래스를 보고 PostgreSQL에 테이블을 자동 생성했다는 뜻입니다. SQL을 직접 작성할 필요가 없습니다.

### 동작 확인

브라우저에서 접속:
```
http://localhost:8080/signals
```
`[]` 빈 배열이 보이면 정상입니다.

```
http://localhost:8080
```
대시보드 화면이 보이면 정상입니다.

### 코드 수정 후 다시 테스트하려면

실행 중인 서버를 정지(빨간 정지 버튼, 또는 터미널에서 `Ctrl+C`)한 뒤 다시 `Run` 합니다. Spring Boot는 코드 변경 후 재시작이 필요합니다 (Node.js의 nodemon 같은 자동 재시작은 기본 설정에 없음).

## API 엔드포인트

| Method | URL | 설명 | 요청 Body |
|---|---|---|---|
| POST | `/register` | 어르신 정보 최초 등록 | `{ "deviceId": "...", "name": "...", "birthdate": "..." }` |
| POST | `/signal` | 화면 켜짐 신호 수신 | `{ "deviceId": "...", "deviceModel": "..." }` |
| GET | `/signals` | 최근 신호 목록 조회 (최대 200개, 최신순) | - |
| GET | `/users` | 등록된 어르신 목록 조회 | - |

## 코드 구조 (계층별 역할)

```
Controller → Service → Repository → Entity(DB 테이블)
```

| 계층 | 역할 | 예시 |
|---|---|---|
| Controller | HTTP 요청/응답만 처리, 실제 로직은 Service에 위임 | `MonitorController` |
| Service | 비즈니스 로직 (검증, 가공, 여러 Repository 조합) | `ElderService`, `SignalService` |
| Repository | DB 입출력만 담당, JPA가 기본 CRUD 자동 생성 | `ElderRepository`, `SignalLogRepository` |
| Entity | DB 테이블과 1:1 매핑되는 Java 클래스 | `Elder`, `SignalLog` |
| DTO | API 요청/응답 데이터의 형태 정의 | `RegisterRequest`, `SignalRequest` |

새 기능을 추가할 때는 보통 Entity(필요시) → Repository → Service → Controller 순서로 작성합니다.

## DB 직접 확인하는 법 (Railway)

### 방법 1 — Railway 웹 UI에서 확인

1. Railway 프로젝트 → PostgreSQL 서비스 클릭
2. 상단 탭 `Data` 클릭
3. 테이블 목록(`elder`, `signal_log` 등)이 보이고, 클릭하면 저장된 행(row)을 표 형태로 볼 수 있습니다.
4. 직접 SQL을 입력해서 조회하고 싶다면 같은 화면의 `Query` 탭에서 작성 가능합니다.
   ```sql
   SELECT * FROM elder;
   SELECT * FROM signal_log ORDER BY signal_time DESC LIMIT 20;
   ```

### 방법 2 — 외부 DB 클라이언트 사용 (DBeaver, TablePlus 등)

`launch.json`에 적어둔 것과 동일한 접속 정보(호스트, 포트, DB명 `railway`, 사용자명 `postgres`, 비밀번호)를 입력해서 연결하면 GUI로 테이블을 조회/수정할 수 있습니다.

## Railway 배포 (서버를 인터넷에 올리기)

### 1. GitHub에 코드 업로드

```bash
cd senior-monitor
git add .
git commit -m "Spring Boot 전환"
git push
```

### 2. Railway 서비스 설정

Node.js 때 만들어둔 서비스를 그대로 쓴다면 빌드 방식이 바뀌었으므로 설정을 다시 확인해야 합니다.

1. Railway 프로젝트 → 서버 서비스 클릭 → `Settings` 탭
2. `Source` 섹션 → `Root Directory`가 `/server`로 되어 있는지 확인
3. Gradle 프로젝트는 Railway가 자동으로 빌드 방식을 인식합니다 (Railpack이 `build.gradle`을 보고 Java 프로젝트로 판단).

### 3. 환경변수 설정

`Variables` 탭에서 아래 값들을 등록합니다. PostgreSQL 서비스가 같은 프로젝트 안에 있다면 Railway가 제공하는 참조 문법을 사용할 수 있습니다.

```
DB_URL=${{Postgres.DATABASE_URL}}
DB_USERNAME=${{Postgres.PGUSER}}
DB_PASSWORD=${{Postgres.PGPASSWORD}}
PORT=8080
```

(서비스 이름이 `Postgres`가 아니라면 실제 PostgreSQL 서비스명으로 바꿔야 합니다. Railway `Variables` 탭에서 `New Variable` 추가 시 `Reference`로 다른 서비스 변수를 선택할 수 있습니다.)

`DB_URL`은 `jdbc:`로 시작해야 하므로, `DATABASE_URL`을 그대로 참조하면 앞에 `jdbc:`가 빠져 있을 수 있습니다. 이 경우 `application.properties`의 `spring.datasource.url` 값을 직접 `jdbc:${DATABASE_URL}` 형태로 조합하거나, Railway Variables에서 수동으로 `jdbc:postgresql://...` 형태로 작성한 변수를 따로 만들어 사용합니다.

### 4. 배포 확인

`Deployments` 탭에서 빌드 로그를 확인합니다. Gradle 빌드 → jar 생성 → 실행까지 진행되며, Node.js 때보다 빌드 시간이 더 오래 걸립니다 (1~3분 정도).

```
BUILD SUCCESSFUL
Started ServerApplication
```

이후 `Settings` → `Networking` → `Public Networking`에서 생성해둔 도메인으로 접속해 확인합니다.

```
https://[기존에 생성한 도메인]/signals
```

## 트러블슈팅

| 증상 | 원인 | 해결 |
|---|---|---|
| VSCode에서 Java 프로젝트로 인식 안 됨 | 확장 미설치 또는 Gradle 동기화 안 됨 | `Extension Pack for Java` 설치 확인, 폴더를 다시 열거나 명령 팔레트에서 `Java: Clean Workspace` 실행 |
| `Run` 버튼이 안 보임 | Spring Boot Extension 미설치 | `Spring Boot Extension Pack` 설치 |
| DB 연결 실패 (`Connection refused` 등) | `launch.json`의 `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` 오타, 또는 `DATABASE_PUBLIC_URL`(외부용)이 아닌 내부용 주소 사용 | Railway `Variables` 탭에서 `DATABASE_PUBLIC_URL` 다시 확인 |
| `launch.json`이 Git에 올라가 있음 | `.gitignore`에 경로 미등록 | 루트 `.gitignore`에 `.vscode/launch.json` 추가, `git rm --cached .vscode/launch.json`으로 이미 올라간 것 제거 |
| 테이블이 안 생성됨 | `application.properties`의 `ddl-auto` 설정 누락 | `spring.jpa.hibernate.ddl-auto=update` 확인 |
| 대시보드 화면이 안 뜸 | HTML 파일이 `static` 폴더에 없음 | `dashboard` 폴더 내용을 `server/src/main/resources/static/`로 복사했는지 확인 |
| 포트 충돌 (`Port already in use`) | 8080 포트를 다른 프로그램이 사용 중 | 기존에 실행 중인 서버를 정지하거나 `PORT` 환경변수를 다른 값으로 변경 |

## 참고

- 비밀번호, DB 접속 정보는 절대 코드에 직접 작성하지 않고 `launch.json`(로컬) 또는 Railway `Variables`(배포)로만 관리합니다.
- `launch.json`을 실수로 커밋했다면 즉시 Railway에서 DB 비밀번호를 재발급하고, Git 히스토리에서도 제거해야 합니다.
- 기존 Node.js 서버 코드(`dashboard` 폴더 포함)는 참고용으로 남겨두었으며, 실제 서비스는 `server` 폴더의 Spring Boot 코드로 운영합니다.