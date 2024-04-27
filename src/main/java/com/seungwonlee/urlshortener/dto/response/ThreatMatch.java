package com.seungwonlee.urlshortener.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThreatMatch {
    private String threatType;
    private String platformType;
    private Threat threat;
    private String threatEntryType;
}
