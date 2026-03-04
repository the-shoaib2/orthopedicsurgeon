package com.orthopedic.api.modules.website.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MenuResponse {
    private UUID id;
    private String title;
    private String url;
    private int order;
    private List<MenuResponse> children;
}
