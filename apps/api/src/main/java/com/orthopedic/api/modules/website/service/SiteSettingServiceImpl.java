package com.orthopedic.api.modules.website.service;

import com.orthopedic.api.modules.audit.annotation.LogMutation;
import com.orthopedic.api.modules.website.dto.request.UpdateSiteSettingRequest;
import com.orthopedic.api.modules.website.dto.response.SiteSettingResponse;
import com.orthopedic.api.modules.website.entity.SiteSetting;
import com.orthopedic.api.modules.website.repository.SiteSettingRepository;
import com.orthopedic.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SiteSettingServiceImpl implements SiteSettingService {

    private final SiteSettingRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<SiteSettingResponse> getAllSettings() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "site_settings_public")
    public List<SiteSettingResponse> getPublicSettings() {
        return repository.findByIsPublicTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SiteSettingResponse getSettingByKey(String key) {
        return repository.findByKey(key)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Setting not found with key: " + key));
    }

    @Override
    @CacheEvict(value = "site_settings_public", allEntries = true)
    @LogMutation(action = "UPDATE_SITE_SETTING", entityName = "SITE_SETTING")
    public SiteSettingResponse updateSetting(UUID id, UpdateSiteSettingRequest request) {
        SiteSetting setting = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setting not found"));
        setting.setValue(request.getValue());
        return mapToResponse(repository.save(setting));
    }

    @Override
    @CacheEvict(value = "site_settings_public", allEntries = true)
    @LogMutation(action = "UPDATE_SITE_SETTING", entityName = "SITE_SETTING")
    public SiteSettingResponse updateSettingByKey(String key, UpdateSiteSettingRequest request) {
        SiteSetting setting = repository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Setting not found with key: " + key));
        setting.setValue(request.getValue());
        return mapToResponse(repository.save(setting));
    }

    private SiteSettingResponse mapToResponse(SiteSetting setting) {
        return SiteSettingResponse.builder()
                .id(setting.getId())
                .key(setting.getKey())
                .value(setting.getValue())
                .category(setting.getCategory())
                .isPublic(setting.getIsPublic())
                .createdAt(setting.getCreatedAt())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
}
