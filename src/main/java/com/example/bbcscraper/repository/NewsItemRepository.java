package com.example.bbcscraper.repository;

import com.example.bbcscraper.entity.NewsItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsItemRepository extends JpaRepository<NewsItem, Long> {
}