package com.jborza.hnews.demo;

public class HackerNewsArticleSummary {
    private final HackerNewsArticle article;
    private final String summary;

    public HackerNewsArticleSummary(HackerNewsArticle article, String summary) {
        this.article = article;
        this.summary = summary;
    }

    public HackerNewsArticle getArticle() {
        return article;
    }

    public String getSummary() {
        return summary;
    }
}