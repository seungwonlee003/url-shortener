package com.seungwonlee.urlshortener.application;

import com.seungwonlee.urlshortener.dto.UrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SpringBootTest
public class UrlServiceTest {

    @Autowired
    private UrlService urlService;

    @Test
    void concurrent_viewCount() throws InterruptedException {
        // given
        int memberCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(memberCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        urlService.createShortUrl(new UrlRequest("www.naver.com"));

        // when
        for (int i = 0; i < memberCount; i++) {
            executorService.submit(() -> {
                try {
                    log.info(Thread.currentThread().getName());
                    urlService.redirectToOriginalUrl("1");
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        System.out.println("successCount = " + successCount);
        System.out.println("failCount = " + failCount);

        // then
        log.info(String.valueOf(urlService.getShortUrlStats("1").getTotalClicks()));
    }
}
