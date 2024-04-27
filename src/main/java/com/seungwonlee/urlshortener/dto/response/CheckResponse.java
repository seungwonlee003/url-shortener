package com.seungwonlee.urlshortener.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckResponse {
    @JsonProperty("matches")
    private List<ThreatMatch> threatMatches;
}
