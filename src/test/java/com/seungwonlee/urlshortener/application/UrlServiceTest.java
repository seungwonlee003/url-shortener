package com.seungwonlee.urlshortener.application;

import com.seungwonlee.urlshortener.dto.request.UrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class UrlServiceTest {

    @Autowired
    private UrlService urlService;

    @RepeatedTest(1)
    void testViewCountConcurrency() throws InterruptedException {
        int memberCount = 500;
        int threadCount = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
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

        assertEquals(memberCount, successCount.get());
        assertEquals(0, failCount.get());
        assertEquals(memberCount, urlService.getShortUrlStats("1").getTotalClicks());
    }
}
