package com.bot.akane.agent.toolsService.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.bot.akane.agent.toolsService.NowCoderToolsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NowCoderToolsServiceImpl implements NowCoderToolsService {

    private static final String USER_TEAM_LIST_URL = "https://ac.nowcoder.com/acm/contest/profile/user-team-list";
    private static final String CONTEST_HISTORY_URL = "https://ac.nowcoder.com/acm-heavy/acm/contest/profile/contest-joined-history";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getNowCoderContestInfo(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return buildError("牛客ID不能为空");
        }

        try {
            String trimmedUserId = userId.trim();
            List<TeamInfo> joinedTeams = fetchAllTeams(trimmedUserId);
            Set<String> teamNames = new HashSet<>();
            ArrayNode teamArray = objectMapper.createArrayNode();

            for (TeamInfo team : joinedTeams) {
                if (team.teamName != null && !team.teamName.isBlank()) {
                    teamNames.add(team.teamName);
                }

                ObjectNode teamNode = objectMapper.createObjectNode();
                teamNode.put("团队rating", team.rating);
                teamNode.put("团队名字", team.teamName == null ? "" : team.teamName);
                teamNode.put("比赛总数", team.contestCount);
                teamArray.add(teamNode);
            }

            List<JsonNode> joinedHistory = fetchAllContestHistory(trimmedUserId);
            String username = "";
            int contestCount = 0;
            int ratedContestCount = 0;
            String currentRating = "0";

            for (JsonNode record : joinedHistory) {
                String teamName = record.path("teamName").asText("");
                boolean isCreator = record.path("isCreator").asBoolean(false);
                boolean isTeamSignUp = record.path("isTeamSignUp").asBoolean(false);

                // 过滤团队参赛记录，只保留用户账号参赛记录
                if ((isTeamSignUp || !isCreator) && teamNames.contains(teamName)) {
                    continue;
                }

                contestCount++;
                if (username.isBlank() && !teamName.isBlank()) {
                    username = teamName;
                }

                String ratingStr = record.path("ratingStr").asText("").trim();
                if (isRatedContest(ratingStr)) {
                    ratedContestCount++;
                    if (currentRating.equals("0")) {
                        currentRating = ratingStr;
                    }
                }
            }

            ObjectNode result = objectMapper.createObjectNode();
            result.put("用户名", username);
            result.put("当前rating", currentRating);
            result.put("参加的比赛总数", contestCount);
            result.put("参加的rated比赛总数", ratedContestCount);
            result.set("已加入的团队", teamArray);
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("获取牛客竞赛信息失败 userId={}", userId, e);
            return buildError("获取牛客竞赛信息失败: " + e.getMessage());
        }
    }

    @Override
    public String getNowCoderContestRecord(String userId, int recordCount, boolean onlyRated) {
        if (userId == null || userId.trim().isEmpty()) {
            return buildError("牛客ID不能为空");
        }

        try {
            List<JsonNode> joinedHistory = fetchAllContestHistory(userId.trim());
            ArrayNode arrayNode = objectMapper.createArrayNode();

            int limit = recordCount == -1 ? Integer.MAX_VALUE : Math.max(recordCount, 0);
            int count = 0;
            for (JsonNode record : joinedHistory) {
                String ratingStr = record.path("ratingStr").asText("").trim();
                if (onlyRated && !isRatedContest(ratingStr)) {
                    continue;
                }

                if (count >= limit) {
                    break;
                }

                ObjectNode item = objectMapper.createObjectNode();
                item.put("参加人(或队伍)", record.path("teamName").asText(""));
                item.put("比赛名称", record.path("contestName").asText(""));

                String ratingChangeStr = record.path("ratingChangeStr").asText("0");
                item.put("rating变化", ratingStr + ratingChangeStr);

                int acceptedCount = record.path("acceptedCount").asInt(0);
                int problemCount = record.path("problemCount").asInt(0);
                item.put("解题数", acceptedCount + "/" + problemCount);

                int rank = record.path("rank").asInt(0);
                int userCount = record.path("userCount").asInt(0);
                item.put("排名", rank + "/" + userCount);

                arrayNode.add(item);
                count++;
            }
            return objectMapper.writeValueAsString(arrayNode);
        } catch (Exception e) {
            log.error("获取牛客竞赛记录失败 userId={}", userId, e);
            return buildError("获取牛客竞赛记录失败: " + e.getMessage());
        }
    }

    private List<TeamInfo> fetchAllTeams(String userId) throws Exception {
        List<TeamInfo> teams = new ArrayList<>();
        int page = 1;
        int pageCount = 1;

        while (page <= pageCount) {
            String url = USER_TEAM_LIST_URL + "?uid=" + userId
                    + "&page=" + page
                    + "&pageSize=10"
                    + "&_=" + System.currentTimeMillis();
            JsonNode root = sendGetRequest(url);
            checkNowCoderSuccess(root);

            JsonNode dataNode = root.path("data");
            JsonNode dataList = dataNode.path("dataList");

            if (dataList.isArray()) {
                for (JsonNode item : dataList) {
                    TeamInfo teamInfo = new TeamInfo();
                    teamInfo.teamName = item.path("name").asText("");
                    teamInfo.rating = item.path("rating").asInt(0);
                    teamInfo.contestCount = item.path("contestCount").asInt(0);
                    teams.add(teamInfo);
                }
            }

            pageCount = dataNode.path("pageInfo").path("pageCount").asInt(page);
            page++;
        }

        return teams;
    }

    private List<JsonNode> fetchAllContestHistory(String userId) throws Exception {
        List<JsonNode> records = new ArrayList<>();
        int page = 1;
        int pageCount = 1;

        while (page <= pageCount) {
            String url = CONTEST_HISTORY_URL + "?uid=" + userId
                    + "&page=" + page
                    + "&onlyJoinedFilter=true"
                    + "&onlyRatingFilter=false"
                    + "&contestEndFilter=true"
                    + "&_=" + System.currentTimeMillis();
            JsonNode root = sendGetRequest(url);
            checkNowCoderSuccess(root);

            JsonNode dataNode = root.path("data");
            JsonNode dataList = dataNode.path("dataList");

            if (dataList.isArray()) {
                for (JsonNode item : dataList) {
                    records.add(item);
                }
            }

            pageCount = dataNode.path("pageInfo").path("pageCount").asInt(page);
            page++;
        }

        return records;
    }

    private JsonNode sendGetRequest(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP请求失败，状态码: " + response.statusCode());
        }

        return objectMapper.readTree(response.body());
    }

    private void checkNowCoderSuccess(JsonNode root) {
        int code = root.path("code").asInt(-1);
        if (code != 0) {
            String msg = root.path("msg").asText("unknown error");
            throw new RuntimeException("牛客接口返回异常: " + msg + " (code=" + code + ")");
        }
    }

    private boolean isRatedContest(String ratingStr) {
        return !ratingStr.isEmpty() && !"不计".equals(ratingStr);
    }

    private String buildError(String message) {
        try {
            ObjectNode error = objectMapper.createObjectNode();
            error.put("error", message);
            return objectMapper.writeValueAsString(error);
        } catch (Exception ex) {
            return "{\"error\":\"" + message + "\"}";
        }
    }

    private static class TeamInfo {
        private String teamName;
        private int rating;
        private int contestCount;
    }
}
