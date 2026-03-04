package com.orthopedic.api.modules.audit.dto.request;

import java.util.UUID;

public class AuditEventRequest {
    private String entityType;
    private UUID entityId;
    private String action;
    private String oldValues;
    private String newValues;
    private UUID userId;
    private String ipAddress;
    private String userAgent;
    private String metadata;
    private String details;
    private String status;

    public AuditEventRequest() {
    }

    public AuditEventRequest(String entityType, UUID entityId, String action, String oldValues,
            String newValues, UUID userId, String ipAddress, String userAgent, String metadata, String details,
            String status) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.metadata = metadata;
        this.details = details;
        this.status = status;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOldValues() {
        return oldValues;
    }

    public void setOldValues(String oldValues) {
        this.oldValues = oldValues;
    }

    public String getNewValues() {
        return newValues;
    }

    public void setNewValues(String newValues) {
        this.newValues = newValues;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static AuditEventRequestBuilder builder() {
        return new AuditEventRequestBuilder();
    }

    public static class AuditEventRequestBuilder {
        private String entityType;
        private UUID entityId;
        private String action;
        private String oldValues;
        private String newValues;
        private UUID userId;
        private String ipAddress;
        private String userAgent;
        private String metadata;
        private String details;
        private String status;

        public AuditEventRequestBuilder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public AuditEventRequestBuilder entityId(UUID entityId) {
            this.entityId = entityId;
            return this;
        }

        public AuditEventRequestBuilder action(String action) {
            this.action = action;
            return this;
        }

        public AuditEventRequestBuilder oldValues(String oldValues) {
            this.oldValues = oldValues;
            return this;
        }

        public AuditEventRequestBuilder newValues(String newValues) {
            this.newValues = newValues;
            return this;
        }

        public AuditEventRequestBuilder userId(java.util.UUID userId) {
            this.userId = userId;
            return this;
        }

        public AuditEventRequestBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public AuditEventRequestBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public AuditEventRequestBuilder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public AuditEventRequestBuilder details(String details) {
            this.details = details;
            return this;
        }

        public AuditEventRequestBuilder status(String status) {
            this.status = status;
            return this;
        }

        public AuditEventRequest build() {
            return new AuditEventRequest(entityType, entityId, action, oldValues, newValues, userId, ipAddress,
                    userAgent, metadata, details, status);
        }
    }
}
