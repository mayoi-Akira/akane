package com.bot.akane.agent.toolsService;

public interface NowCoderToolsService {
    
    /**
     * 获取牛客网某用户的基本信息
     * @param userId 牛客网用户ID
     * @return 用户基本信息的JSON字符串
     */
    public String getNowCoderContestInfo(String userId);

    /**
     * 获取牛客网某用户的竞赛记录
     * @param userId 牛客网用户ID
     * @param recordCount 返回的竞赛记录条数（取前N条）
     * @param onlyRated 是否仅返回计rating的比赛记录
     * @return 用户竞赛记录的JSON字符串
     */
    public String getNowCoderContestRecord(String userId, int recordCount, boolean onlyRated);
}
