package com.jborza.hnews.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Controller
public class HackerNewsController {
    @Autowired
    private HackerNewsService service;

    @GetMapping("/top-articles")
    public String getTopArticles(Model model) throws IOException {
        List<HackerNewsArticle> articles = service.fetchTopArticles();
        System.out.println("called getTopArticles, found "+articles.size()+" top articles.");
        model.addAttribute("articles", articles);
        return "top-articles"; // Ensure this matches the template name
    }

    @GetMapping("/top-articles-summary")
    public String getTopArticlesSummary(Model model) throws IOException {
        List<HackerNewsArticleSummary> articles = service.fetchAndSummarizeArticles();
        model.addAttribute("articles", articles);
        return "top-articles-summary";
    }
}
