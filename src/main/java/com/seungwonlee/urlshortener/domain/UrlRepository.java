package com.seungwonlee.urlshortener.domain;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

//  @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Url> findByShortUrl(String shortUrl);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Url url set url.viewCount = url.viewCount + 1 where url.shortUrl = :shortUrl")
    void increaseViewCount(String shortUrl);
}
