package com.seungwonlee.urlshortener.application;

import com.seungwonlee.urlshortener.domain.Url;
import com.seungwonlee.urlshortener.domain.UrlRepository;
import com.seungwonlee.urlshortener.dto.CustomUrlRequest;
import com.seungwonlee.urlshortener.dto.UrlRequest;
import com.seungwonlee.urlshortener.dto.UrlResponse;
import com.seungwonlee.urlshortener.dto.ViewCountResponse;
import com.seungwonlee.urlshortener.exception.CustomShortUrlAlreadyExistsException;
import com.seungwonlee.urlshortener.exception.ShortUrlNotFoundException;
import com.seungwonlee.urlshortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;

    @Transactional
    public UrlResponse createShortUrl(UrlRequest urlRequest) {
        Url url = createUrlEntity(urlRequest.getOriginalUrl());
        return new UrlResponse(url.getShortUrl());
    }

    @Transactional
    public UrlResponse createCustomShortUrl(CustomUrlRequest urlRequest) throws CustomShortUrlAlreadyExistsException {
        Url url = new Url();
        url.setOriginalUrl(urlRequest.getOriginalUrl());
        url.setShortUrl(urlRequest.getCustomShortUrl());
        // short url is a unique index
        try {
            urlRepository.save(url);
        } catch (DataIntegrityViolationException e) {
            throw new CustomShortUrlAlreadyExistsException("Custom short URL already exists");
        }
        return new UrlResponse(url.getShortUrl());
    }

    @Transactional
    public HttpHeaders redirectToOriginalUrl(String shortUrl) {
        Url url = findByShortUrl(shortUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, url.getOriginalUrl());
        url.setViewCount(url.getViewCount() + 1);
        return headers;
    }

    @Transactional
    public ViewCountResponse getShortUrlStats(String shortUrl) {
        Url url = findByShortUrl(shortUrl);
        return new ViewCountResponse(url.getViewCount());
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
}
