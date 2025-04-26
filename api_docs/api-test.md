# Tutoring API 테스트 가이드

## 1. 환경 설정
- 서버 URL: `http://localhost:8080`
- 모든 API는 JSON 형식으로 요청/응답을 처리합니다.

## 2. API 테스트 순서

1. **튜터 회원가입**
```bash
curl -X POST http://localhost:8080/api/users/tutors/signup \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "tutor1",
    "password": "password123",
    "name": "김튜터"
  }'
```
2. **학생 회원가입**
```bash
curl -X POST http://localhost:8080/api/users/students/signup \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "student1",
    "password": "password123",
    "name": "이학생"
  }'
```
3. **로그인**
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'userId=tutor1&password=password123'
```
4. **로그아웃**
```bash
curl -X POST http://localhost:8080/api/users/logout
```
5. **튜터 수업 가능한 시간대 등록**
```bash
curl -X POST http://localhost:8080/api/availabilities/tutors \
  -H "Content-Type: application/json" \
  -d '{
    "startTime": "2025-05-20T10:00:00",
    "endTime": "2025-05-20T18:00:00"
  }'
```
6. **튜터 수업 가능한 시간대 삭제**
```bash
curl -X DELETE http://localhost:8080/api/availabilities/tutors \
  -H "Content-Type: application/json" \
  -d '{
    "startTime": "2025-05-20T11:30:00",
    "endTime": "2025-05-20T13:00:00"
  }'
```
7. **학생 수업 가능한 시간대 조회**
```bash
curl -X GET "http://localhost:8080/api/availabilities/search?startDate=2025-05-20T11:00:00&endDate=2025-05-20T14:00:00&durationMinutes=30"
```
8. **학생 수업 가능한 튜터 조회**
```bash
curl -X GET "http://localhost:8080/api/availabilities/tutors/search?startTime=2025-05-20T11:00:00&endTime=2025-05-20T12:00:00&durationMinutes=30"

```
9. **학생 30분 수업 신청**
```bash
curl -X POST http://localhost:8080/api/lessons \
  -H "Content-Type: application/json" \
  -d '{
    "tutorName": "김튜터",
    "startTime": "2025-05-20T11:00:00",
    "endTime": "2025-05-20T11:30:00",
    "type": "THIRTY_MINUTES"
  }'
```
10. **학생 60분 수업 신청**
```bash
curl -X POST http://localhost:8080/api/lessons \
  -H "Content-Type: application/json" \
  -d '{
    "tutorName": "김튜터",
    "startTime": "2025-05-20T14:30:00",
    "endTime": "2025-05-20T15:30:00",
    "type": "SIXTY_MINUTES"
  }'
```
11. **학생 신청한 수업 목록 조회 API**
```bash
curl -X GET http://localhost:8080/api/lessons/students \
  -H "Content-Type: application/json"
```

## 3. 테스트 시나리오
1. 튜터와 학생 회원가입
2. 튜터 로그인
3. 수업 가능한 시간대 생성
4. 로그아웃
5. 학생 로그인
6. 수업 가능한 시간대 조회
7. 수업 가능한 튜터 조회
8. 30분 수업 신청
9. 60분 수업 신청
10. 신청한 수업 목록 조회
11. 로그아웃
12. 튜터 로그인
13. 수업 가능한 시간대 삭제