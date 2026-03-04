package com.orthopedic.api.modules.admin.service;

import com.orthopedic.api.modules.audit.annotation.LogMutation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ApiControlService {

    private final StringRedisTemplate redisTemplate;

    @LogMutation(action = "UPDATE_MAINTENANCE_MODE", entityName = "SYSTEM")
    public void setMaintenanceMode(boolean enabled) {
        redisTemplate.opsForValue().set("maintenance:mode", String.valueOf(enabled));
    }

    public boolean isMaintenanceModeEnabled() {
        return "true".equals(redisTemplate.opsForValue().get("maintenance:mode"));
    }

    public void addAllowedIp(String ip) {
        redisTemplate.opsForSet().add("maintenance:allowedIps", ip);
    }

    public void removeAllowedIp(String ip) {
        redisTemplate.opsForSet().remove("maintenance:allowedIps", ip);
    }

    public Set<String> getAllowedIps() {
        return redisTemplate.opsForSet().members("maintenance:allowedIps");
    }

    @LogMutation(action = "BLOCK_IP", entityName = "SECURITY")
    public void blockIp(String ip) {
        redisTemplate.opsForSet().add("blocked:ips", ip);
    }

    @LogMutation(action = "UNBLOCK_IP", entityName = "SECURITY")
    public void unblockIp(String ip) {
        redisTemplate.opsForSet().remove("blocked:ips", ip);
    }

    public Set<String> getBlockedIps() {
        return redisTemplate.opsForSet().members("blocked:ips");
    }

    @LogMutation(action = "DISABLE_API", entityName = "API_CONTROL")
    public void disableApiEndpoint(String method, String path, String reason) {
        String key = "api:disabled:" + method.toUpperCase() + ":" + path;
        redisTemplate.opsForValue().set(key, reason != null ? reason : "Temporarily disabled by Admin");
    }

    @LogMutation(action = "ENABLE_API", entityName = "API_CONTROL")
    public void enableApiEndpoint(String method, String path) {
        String key = "api:disabled:" + method.toUpperCase() + ":" + path;
        redisTemplate.delete(key);
    }

    public Map<String, String> getDisabledApiEndpoints() {
        Set<String> keys = redisTemplate.keys("api:disabled:*");
        Map<String, String> disabledApis = new HashMap<>();
        if (keys != null) {
            for (String key : keys) {
                String reason = redisTemplate.opsForValue().get(key);
                disabledApis.put(key.replace("api:disabled:", ""), reason);
            }
        }
        return disabledApis;
    }
}
