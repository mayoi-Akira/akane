-- 1. 群聊配置表
CREATE TABLE IF NOT EXISTS chat_group (
    group_id VARCHAR(255) PRIMARY KEY,
    group_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 工具定义表
CREATE TABLE IF NOT EXISTS tool (
    tool_code VARCHAR(100) PRIMARY KEY,
    tool_name VARCHAR(100) NOT NULL,
    description TEXT,
    tool_default_type ENUM('ENABLE', 'DISABLE', 'FORCE', 'DEVELOPING', 'DEPRECATED') DEFAULT 'ENABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 群组-工具关联表
CREATE TABLE IF NOT EXISTS group_tool_config (
    group_id VARCHAR(255) NOT NULL,
    tool_code VARCHAR(100) NOT NULL,
    status ENUM('ENABLE', 'DISABLE', 'FORCE', 'DEVELOPING', 'DEPRECATED') DEFAULT 'ENABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (group_id, tool_code),
    FOREIGN KEY (group_id) REFERENCES chat_group(group_id),
    FOREIGN KEY (tool_code) REFERENCES tool(tool_code)
);
