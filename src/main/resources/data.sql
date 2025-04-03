-- 외래키 제약 끄기
SET FOREIGN_KEY_CHECKS = 0;

-- 모든 관련 테이블 삭제ct_users
DROP TABLE IF EXISTS ct_users;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS comment;

-- 외래키 제약 다시 켜기
SET FOREIGN_KEY_CHECKS = 1;

update animalskin.ct_users
set role = 'ADMIN'
where username = '김혜원'