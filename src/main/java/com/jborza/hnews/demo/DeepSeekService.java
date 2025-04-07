package com.jborza.hnews.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class DeepSeekService {


    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DeepSeekService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String generateSummary(String content) throws JsonProcessingException {
        String url = "http://localhost:11434/api/generate";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // looks like our content  contains quotes or something
        String escapedContent = StringEscapeUtils.escapeJson(content);
        // limit for GPT 4o is 30000, so shrink the escaped content if it's too big
        // TODO fix for DeepSeek
        int sizeLimit = 30000;
        if(escapedContent.length() > sizeLimit)
            escapedContent = escapedContent.substring(0, sizeLimit);
        String requestBody = String.format("{\"prompt\": \"Summarize the following content:\\n\\n%s\\n\\nSummary:\", \"model\": \"deepseek-r1:1.5b\"}", escapedContent);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // TODO parse the text from response
        // it's in
        // {"model":"deepseek-r1:1.5b","created_at":"2025-04-06T19:46:03.0800156Z","response":"Okay","done":false}
        JsonNode root = objectMapper.readTree(response.getBody());
        return response.getBody();
//        // Parse the response JSON and extract the summary
//        JsonNode root = objectMapper.readTree(response.getBody());
//        JsonNode textNode = root.path("output").get(0).path("content").get(0).path("text");
//        return textNode.asText();
    }


}