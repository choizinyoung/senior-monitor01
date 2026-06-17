# Senior Monitor Server

독거노인 모니터링 앱을 위한 신호 수신 서버 + 대시보드입니다.
안드로이드 앱에서 화면 켜짐 신호를 받아 저장하고, 웹 대시보드로 실시간 확인할 수 있습니다.

## 사용 기술 / 프로그램

| 구분 | 이름 | 용도 |
|---|---|---|
| 코드 편집기 | VSCode | 서버 코드 작성 |
| 런타임 | Node.js | 서버 실행 환경 (JavaScript를 서버에서 실행) |
| 프레임워크 | Express | Node.js용 웹 서버 프레임워크 (API 라우팅 처리) |
| 버전관리 | Git | 코드 변경 이력 관리, 공동 작업 시 변경사항 동기화 |
| 코드 저장소 | GitHub | 코드 원격 저장 및 Railway 연동용 |
| 배포 플랫폼 | Railway | 서버를 인터넷에 올려서 외부에서 접속 가능하게 함 |

## 폴더 구조

```
senior-monitor/                 (VSCode에서 이 폴더를 열고 작업)
├── dashboard/
│   └── index.html             # 대시보드 화면 (브라우저로 보는 웹페이지)
└── server/
    ├── server.js              # 서버 메인 코드
    ├── package.json           # 설치한 라이브러리 목록
    ├── package-lock.json      # 설치 버전 고정 파일
    ├── node_modules/          # 설치된 라이브러리 실제 파일 (Git에는 안 올라감, npm install로 생성)
    └── Procfile                # Railway가 서버를 어떤 명령어로 실행할지 알려주는 파일
```

## 개발 환경 세팅 (이 레포로 작업을 시작할 때)

### 1. 필수 프로그램 설치

**Node.js 설치**
https://nodejs.org 접속 → `LTS` 버전 다운로드 → 설치 파일 실행 → `Next` 계속 눌러서 설치

**VSCode 설치**
https://code.visualstudio.com 접속 → 다운로드 → 설치

**Git 설치**
https://git-scm.com 접속 → 다운로드 → 설치 (Windows는 옵션 기본값 그대로 `Next`)

**설치 확인**

VSCode를 열고 상단 메뉴 `Terminal` → `New Terminal` 클릭해서 터미널 창을 띄운 후 아래 명령어 입력:

```bash
node -v
npm -v
git --version
```

각각 버전 번호가 출력되면 설치 성공입니다.

### 2. 레포 클론

작업할 폴더(예: 바탕화면)로 이동한 뒤 클론합니다.

```bash
cd Desktop
git clone https://github.com/[깃허브아이디]/senior-monitor.git
cd senior-monitor
```

클론이 끝나면 아래와 같은 구조가 그대로 받아져 있어야 합니다.

```
senior-monitor/
├── dashboard/
│   └── index.html
└── server/
    ├── server.js
    ├── package.json
    ├── package-lock.json
    └── Procfile
```

VSCode 상단 메뉴 `File` → `Open Folder` → 클론한 `senior-monitor` 폴더 선택해서 엽니다.

### 3. 서버 라이브러리 설치

`node_modules` 폴더는 Git에 올라가지 않으므로(용량이 커서 보통 제외함) 클론 직후엔 없습니다. `package.json`에 적힌 목록 그대로 설치해줍니다.

```bash
cd server
npm install
```

`package.json`에 정의된 `express` 등의 라이브러리가 `node_modules`에 설치됩니다. 이 폴더는 직접 수정하지 않습니다.

### 4. 환경 확인

`server.js`가 잘 받아졌는지, 어떤 API들이 있는지는 아래 "API 엔드포인트" 섹션을 참고합니다. 코드를 수정할 일이 있다면 VSCode에서 `server/server.js`를 열어서 작업합니다.

## 로컬 실행 (테스트용)

### 서버 실행

```bash
cd server
node server.js
```

성공 시 출력:
```
서버 실행 중! 포트: 3000
```

이 터미널 창은 서버가 켜져 있는 동안 계속 열려 있어야 합니다. `Ctrl+C`를 누르거나 터미널을 닫으면 서버가 꺼집니다.

### 동작 확인

브라우저에서 접속:
```
http://localhost:3000/signals
```
빈 배열 `[]`이 보이면 정상입니다.

대시보드 확인:
```
http://localhost:3000
```

### 같은 와이파이의 폰에서 테스트하려면

1. 컴퓨터 IP 확인
   ```bash
   # Windows
   ipconfig

   # Mac
   ifconfig | grep "inet "
   ```
   `192.168.x.x` 형태의 IPv4 주소를 확인합니다.

2. 안드로이드 앱의 `baseUrl`을 `http://[컴퓨터 IP]:3000`으로 설정

3. 폰과 컴퓨터가 동일한 Wi-Fi에 연결되어 있어야 합니다.

### 코드 수정 후 다시 테스트하려면

`server.js`를 수정하고 저장한 뒤에는 서버를 재시작해야 변경 사항이 반영됩니다.

```bash
# 기존 서버 터미널에서 Ctrl+C로 종료 후
node server.js
```

## 코드 변경 후 공동 작업 반영 (Git)

### 작업 시작 전 최신 코드 받기

다른 사람이 작업한 내용을 먼저 받습니다.

```bash
git pull
```

### 수정 후 올리기

```bash
git add .
git commit -m "수정 내용 설명"
git push
```

`push`하면 Railway가 자동으로 감지해서 재배포합니다 (아래 Railway 섹션 참고).

### 충돌(conflict)이 발생했을 때

같은 파일을 동시에 수정하면 `git pull` 시 충돌이 날 수 있습니다. VSCode가 충돌 부분을 색으로 표시해주니, 어떤 내용을 남길지 선택하고 다시 `git add . / git commit / git push` 하면 됩니다.

## API 엔드포인트

| Method | URL | 설명 |
|---|---|---|
| POST | `/register` | 사용자(어르신) 정보 최초 등록 (deviceId, name, birthdate) |
| POST | `/signal` | 화면 켜짐 신호 수신 (deviceId, deviceModel) |
| GET | `/signals` | 최근 신호 목록 조회 (최대 200개) |
| GET | `/users` | 등록된 사용자 목록 조회 |

## Railway 배포 방법

### 1. 사전 준비

- GitHub 계정 (https://github.com)
- Railway 계정 (https://railway.app, GitHub 계정으로 로그인 가능)

### 2. GitHub에 코드 업로드

```bash
cd senior-monitor   # 루트 폴더에서 실행
git init
git add .
git commit -m "첫 커밋"
git remote add origin https://github.com/[내아이디]/senior-monitor.git
git branch -M main
git push -u origin main
```

이후 코드를 수정할 때마다:
```bash
git add .
git commit -m "수정 내용 설명"
git push
```

### 3. Railway 프로젝트 생성

1. https://railway.app 접속 → `New Project`
2. `Deploy from GitHub repo` 선택
3. (레포가 안 보이면) `Configure GitHub App` 클릭 → 해당 레포 접근 허용 → `Save`
4. `senior-monitor` 레포 선택 → 배포 시작됨 (이 시점엔 실패해도 정상)

### 4. Root Directory 설정 (필수)

서버 코드가 `server` 폴더 안에 있으므로 Railway에게 위치를 알려줘야 합니다.

1. 배포된 서비스 클릭 → 상단 `Settings` 탭
2. `Source` 섹션 → `Root Directory`에 `/server` 입력
3. 자동으로 재배포 시작됨

### 5. 포트(PORT) 환경변수 확인

`server.js`는 아래처럼 Railway가 지정하는 포트를 사용하도록 되어 있습니다.

```js
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`서버 실행 중! 포트: ${PORT}`);
});
```

만약 502 Bad Gateway 에러가 발생하면:
1. `Variables` 탭 클릭
2. `PORT` 변수가 없으면 직접 추가
   ```
   NAME: PORT
   VALUE: (Railway 로그에 표시된 포트 번호, 예: 8080)
   ```

### 6. 도메인(URL) 생성

1. `Settings` 탭 → `Networking` 섹션
2. `Public Networking` → `Generate Domain` 클릭
3. 생성된 URL 확인 (예: `https://senior-monitor01-production.up.railway.app`)

### 7. 배포 확인

브라우저에서 접속:
```
https://[생성된 URL]/signals
```
`[]` 가 보이면 서버 배포 성공.

```
https://[생성된 URL]
```
대시보드 화면이 보이면 정상 (만약 안 보이면 아래 "트러블슈팅" 참고).

### 8. 안드로이드 앱 서버 URL 교체

`MonitorService.kt`에서:
```kotlin
private val baseUrl = "https://[생성된 URL]"
```

수정 후 APK 다시 빌드해서 설치.

## 자동 배포

GitHub `main` 브랜치에 `git push` 하면 Railway가 자동으로 감지해서 재배포합니다.
별도로 배포 버튼을 누를 필요는 없습니다 (`Settings` → `Source`에서 `Auto deploys when pushed to GitHub` 옵션이 켜져 있는지 확인).

## 트러블슈팅

| 증상 | 원인 | 해결 |
|---|---|---|
| `Railpack could not determine how to build` | Root Directory 미설정 | `Settings` → `Root Directory`에 `/server` 입력 |
| `502 Bad Gateway` | 포트 미일치 | `Variables`에 `PORT` 환경변수 추가 |
| 대시보드 화면이 안 뜸 | `dashboard` 폴더 경로 문제 | `server.js`의 정적 파일 경로 확인, 최신 코드가 `git push` 되었는지 확인 |
| 신호가 서버에 안 들어옴 | 앱의 `baseUrl` 오타 또는 미배포 상태 URL 사용 | Railway 도메인 재확인, Logcat으로 에러 확인 |
| `CLEARTEXT not permitted` (안드로이드) | http 평문 통신 차단 | Manifest에 `android:usesCleartextTraffic="true"` 추가, 또는 https URL(Railway) 사용 시 자동 해결 |

## 참고

- Railway 무료 플랜은 월 $5 크레딧 제공 (테스트 용도로 충분)
- 서버 코드는 현재 메모리(`signals`, `users` 배열)에 저장하는 방식이라 재배포 시 데이터가 초기화됩니다. 영구 저장이 필요하면 DB 연동이 필요합니다.