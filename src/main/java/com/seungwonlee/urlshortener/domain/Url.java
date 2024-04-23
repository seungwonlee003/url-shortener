package com.seungwonlee.urlshortener.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "original_url")
    private String originalUrl;

    // creates unique index and is case sensitive
    @Column(name = "short_url", length = 50, unique = true, columnDefinition = "VARCHAR(50) COLLATE utf8_bin")
    private String shortUrl;
}
