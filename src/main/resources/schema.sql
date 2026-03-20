-- 1. 群聊配置表
CREATE TABLE IF NOT EXISTS `group` (
    group_id BIGINT PRIMARY KEY,
    group_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 工具定义表
CREATE TABLE IF NOT EXISTS tools (
    tool_code VARCHAR(100) PRIMARY KEY, -- 对应代码里的 getName()
    tool_name VARCHAR(100) NOT NULL,
    description TEXT
);

-- 3. 群组-工具关联表
CREATE TABLE IF NOT EXISTS group_tool_mapping (
    id SERIAL PRIMARY KEY,
    group_id BIGINT REFERENCES `group`(group_id),
    tool_code VARCHAR(100) REFERENCES tools(tool_code),
    is_enabled BOOLEAN DEFAULT TRUE,
    UNIQUE(group_id, tool_code)
);