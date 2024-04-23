package com.seungwonlee.urlshortener.presentation;

import com.seungwonlee.urlshortener.application.UrlService;
import com.seungwonlee.urlshortener.dto.CustomUrlRequest;
import com.seungwonlee.urlshortener.dto.UrlRequest;
import com.seungwonlee.urlshortener.dto.UrlResponse;
import com.seungwonlee.urlshortener.dto.ViewCountResponse;
import com.seungwonlee.urlshortener.exception.CustomShortUrlAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
// http://localhost:8080/swagger-ui/index.html#/
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> createShortUrl(@RequestBody UrlRequest urlRequest) {
        return ResponseEntity.ok(urlService.createShortUrl(urlRequest));
    }

    @PostMapping("/custom")
    public ResponseEntity<UrlResponse> createCustomShortUrl(@RequestBody CustomUrlRequest urlRequest)
            throws CustomShortUrlAlreadyExistsException {
        return ResponseEntity.ok(urlService.createCustomShortUrl(urlRequest));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortUrl) {
        HttpHeaders headers = urlService.redirectToOriginalUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers(headers).build();
    }

    @GetMapping("/{shortUrl}/stats")
    public ResponseEntity<ViewCountResponse> getShortUrlStats(@PathVariable String shortUrl) {
        return ResponseEntity.ok(urlService.getShortUrlStats(shortUrl));
    }
}