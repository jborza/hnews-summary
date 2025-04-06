package com.jborza.hnews.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class HackerNewsApiController {
    @Autowired
    private HackerNewsService service;

    @GetMapping("/top-articles-api")
    public List<HackerNewsArticle> getTopArticlesApi() throws IOException {
        return service.fetchTopArticles();
    }

}
