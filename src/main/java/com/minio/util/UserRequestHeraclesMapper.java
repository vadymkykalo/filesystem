package com.minio.util;

import com.minio.dto.UserRequestDto;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper for converting UserRequestDto to Heracles object and back
 */
public class UserRequestHeraclesMapper {
    
    /**
     * Pack UserRequestDto into Heracles object
     */
    public static Heracles toHeracles(UserRequestDto userRequest) {
        Heracles heracles = new Heracles();
        
        if (userRequest == null) {
            return heracles;
        }
        
        // Main fields
        putIfNotNull(heracles, "smid", userRequest.getSmid());
        putIfNotNull(heracles, "country", userRequest.getCountry());
        putIfNotNull(heracles, "operatingSystem", userRequest.getOperatingSystem());
        putIfNotNull(heracles, "clientVersion", userRequest.getClientVersion());
        putIfNotNull(heracles, "browser", userRequest.getBrowser());
        putIfNotNull(heracles, "browserVersion", userRequest.getBrowserVersion());
        putIfNotNull(heracles, "deviceType", userRequest.getDeviceType());
        putIfNotNull(heracles, "language", userRequest.getLanguage());
        putIfNotNull(heracles, "timezone", userRequest.getTimezone());
        putIfNotNull(heracles, "userAgent", userRequest.getUserAgent());
        putIfNotNull(heracles, "ipAddress", userRequest.getIpAddress());
        putIfNotNull(heracles, "sessionId", userRequest.getSessionId());
        putIfNotNull(heracles, "userId", userRequest.getUserId());
        putIfNotNull(heracles, "deviceId", userRequest.getDeviceId());
        putIfNotNull(heracles, "appVersion", userRequest.getAppVersion());
        putIfNotNull(heracles, "platform", userRequest.getPlatform());
        putIfNotNull(heracles, "carrier", userRequest.getCarrier());
        putIfNotNull(heracles, "networkType", userRequest.getNetworkType());
        putIfNotNull(heracles, "screenResolution", userRequest.getScreenResolution());
        putIfNotNull(heracles, "deviceModel", userRequest.getDeviceModel());
        putIfNotNull(heracles, "osVersion", userRequest.getOsVersion());
        
        // Custom fields
        if (userRequest.getCustomFields() != null) {
            for (Map.Entry<String, String> entry : userRequest.getCustomFields().entrySet()) {
                heracles.setValue("custom_" + entry.getKey(), entry.getValue());
            }
        }
        
        return heracles;
    }
    
    /**
     * Unpack from Heracles object to UserRequestDto
     */
    public static UserRequestDto fromHeracles(Heracles heracles) {
        UserRequestDto userRequest = new UserRequestDto();
        
        if (heracles == null || heracles.isEmpty()) {
            return userRequest;
        }
        
        // Main fields
        userRequest.setSmid(heracles.getValue("smid"));
        userRequest.setCountry(heracles.getValue("country"));
        userRequest.setOperatingSystem(heracles.getValue("operatingSystem"));
        userRequest.setClientVersion(heracles.getValue("clientVersion"));
        userRequest.setBrowser(heracles.getValue("browser"));
        userRequest.setBrowserVersion(heracles.getValue("browserVersion"));
        userRequest.setDeviceType(heracles.getValue("deviceType"));
        userRequest.setLanguage(heracles.getValue("language"));
        userRequest.setTimezone(heracles.getValue("timezone"));
        userRequest.setUserAgent(heracles.getValue("userAgent"));
        userRequest.setIpAddress(heracles.getValue("ipAddress"));
        userRequest.setSessionId(heracles.getValue("sessionId"));
        userRequest.setUserId(heracles.getValue("userId"));
        userRequest.setDeviceId(heracles.getValue("deviceId"));
        userRequest.setAppVersion(heracles.getValue("appVersion"));
        userRequest.setPlatform(heracles.getValue("platform"));
        userRequest.setCarrier(heracles.getValue("carrier"));
        userRequest.setNetworkType(heracles.getValue("networkType"));
        userRequest.setScreenResolution(heracles.getValue("screenResolution"));
        userRequest.setDeviceModel(heracles.getValue("deviceModel"));
        userRequest.setOsVersion(heracles.getValue("osVersion"));
        
        // Custom fields
        Map<String, String> customFields = new HashMap<>();
        for (Map.Entry<String, String> entry : heracles.getData().entrySet()) {
            if (entry.getKey().startsWith("custom_")) {
                String customKey = entry.getKey().substring("custom_".length());
                customFields.put(customKey, entry.getValue());
            }
        }
        
        if (!customFields.isEmpty()) {
            userRequest.setCustomFields(customFields);
        }
        
        return userRequest;
    }
    
    /**
     * Helper method to add value to Heracles only if it's not null
     */
    private static void putIfNotNull(Heracles heracles, String key, String value) {
        if (value != null) {
            heracles.setValue(key, value);
        }
    }
}
