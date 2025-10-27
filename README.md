# admin-project (포트폴리오 프로젝트)

> Vue.js와 Spring Boot를 이용한 어드민(Admin) 웹 서비스 프로젝트  
> 포트폴리오용으로 제작된 예제 프로젝트입니다.

## 🔹 프로젝트 소개
이 프로젝트는 어드민 웹 서비스를 구현한 포트폴리오용 프로젝트입니다.  
현재 임시 데이터로 **이력서 정보를 관리/조회**할 수 있도록 구성되어 있으며, 별도의 DB 설치 없이 바로 실행 및 확인할 수 있도록 **H2 데이터베이스**를 사용했습니다.

## 🔹 주요 기능
- 이력서 정보 조회 및 관리 (임시 데이터)
- 사용자 인터페이스: Vue.js 기반 프론트엔드
- 서버: Spring Boot 기반 REST API
- 데이터베이스: H2 (인메모리 DB, 별도 설치 불필요)
- 즉시 실행 가능: 프로젝트를 클론 후 바로 확인 가능

## 🔹 기술 스택
| Frontend | Backend | Database | Build & Tooling |
|----------|---------|---------|----------------|
| Vue.js | Spring Boot (멀티모듈) | H2 Database | Gradle, Node.js |

---

## 🔹 설치 및 실행 방법
```bash
# 백엔드 실행 (멀티모듈 Gradle 프로젝트)
cd backend
./gradlew bootRun

# 프론트엔드 실행
cd frontend
npm install
npm run serve
