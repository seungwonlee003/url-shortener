package com.seungwonlee.urlshortener.application;

import com.seungwonlee.urlshortener.aspect.ToLog;
import com.seungwonlee.urlshortener.domain.Url;
import com.seungwonlee.urlshortener.domain.UrlRepository;
import com.seungwonlee.urlshortener.dto.request.CustomUrlRequest;
import com.seungwonlee.urlshortener.dto.request.UrlRequest;
import com.seungwonlee.urlshortener.dto.response.UrlResponse;
import com.seungwonlee.urlshortener.dto.response.ViewCountResponse;
import com.seungwonlee.urlshortener.exception.ShortUrlNotFoundException;
import com.seungwonlee.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final GoogleSafeBrowsingService googleSafeBrowsingService;

    @Transactional
    public UrlResponse createShortUrl(UrlRequest urlRequest) {
        String validatedOriginalUrl = validateOriginalUrl(urlRequest.getOriginalUrl());
        googleSafeBrowsingService.checkUrlSafety(validatedOriginalUrl);
        Url url = createUrlEntity(validatedOriginalUrl);
        return new UrlResponse(url.getShortUrl());
    }

    @ToLog
    @Transactional
    public UrlResponse createCustomShortUrl(CustomUrlRequest urlRequest) {
        String validatedOriginalUrl = validateOriginalUrl(urlRequest.getOriginalUrl());
        String validatedCustomShortUrl = validateShortUrl(urlRequest.getCustomShortUrl());
        googleSafeBrowsingService.checkUrlSafety(validatedOriginalUrl);
        // encrypt the url using symmetric encryption algo?
        Url url = createUrlEntity(validatedOriginalUrl, validatedCustomShortUrl);
        return new UrlResponse(url.getShortUrl());
    }

    @Transactional
    public HttpHeaders redirectToOriginalUrl(String shortUrl) {
        Url url = findByShortUrl(shortUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, url.getOriginalUrl());
        urlRepository.increaseViewCount(shortUrl);
        return headers;
    }

    @Transactional
    public ViewCountResponse getShortUrlStats(String shortUrl) {
        Url url = findByShortUrl(shortUrl);
        return new ViewCountResponse(url.getViewCount());
    }

    private String validateShortUrl(String customShortUrl) {
        if(customShortUrl.length() < 5){
            throw new IllegalArgumentException("Custom short URL must be at least 5 characters long.");
        }
        return customShortUrl;
    }

    private String validateOriginalUrl(String originalUrl){
        if (!originalUrl.startsWith("https://") && !originalUrl.startsWith("http://")) {
            originalUrl = "https://" + originalUrl;
        }
        if (originalUrl.length() >= 2083) {
            throw new IllegalArgumentException("URL length exceeds the limit of 2083 characters");
        }
        return originalUrl;
    }

    private Url findByShortUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new ShortUrlNotFoundException(shortUrl));
    }

    private Url createUrlEntity(String originalUrl) {
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        urlRepository.save(url);
        url.setShortUrl(Base62Encoder.encode(url.getId()));
        return url;
    }

    private Url createUrlEntity(String originalUrl, String shortUrl){
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortUrl(shortUrl);
        urlRepository.save(url);
        return url;
    }
}