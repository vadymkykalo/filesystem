package com.minio.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Heracles - class for flat data storage in key-value format
 * Used for passing complex objects through executors
 */
public class Heracles {
    
    private Map<String, String> data;
    
    public Heracles() {
        this.data = new HashMap<>();
    }
    
    public Heracles(Map<String, String> data) {
        this.data = data != null ? new HashMap<>(data) : new HashMap<>();
    }
    
    /**
     * Set value for the given key
     */
    public void setValue(String key, String value) {
        if (key != null) {
            if (value != null) {
                data.put(key, value);
            } else {
                data.remove(key);
            }
        }
    }
    
    /**
     * Get value by key
     */
    public String getValue(String key) {
        return data.get(key);
    }
    
    /**
     * Check if key exists
     */
    public boolean hasKey(String key) {
        return data.containsKey(key);
    }
    
    /**
     * Get all data as Map (for internal use)
     */
    public Map<String, String> getData() {
        return new HashMap<>(data);
    }
    
    /**
     * Check if Heracles is empty
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    /**
     * Clear all data
     */
    public void clear() {
        data.clear();
    }
    
    /**
     * Get number of entries
     */
    public int size() {
        return data.size();
    }
}
