package com.example.demo;


import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String getWeatherByCoordinates(double lat, double lon) {
        // 1. 查询点的元数据（包括forecast链接）
        String pointUrl = String.format("https://api.weather.gov/points/%f,%f", lat, lon);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "SpringBootWeatherApp (1446746582@qq.com)");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> pointResp = restTemplate.exchange(pointUrl, HttpMethod.GET, entity, String.class);
        // 2. 提取forecast链接
        String body = pointResp.getBody();
        String forecastUrl = null;
        if (body != null && body.contains("\"forecast\"")) {
            int idx = body.indexOf("\"forecast\"");
            int start = body.indexOf("\"", idx + 11) + 1;
            int end = body.indexOf("\"", start);
            forecastUrl = body.substring(start, end);
        }
        if (forecastUrl == null) {
            throw new RuntimeException("未能找到 forecast 链接");
        }
        // 3. 查询forecast
        ResponseEntity<String> forecastResp = restTemplate.exchange(forecastUrl, HttpMethod.GET, entity, String.class);
        return forecastResp.getBody();
    }
}