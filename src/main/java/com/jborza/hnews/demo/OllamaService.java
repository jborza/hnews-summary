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
public class OllamaService {

    @Value("${ollama.url}")
    private String url;

    @Value("${ollama.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final MarkdownConverter markdownConverter;

    public OllamaService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.markdownConverter = new MarkdownConverter();
    }

    private String getResponseBody(String url, HttpEntity<String> entity ){
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    public String generateSummary(String content) throws JsonProcessingException  {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // looks like our content  contains quotes or something
        String escapedContent = StringEscapeUtils.escapeJson(content);
        // TODO fix for DeepSeek
        int sizeLimit = 30000;
        if(escapedContent.length() > sizeLimit)
            escapedContent = escapedContent.substring(0, sizeLimit);
        String requestFormat = """
                {"prompt": "Summarize the following content: \\n %s",
                "model": "%s"
                }
                """;
        String requestBody = requestFormat.formatted(escapedContent, model);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // it's in {"model":"deepseek-r1:1.5b","created_at":"2025-04-06T19:46:03.0800156Z","response":"Okay","done":false}
        String body = getResponseBody(url, entity);
        // the body is a list of json objects separated by newline, so we need to iterate through them
        StringBuilder stringBuilder = new StringBuilder();
        body.lines().forEach(line -> {
            System.out.println(line);
            try {
                JsonNode root = objectMapper.readTree(line);
                // get "response"
                JsonNode responseNode = root.path("response");
                String responsePart = responseNode.asText();
                stringBuilder.append(responsePart);
            }
            catch (JsonProcessingException ignored){}
        });
        String response = stringBuilder.toString();
        // remove <think> ... </think> from Qwen models
        String markdownResponse = response.replaceAll("(?s)<think>.*?</think>", "");

        // Convert Markdown response to HTML
        return markdownConverter.convertToHtml(markdownResponse);
    }
}