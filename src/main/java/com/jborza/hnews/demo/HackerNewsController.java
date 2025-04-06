package com.jborza.hnews.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class HackerNewsController {
    @Autowired
    private HackerNewsService service;

    @GetMapping("/top-articles")
    public List<HackerNewsArticle> getTopArticles() throws IOException {
        return service.fetchTopArticles();
    }
}
