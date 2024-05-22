package com.example.bbcscraper.service;

import com.example.bbcscraper.entity.NewsItem;
import com.example.bbcscraper.repository.NewsItemRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BBCScrapperService {

    private static final String BBC_NEWS_URL = "https://www.bbc.com/news";
    private static final Logger logger = LoggerFactory.getLogger(BBCScrapperService.class);

    @Autowired
    private NewsItemRepository newsItemRepository;

    public void scrapeAndSaveNews() {
        List<NewsItem> newsItems = fetchNews();
        if (newsItems != null && !newsItems.isEmpty()) {
            newsItemRepository.saveAll(newsItems);
            logger.info("Saved {} news items to the database.", newsItems.size());
        } else {
            logger.warn("No news items found to save.");
        }
    }

    private List<NewsItem> fetchNews() {
        List<NewsItem> newsItems = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(BBC_NEWS_URL).get();
            Elements items = doc.select("div[data-testid='edinburgh-article']");

            for (Element item : items) {
                String headline = item.select("h2[data-testid='card-headline']").text();
                String summary = item.select("p[data-testid='card-description']").text();
                String link = item.select("a[data-testid='internal-link']").attr("href");

                if (!link.startsWith("https://")) {
                    link = "https://www.bbc.com" + link;
                }

                if (!headline.isEmpty() && !summary.isEmpty() && !link.isEmpty()) {
                    newsItems.add(new NewsItem(headline, summary, link));
                } else {
                    logger.warn("Skipped a news item due to missing information: headline='{}', summary='{}', link='{}'.", headline, summary, link);
                }
            }
        } catch (IOException e) {
            logger.error("Error fetching news from BBC: ", e);
            return null;
        }

        return newsItems;
    }
}