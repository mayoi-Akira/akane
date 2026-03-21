-- 1. 群聊配置表
CREATE TABLE IF NOT EXISTS `group` (
    group_id VARCHAR(255) PRIMARY KEY,
    group_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 工具定义表
CREATE TABLE IF NOT EXISTS tools (
    tool_id INT AUTO_INCREMENT PRIMARY KEY,
    tool_code VARCHAR(100) NOT NULL UNIQUE, -- 对应代码里的 getName()
    tool_name VARCHAR(100) NOT NULL,
    description TEXT
);

-- 3. 群组-工具关联表
CREATE TABLE IF NOT EXISTS group_tool_mapping (
    id INT AUTO_INCREMENT PRIMARY KEY,
    group_id VARCHAR(255) REFERENCES `group`(group_id),
    tool_id INT REFERENCES tools(tool_id),
    is_enabled ENUM('ENABLE', 'DISABLE', 'FORCE', 'DEVELOPING', 'DEPRECATED') DEFAULT 'ENABLE',
    UNIQUE(group_id, tool_id)
);
