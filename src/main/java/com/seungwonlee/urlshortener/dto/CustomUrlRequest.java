package com.seungwonlee.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUrlRequest {
    private String originalUrl;
    private String customShortUrl;
}
