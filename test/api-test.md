# Tutoring API 테스트 가이드

## 1. 환경 설정
- 서버 URL: `http://localhost:8080`
- 모든 API는 JSON 형식으로 요청/응답을 처리합니다.

## 2. API 테스트 순서

### 2.1 사용자 등록
1. 튜터 회원가입
```bash
curl -X POST http://localhost:8080/api/users/tutors/signup \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "tutor1",
    "password": "password123",
    "name": "김튜터"
  }'
```

2. 학생 회원가입
```bash
curl -X POST http://localhost:8080/api/users/students/signup \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "student1",
    "password": "password123",
    "name": "이학생"
  }'
```

3. 로그인
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "tutor1",
    "password": "password123"
  }'
```

### 2.2 튜터 가용성 설정
1. 튜터 가용성 생성
```bash
curl -X POST http://localhost:8080/api/v1/availabilities/tutors/1 \
  -H "Content-Type: application/json" \
  -d '{
    "startTime": "2024-03-20T10:00:00",
    "endTime": "2024-03-20T12:00:00"
  }'
```

### 2.3 가용 시간대 검색
1. 특정 기간 내 가용 시간대 검색
```bash
curl -X GET "http://localhost:8080/api/v1/availabilities/search?startDate=2024-03-20T00:00:00&endDate=2024-03-21T00:00:00&durationMinutes=30"
```

2. 특정 시간대에 가용한 튜터 검색
```bash
curl -X GET "http://localhost:8080/api/v1/availabilities/tutors/search?startTime=2024-03-20T10:00:00&endTime=2024-03-20T11:00:00&durationMinutes=30"
```

### 2.4 수업 예약
1. 30분 수업 예약
```bash
curl -X POST http://localhost:8080/api/v1/lessons \
  -H "Content-Type: application/json" \
  -d '{
    "tutorId": 1,
    "studentId": 1,
    "startTime": "2024-03-20T10:00:00",
    "type": "THIRTY_MINUTES"
  }'
```

2. 60분 수업 예약
```bash
curl -X POST http://localhost:8080/api/v1/lessons \
  -H "Content-Type: application/json" \
  -d '{
    "tutorId": 1,
    "studentId": 1,
    "startTime": "2024-03-20T11:00:00",
    "type": "SIXTY_MINUTES"
  }'
```

### 2.5 수업 취소
```bash
curl -X DELETE http://localhost:8080/api/v1/lessons/1
```

## 3. 테스트 시나리오

### 시나리오 1: 기본 수업 예약
1. 튜터와 학생을 등록합니다.
2. 튜터의 가용 시간을 설정합니다.
3. 가용 시간대를 검색하여 확인합니다.
4. 30분 수업을 예약합니다.
5. 예약된 수업을 확인합니다.

### 시나리오 2: 연속 수업 예약
1. 튜터의 가용 시간을 2시간으로 설정합니다.
2. 60분 수업을 예약합니다.
3. 같은 시간대에 다른 수업을 예약하려고 시도합니다 (실패해야 함).
4. 다른 시간대에 30분 수업을 예약합니다.

### 시나리오 3: 수업 취소
1. 수업을 예약합니다.
2. 수업을 취소합니다.
3. 취소된 시간대가 다시 가용 상태로 변경되었는지 확인합니다.

## 4. 주의사항
- 모든 시간은 ISO 8601 형식(YYYY-MM-DDThh:mm:ss)을 사용합니다.
- 수업 예약 시 시작 시간은 30분 단위로만 가능합니다.
- 60분 수업은 연속된 두 개의 30분 슬롯이 필요합니다.
- 이미 예약된 시간대에는 새로운 예약이 불가능합니다. 