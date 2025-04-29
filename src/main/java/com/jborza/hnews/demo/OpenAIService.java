package com.jborza.hnews.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.text.StringEscapeUtils;
import java.io.IOException;


@Service
public class OpenAIService {


    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenAIService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("OPENAI_API_KEY");
    }

    public String generateSummary(String content) throws JsonProcessingException {
        String url = "https://api.openai.com/v1/responses";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // looks like our content  contains quotes or something
        String escapedContent = StringEscapeUtils.escapeJson(content);
        // limit for GPT 4o is 30000, so shrink the escaped content if it's too big
        int sizeLimit = 30000;
        if(escapedContent.length() > sizeLimit)
            escapedContent = escapedContent.substring(0, sizeLimit);
        String requestBody = String.format("{\"input\": \"Summarize the following content:\\n\\n%s\\n\\nSummary:\", \"model\": \"gpt-4o\"}", escapedContent);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Parse the response JSON and extract the summary
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode textNode = root.path("output").get(0).path("content").get(0).path("text");
        return textNode.asText();
    }


}