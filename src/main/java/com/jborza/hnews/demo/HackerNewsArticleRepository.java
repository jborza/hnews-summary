package com.jborza.hnews.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HackerNewsArticleRepository extends JpaRepository<HackerNewsArticle, Long> {
}