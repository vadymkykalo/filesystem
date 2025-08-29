package com.minio.util;

import com.minio.dto.UserRequestDto;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper for converting UserRequestDto to flat Map structure (Heracles) and back
 */
public class UserRequestHeraclesMapper {
    
    /**
     * Pack UserRequestDto into flat Map structure
     */
    public static Map<String, String> toHeracles(UserRequestDto userRequest) {
        Map<String, String> heracles = new HashMap<>();
        
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
                heracles.put("custom_" + entry.getKey(), entry.getValue());
            }
        }
        
        return heracles;
    }
    
    /**
     * Unpack from flat Map structure to UserRequestDto
     */
    public static UserRequestDto fromHeracles(Map<String, String> heracles) {
        UserRequestDto userRequest = new UserRequestDto();
        
        if (heracles == null || heracles.isEmpty()) {
            return userRequest;
        }
        
        // Main fields
        userRequest.setSmid(heracles.get("smid"));
        userRequest.setCountry(heracles.get("country"));
        userRequest.setOperatingSystem(heracles.get("operatingSystem"));
        userRequest.setClientVersion(heracles.get("clientVersion"));
        userRequest.setBrowser(heracles.get("browser"));
        userRequest.setBrowserVersion(heracles.get("browserVersion"));
        userRequest.setDeviceType(heracles.get("deviceType"));
        userRequest.setLanguage(heracles.get("language"));
        userRequest.setTimezone(heracles.get("timezone"));
        userRequest.setUserAgent(heracles.get("userAgent"));
        userRequest.setIpAddress(heracles.get("ipAddress"));
        userRequest.setSessionId(heracles.get("sessionId"));
        userRequest.setUserId(heracles.get("userId"));
        userRequest.setDeviceId(heracles.get("deviceId"));
        userRequest.setAppVersion(heracles.get("appVersion"));
        userRequest.setPlatform(heracles.get("platform"));
        userRequest.setCarrier(heracles.get("carrier"));
        userRequest.setNetworkType(heracles.get("networkType"));
        userRequest.setScreenResolution(heracles.get("screenResolution"));
        userRequest.setDeviceModel(heracles.get("deviceModel"));
        userRequest.setOsVersion(heracles.get("osVersion"));
        
        // Custom fields
        Map<String, String> customFields = new HashMap<>();
        for (Map.Entry<String, String> entry : heracles.entrySet()) {
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
     * Helper method to add value to Map only if it's not null
     */
    private static void putIfNotNull(Map<String, String> map, String key, String value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
