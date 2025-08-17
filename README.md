# Highlight Backend

블레이버스 해커톤 2025 - Highlight 팀의 실시간 경매 백엔드 API 서버

## 프로젝트 개요

실시간 경매 플랫폼의 백엔드 서비스입니다. 사용자는 경매에 참여하여 입찰하거나 즉시구매할 수 있으며, 관리자는 상품과 경매를 관리할 수 있습니다.

## 기술 스택

- **Backend**: Spring Boot 3.4.5, Java 17
- **Database**: MySQL 8.0 (AWS RDS)
- **Server**: AWS EC2 (프리티어, 스왑메모리 설정)
- **Authentication**: JWT (Access Token + Refresh Token)
- **Real-time**: WebSocket (STOMP)
- **Cloud Storage**: AWS S3
- **SMS**: CoolSMS API
- **Documentation**: Swagger/OpenAPI 3.0
- **Build Tool**: Gradle 8.4
- **Container**: Docker & Docker Compose

## 실행 방법

```bash
# 프로젝트 클론
git clone https://github.com/blaybus-highlight/Highlight_Backend.git
cd Highlight_Backend

# 환경변수 설정 (.env 파일 수정)
vi .env

# Docker로 실행
docker-compose up -d

# 또는 로컬 실행
./gradlew bootRun
```

## API 문서

- Swagger UI: http://ec2-43-201-71-156.ap-northeast-2.compute.amazonaws.com:8085/swagger-ui/index.html
- OpenAPI JSON: http://ec2-43-201-71-156.ap-northeast-2.compute.amazonaws.com:8085/api-docs

## 주요 기능

- 사용자 회원가입/로그인 (휴대폰 인증)
- 실시간 경매 입찰 시스템
- 즉시구매 기능
- 상품 찜하기 및 알림 설정
- 관리자 상품/경매 관리
- AWS S3 이미지 업로드

## 팀원

- 전우선 - 백엔드 개발
- 탁찬홍 - 백엔드 개발