-- 添加 is_deleted 字段到 user_profile 表
-- 用于实现软删除功能

ALTER TABLE user_profile ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE AFTER profile_value;

-- 为 is_deleted 字段添加索引，提高查询性能
CREATE INDEX idx_user_profile_is_deleted ON user_profile(user_id, is_deleted);
