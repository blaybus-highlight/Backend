# 🚀 Highlight Backend

> **블레이버스 해커톤 2025** - Highlight 팀의 백엔드 API 서버

## 📋 프로젝트 개요

Highlight 팀의 MVP 백엔드 서비스입니다. Spring Boot 기반의 RESTful API를 제공하며, JWT 인증 시스템과 MySQL 데이터베이스를 사용합니다.

## 🛠️ 기술 스택

- **Backend**: Spring Boot 3.4.5, Java 17
- **Database**: MySQL 8.0
- **Authentication**: JWT (Access Token + Refresh Token)
- **Documentation**: Swagger/OpenAPI 3.0
- **Build Tool**: Gradle 8.4
- **Container**: Docker & Docker Compose

## 🚀 빠른 시작

### 1. 프로젝트 클론
```bash
git clone https://github.com/your-org/Highlight_Backend.git
cd Highlight_Backend
```

### 2. 환경설정
```bash
# 환경변수 파일 생성
cp .env.example .env
# 필요시 .env 파일 수정
```

### 3. Docker로 실행 (권장)
```bash
# MySQL 데이터베이스 시작
docker-compose up -d highlight-db

# 애플리케이션 실행
./gradlew bootRun
```

### 4. 로컬에서 바로 실행
```bash
# 빌드 및 실행
./gradlew bootRun
```

## 📖 API 문서

애플리케이션 실행 후 아래 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8085/api-docs](http://localhost:8085/api-docs)

## 🔐 인증 시스템

### JWT 토큰 기반 인증
- **Access Token**: 30분 (API 요청용)
- **Refresh Token**: 7일 (토큰 갱신용)

### 사용 방법
1. `/api/auth/login`으로 로그인
2. 응답으로 받은 JWT 토큰 복사
3. API 요청시 `Authorization: Bearer {토큰}` 헤더 추가

## 🏗️ 프로젝트 구조

```
src/main/java/com/highlight/highlight_backend/
├── config/          # 설정 클래스 (Security, CORS, Swagger)
├── controller/      # REST API 컨트롤러
├── service/         # 비즈니스 로직
├── repository/      # 데이터 액세스 레이어
├── domain/          # JPA 엔티티
├── dto/             # 데이터 전송 객체
├── exception/       # 전역 예외 처리
├── security/        # JWT 인증 필터
└── util/            # 유틸리티 클래스
```

## 🔧 개발 가이드

### 코드 스타일
- **주석**: 모든 클래스와 메서드에 JavaDoc 주석 작성
- **네이밍**: camelCase 사용, 의미있는 변수명 사용
- **예외처리**: 비즈니스 예외는 `BusinessException` 사용

### API 개발 순서
1. Entity 생성 (`domain` 패키지)
2. Repository 인터페이스 작성 (`repository` 패키지)
3. DTO 클래스 작성 (`dto` 패키지)
4. Service 로직 구현 (`service` 패키지)
5. Controller API 작성 (`controller` 패키지)
6. Swagger 문서 확인

### 테스트
```bash
# 전체 테스트 실행
./gradlew test

# 빌드 (테스트 포함)
./gradlew build
```

## 🐳 Docker 배포

```bash
# 전체 스택 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f highlight-backend

# 종료
docker-compose down
```

## 👥 팀원

- **전우선** - 백엔드 개발
- **탁찬홍** - 백엔드 개발

## 📞 문의

- **이메일**: wooxexn@gmail.com
- **GitHub**: [https://github.com/blaybus-highlight](https://github.com/blaybus-highlight)

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

---

**🏆 블레이버스 해커톤 2025 - Highlight 팀**