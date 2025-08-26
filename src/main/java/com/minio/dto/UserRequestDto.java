package com.minio.dto;

import java.util.Map;

/**
 * DTO for incoming user request to check filter matching
 */
public class UserRequestDto {
    
    private String smid;
    private String country;
    private String operatingSystem;
    private String clientVersion;
    private String browser;
    private String browserVersion;
    private String deviceType;
    private String language;
    private String timezone;
    private String userAgent;
    private String ipAddress;
    private String sessionId;
    private String userId;
    private String deviceId;
    private String appVersion;
    private String platform;
    private String carrier;
    private String networkType;
    private String screenResolution;
    private String deviceModel;
    private String osVersion;
    
    // Additional fields for custom parameters
    private Map<String, String> customFields;
    
    public UserRequestDto() {}
    
    public String getSmid() {
        return smid;
    }
    
    public void setSmid(String smid) {
        this.smid = smid;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getOperatingSystem() {
        return operatingSystem;
    }
    
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
    
    public String getClientVersion() {
        return clientVersion;
    }
    
    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }
    
    public String getBrowser() {
        return browser;
    }
    
    public void setBrowser(String browser) {
        this.browser = browser;
    }
    
    public String getBrowserVersion() {
        return browserVersion;
    }
    
    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getAppVersion() {
        return appVersion;
    }
    
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    
    public String getPlatform() {
        return platform;
    }
    
    public void setPlatform(String platform) {
        this.platform = platform;
    }
    
    public String getCarrier() {
        return carrier;
    }
    
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
    
    public String getNetworkType() {
        return networkType;
    }
    
    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
    
    public String getScreenResolution() {
        return screenResolution;
    }
    
    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }
    
    public String getDeviceModel() {
        return deviceModel;
    }
    
    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
    
    public String getOsVersion() {
        return osVersion;
    }
    
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }
    
    public Map<String, String> getCustomFields() {
        return customFields;
    }
    
    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }
    
    /**
     * Get field value by filter type
     */
    public String getFieldValue(String fieldType) {
        switch (fieldType) {
            case "SMID": return smid;
            case "COUNTRY": return country;
            case "OPERATING_SYSTEM": return operatingSystem;
            case "CLIENT_VERSION": return clientVersion;
            case "BROWSER": return browser;
            case "BROWSER_VERSION": return browserVersion;
            case "DEVICE_TYPE": return deviceType;
            case "LANGUAGE": return language;
            case "TIMEZONE": return timezone;
            case "USER_AGENT": return userAgent;
            case "IP_ADDRESS": return ipAddress;
            case "SESSION_ID": return sessionId;
            case "USER_ID": return userId;
            case "DEVICE_ID": return deviceId;
            case "APP_VERSION": return appVersion;
            case "PLATFORM": return platform;
            case "CARRIER": return carrier;
            case "NETWORK_TYPE": return networkType;
            case "SCREEN_RESOLUTION": return screenResolution;
            case "DEVICE_MODEL": return deviceModel;
            case "OS_VERSION": return osVersion;
            default:
                return customFields != null ? customFields.get(fieldType) : null;
        }
    }
}
