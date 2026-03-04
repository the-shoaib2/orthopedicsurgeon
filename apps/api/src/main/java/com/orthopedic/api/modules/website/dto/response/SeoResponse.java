package com.orthopedic.api.modules.website.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeoResponse {
    private String slug;
    private String title;
    private String description;
    private String keywords;
    private String ogImage;
    private String canonicalUrl;
}
