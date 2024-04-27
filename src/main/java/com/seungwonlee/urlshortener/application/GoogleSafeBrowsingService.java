package com.seungwonlee.urlshortener.application;

import com.seungwonlee.urlshortener.aspect.ToLog;
import com.seungwonlee.urlshortener.dto.response.CheckResponse;
import com.seungwonlee.urlshortener.exception.MaliciousWebsiteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class GoogleSafeBrowsingService {
    private final RestTemplate restTemplate;
    private final String apiKey;
    private static final String GOOGLE_API_URL = "https://safebrowsing.googleapis.com/v4/threatMatches:find";

    public GoogleSafeBrowsingService(RestTemplate restTemplate, @Value("${google.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    @ToLog
    public void checkUrlSafety(String url){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GOOGLE_API_URL)
                .queryParam("key", apiKey);

        String requestBody =
                "{" +
                    "\"threatInfo\": {" +
                        "\"threatTypes\": [\"UNWANTED_SOFTWARE\", \"SOCIAL_ENGINEERING\", \"MALWARE\", \"THREAT_TYPE_UNSPECIFIED\", \"POTENTIALLY_HARMFUL_APPLICATION\"]," +
                        "\"platformTypes\": [\"ANY_PLATFORM\"]," +
                        "\"threatEntryTypes\": [\"URL\"]," +
                        "\"threatEntries\": [" +
                        "{\"url\": \"" + url + "\"}" +
                        "]" +
                    "}" +
                "}";

        log.info("JSON request body payload is {}", requestBody);
        ResponseEntity<CheckResponse> response = restTemplate.postForEntity(builder.toUriString(), requestBody, CheckResponse.class);

        if (response.getBody() != null && response.getBody().getThreatMatches() != null) {
            log.info("Threat Type: {}", response.getBody().getThreatMatches().getFirst().getThreatType());
            throw new MaliciousWebsiteException("malicious websites cannot be registered");
        }
    }
}
