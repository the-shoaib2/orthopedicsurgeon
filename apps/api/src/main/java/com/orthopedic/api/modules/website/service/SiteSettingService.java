package com.orthopedic.api.modules.website.service;

import com.orthopedic.api.modules.website.dto.request.UpdateSiteSettingRequest;
import com.orthopedic.api.modules.website.dto.response.SiteSettingResponse;
import java.util.List;
import java.util.UUID;

public interface SiteSettingService {
    List<SiteSettingResponse> getAllSettings();

    List<SiteSettingResponse> getPublicSettings();

    SiteSettingResponse getSettingByKey(String key);

    SiteSettingResponse updateSetting(UUID id, UpdateSiteSettingRequest request);

    SiteSettingResponse updateSettingByKey(String key, UpdateSiteSettingRequest request);
}
