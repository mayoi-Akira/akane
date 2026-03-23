package com.bot.akane.agent.toolsService.impl;

import com.bot.akane.agent.toolsService.GithubToolsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

@Service
public class GithubToolsServiceImpl implements GithubToolsService {
    
    private final String GITHUB_API_URL = "https://api.github.com/";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String getRepoInfo(String username) {
        String url = GITHUB_API_URL + "users/" + username + "/repos";
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonNode jsonArray = objectMapper.readTree(response.body());
                ArrayNode filteredArray = objectMapper.createArrayNode();
                
                for (JsonNode repo : jsonArray) {
                    ObjectNode filteredRepo = objectMapper.createObjectNode();
                    filteredRepo.put("name", repo.get("name").asText());
                    filteredRepo.put("description", repo.get("description").asText());
                    filteredRepo.put("created_at", repo.get("created_at").asText());
                    filteredRepo.put("updated_at", repo.get("updated_at").asText());
                    filteredRepo.put("stargazers_count", repo.get("stargazers_count").asInt());
                    filteredRepo.put("watchers", repo.get("watchers").asInt());
                    filteredRepo.put("language", repo.get("language").asText());
                    filteredRepo.put("size", repo.get("size").asInt());
                    filteredRepo.put("forks_count", repo.get("forks_count").asInt());
                    
                    filteredArray.add(filteredRepo);
                }
                
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(filteredArray);
            }
            
            return "Error: " + response.statusCode();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
