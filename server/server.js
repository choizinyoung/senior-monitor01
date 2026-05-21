const express = require('express');
const path = require('path');
const app = express();

app.use(express.json());

// 대시보드 HTML 파일 서빙 (dashboard 폴더)
app.use(express.static(path.join(__dirname, '../dashboard')));

// CORS 허용 (앱에서 보내는 요청 받기 위해)
app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', 'Content-Type');
  next();
});

// 사용자 등록 정보 저장 (이름, 생일)
let users = {};

// 신호 저장
let signals = [];

// 앱 첫 실행 시 사용자 정보 등록
app.post('/register', (req, res) => {
  const { deviceId, name, birthdate } = req.body;
  if (!deviceId || !name) {
    return res.status(400).json({ error: '필수 정보 누락' });
  }
  users[deviceId] = { name, birthdate };
  console.log('사용자 등록:', users[deviceId]);
  res.json({ success: true });
});

// 화면 켜짐 신호 수신
app.post('/signal', (req, res) => {
  const { deviceId, deviceModel } = req.body;
  const user = users[deviceId] || { name: '알 수 없는 분', birthdate: '-' };

  const now = new Date();
  const signal = {
    id: Date.now(),
    deviceId,
    deviceModel: deviceModel || '알 수 없는 기기',
    name: user.name,
    birthdate: user.birthdate,
    screenOnTime: now.toLocaleString('ko-KR', { timeZone: 'Asia/Seoul' }),
    timestamp: now.getTime()
  };

  signals.push(signal);
  if (signals.length > 200) signals.shift(); // 최대 200개 유지

  console.log(`[신호] ${signal.name} | ${signal.screenOnTime}`);
  res.json({ success: true });
});

// 대시보드용 신호 목록 API
app.get('/signals', (req, res) => {
  res.json(signals.slice().reverse());
});

// 등록된 사용자 목록
app.get('/users', (req, res) => {
  res.json(users);
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`서버 실행 중! 포트: ${PORT}`);
});