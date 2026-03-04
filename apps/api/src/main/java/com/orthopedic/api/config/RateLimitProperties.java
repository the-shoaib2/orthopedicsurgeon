package com.orthopedic.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private int defaultLimit = 60;
    private long defaultWindow = 60;

    private int loginLimit = 5;
    private long loginWindow = 900; // 15 mins

    private int searchLimit = 20;
    private long searchWindow = 60;

    // Getters and Setters
    public int getDefaultLimit() {
        return defaultLimit;
    }

    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    public long getDefaultWindow() {
        return defaultWindow;
    }

    public void setDefaultWindow(long defaultWindow) {
        this.defaultWindow = defaultWindow;
    }

    public int getLoginLimit() {
        return loginLimit;
    }

    public void setLoginLimit(int loginLimit) {
        this.loginLimit = loginLimit;
    }

    public long getLoginWindow() {
        return loginWindow;
    }

    public void setLoginWindow(long loginWindow) {
        this.loginWindow = loginWindow;
    }

    public int getSearchLimit() {
        return searchLimit;
    }

    public void setSearchLimit(int searchLimit) {
        this.searchLimit = searchLimit;
    }

    public long getSearchWindow() {
        return searchWindow;
    }

    public void setSearchWindow(long searchWindow) {
        this.searchWindow = searchWindow;
    }
}
