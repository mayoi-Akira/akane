package com.bot.akane.agent.toolsService;

public interface GithubToolsService {
    
    /**
     * 获取某用户的GitHub仓库信息
     * @param username GitHub用户名
     * @return 仓库信息的JSON字符串
     */
    public String getRepoInfo(String username);


    /**
     * 
     */
}
