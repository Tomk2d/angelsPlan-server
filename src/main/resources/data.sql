-- users 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
);

-- task 테이블 생성
CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN DEFAULT FALSE,
    created_at DATETIME,
    updated_at DATETIME
);

-- 샘플 데이터 추가
INSERT INTO tasks (title, description, completed, user_id, created_at, updated_at) VALUES
('프로젝트 기획서 작성', '2024년 1분기 프로젝트 기획서 초안 작성', false, 1, NOW(), NOW()),
('디자인 시안 검토', '메인 페이지 디자인 시안 3개 검토 및 피드백', true, 1, NOW(), NOW()),
('API 문서 작성', 'REST API 엔드포인트 문서화 작업', false, 2, NOW(), NOW()),
('테스트 코드 작성', '사용자 인증 모듈 단위 테스트 작성', false, 2, NOW(), NOW()),
('데이터베이스 스키마 설계', '회원 관리 시스템 DB 스키마 설계 및 검토', true, 3, NOW(), NOW()),
('버그 수정', '로그인 페이지 무한 로딩 이슈 해결', false, 3, NOW(), NOW()),
('성능 최적화', '이미지 로딩 속도 개선 작업', false, 1, NOW(), NOW()),
('코드 리뷰', '팀원들의 PR 검토 및 피드백 작성', true, 2, NOW(), NOW()),
('보안 점검', '취약점 분석 및 보안 이슈 점검', false, 3, NOW(), NOW()),
('배포 자동화', 'CI/CD 파이프라인 구축 작업', false, 1, NOW(), NOW());

-- 테스트 사용자 추가 (비밀번호: password123)
INSERT INTO users (email, password, nickname, created_at, updated_at) VALUES
('admin@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '관리자', NOW(), NOW()),
('user1@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '홍길동', NOW(), NOW()),
('user2@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '김철수', NOW(), NOW()),
('test@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '테스트', NOW(), NOW())
ON DUPLICATE KEY UPDATE password = VALUES(password);

-- 테스트 태스크 추가
INSERT INTO tasks (title, description, completed, user_id, created_at, updated_at) VALUES
('프로젝트 기획', '2024년 프로젝트 기획 및 일정 수립', false, 1, NOW(), NOW()),
('디자인 검토', '메인 페이지 디자인 검토', true, 1, NOW(), NOW()),
('API 개발', 'REST API 엔드포인트 구현', false, 2, NOW(), NOW()),
('테스트 코드 작성', '단위 테스트 및 통합 테스트 작성', false, 2, NOW(), NOW()),
('데이터베이스 설계', 'ERD 작성 및 테이블 설계', true, 3, NOW(), NOW()); 