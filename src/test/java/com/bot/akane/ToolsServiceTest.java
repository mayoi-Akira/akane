package com.bot.akane;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bot.akane.agent.toolsService.CityLocationService;
import com.bot.akane.agent.toolsService.EmailService;
import com.bot.akane.agent.toolsService.WeatherService;
import com.bot.akane.agent.toolsService.GithubToolsService;
import com.bot.akane.agent.toolsService.NowCoderToolsService;

@SpringBootTest
public class ToolsServiceTest {
    @Autowired
    private CityLocationService cityLocationService;
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private EmailService emailService;

    @Test
    public void testGetCityLocation() {
        String cityName = "金州区";
        String adm = "";

        String location = cityLocationService.getCityLocation(cityName, adm);
        System.out.println("城市经纬度信息: " + location);
    }

    @Test
    public void testGetWeatherForDays() {
        String cityName = "金州区";
        String adm = "";
        int days = 3;
        String weatherInfo = weatherService.getWeatherForDays(cityName, adm, days);
        System.out.println("未来" + days + "天的天气信息: " + weatherInfo);
    }

    @Test
    public void testGetWeatherForHours() {
        String cityName = "金州区";
        String adm = "";
        String weatherInfo = weatherService.getWeatherForHours(cityName, adm, 2);
        System.out.println("未来2小时的天气信息: " + weatherInfo);
    }

    @Test
    public void testEmailTool() {
        String to = "akane36@163.com";
        String subject = "测试邮件";
        String content = "这是一封测试邮件，测试异步发送功能。";

        emailService.sendEmailAsync(to, subject, content);
        System.out.println("邮件发送请求已提交，实际发送在后台异步执行中...");

    }
    @Autowired
    private GithubToolsService githubToolsService;
    @Test
    public void testGithubGetRepoInfo(){
        String result = githubToolsService.getRepoInfo("mayoi-Akira");
        System.out.println(result);
    }

    @Autowired
    private NowCoderToolsService nowCoderToolsService;
    @Test
    public void testNowCoderToolsService() {
        String result = nowCoderToolsService.getNowCoderContestInfo("588125209");
        System.out.println(result);

        result = nowCoderToolsService.getNowCoderContestRecord("588125209",10, true);
        System.out.println(result);
    }

}
