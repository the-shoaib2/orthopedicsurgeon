package com.orthopedic.api.modules.analytics.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SearchSuggestionResponse {
    private List<String> suggestions;
}
