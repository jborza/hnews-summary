package com.jborza.hnews.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.client.RestTemplate;

@Service
public class HackerNewsService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final OpenAIService openAIService;


    private final RestTemplate restTemplate;

    public HackerNewsService(RestTemplate restTemplate, OpenAIService service) {
        this.restTemplate = restTemplate;
        this.openAIService = service;
    }

    @Autowired
    private HackerNewsArticleRepository repository;

    public List<HackerNewsArticle> fetchTopArticles() throws IOException {
        List<HackerNewsArticle> articles = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String url = "https://news.ycombinator.com/news?p=" + (i + 1);
            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select(".athing");

            for (Element item : items) {
                String title = item.select(".titleline").text();
                String link = item.select(".titleline").select("a").attr("href");
                if(!link.contains("http")){
                    // internal HN link like item?id=43558671, prepend HN
                    link = "https://news.ycombinator.com/" + link;
                }
                var commentEl = item.nextElementSibling().select("a:contains(comment)");
                int comments = 0;
                if(!commentEl.isEmpty()){
                    // text is like N comments, or 1 comment, or discuss if 0 comments
                    comments = Integer.parseInt(commentEl.text().split(" ")[0]);
                }
                HackerNewsArticle article = new HackerNewsArticle();
                article.setTitle(title);
                article.setUrl(link);
                article.setComments(comments);

                articles.add(article);
            }
        }

        articles.sort((a1, a2) -> Integer.compare(a2.getComments(), a1.getComments()));
        List<HackerNewsArticle> top10Articles = articles.subList(0, Math.min(10, articles.size()));

        repository.saveAll(top10Articles);
        return top10Articles;
    }

    public String fetchArticleContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.text();
    }

//    public String generateSummary(String content) {
//        String url = "https://api.openai.com/v1/responses";
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + apiKey);
//        headers.set("Content-Type", "application/json");
//
//        String requestBody = String.format("{\"input\": \"Summarize the following content:\\n\\n%s\\n\\nSummary:\", \"model\": \"gpt-4o\"}", content);
//        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//        return response.getBody();
//    }

    public List<HackerNewsArticleSummary> fetchAndSummarizeArticles() throws IOException {
        List<HackerNewsArticle> articles = fetchTopArticles();
        // for now, take just the first one
//        var first = new ArrayList<>(articles);
//        int count = first.size();
//        for(int i = 1; i < count; i++){
//            first.removeLast();
//        }

        return articles.stream().map(article -> {
            try {
                String content = fetchArticleContent(article.getUrl());
                String summary = openAIService.generateSummary(content);
                return new HackerNewsArticleSummary(article, summary);
            } catch (IOException e) {
                e.printStackTrace();
                return new HackerNewsArticleSummary(article, "Summary not available");
            }
        }).collect(Collectors.toList());
    }
}
