ALTER TABLE user_session
ADD COLUMN validity tinyint DEFAULT 1 COMMENT '有效性（0=无效，1=有效）';