package com.minio.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Heracles utility class for object serialization and manipulation
 * Based on the original Heracles implementation
 * 
 * @author dmitkhaylenko
 */
public class Heracles implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private ErrorInfo error;
    private HashMap<String, Object> values = new HashMap<>();
    private transient Integer recursiveObjHash;
    private transient Object recursiveObjEquals;
    
    public Heracles() {}
    
    public Heracles(PanteonException error) {
        this.error = (error == null) ? null : new ErrorInfo(error);
    }
    
    public Heracles(Object object) throws PanteonException {
        this(object, null, (String[]) null);
    }
    
    public Heracles(Object object, String... onlyTheseFields) throws PanteonException {
        this(object, null, (onlyTheseFields == null) ? null : Arrays.asList(onlyTheseFields));
    }
    
    private Heracles(Object object, Map<Object, Object> formattedValues, List<String> onlyTheseFields) throws PanteonException {
        if (object == null) {
            return;
        }
        
        if (object instanceof Collection || object instanceof Map || object.getClass().isArray()) {
            throw new PanteonException(CodeError.HERACLES_FAIL, "cannot create heracles from Collection or Map or Array");
        }
        
        if (formattedValues == null) {
            formattedValues = new HashMap<>();
        }
        formattedValues.put(object, this);
        buildHeracles(object, formattedValues, onlyTheseFields);
    }
    
    private void buildHeracles(Object object, Map<Object, Object> formattedValues, List<String> onlyTheseFields) throws PanteonException {
        try {
            if (object.getClass().isArray()) {
                throw new PanteonException(CodeError.HERACLES_FAIL, "Processing arrays are not available, use any Iterable class.");
            }
            
            final HashMap<String, Object> localValues = new HashMap<>();
            final List<Field> fieldClasses = ReflectionHelper.getPlainFields(object.getClass()).collect(Collectors.toList());
            
            for (Field field : fieldClasses) {
                // IGNORE ENTITY LAZY LOADING BY DEFAULT
                // Skip JPA annotations if they exist
                field.setAccessible(true);
                String name = field.getName();
                if (onlyTheseFields != null && !onlyTheseFields.contains(name)) {
                    continue;
                }
                Object value = field.get(object);
                localValues.put(name, formatValue(value, formattedValues));
            }
            values.putAll(localValues);
        } catch (PanteonException e) {
            throw e;
        } catch (Throwable e) {
            throw new PanteonException(CodeError.HERACLES_FAIL, "Heracle object initialization error", e);
        }
    }
    
    private List<Object> getFormattedList(Iterable<Object> objects, Map<Object, Object> formattedValues) throws PanteonException {
        if (objects == null) {
            return null;
        }
        List<Object> formattedList = new ArrayList<>();
        for (Object value : objects) {
            formattedList.add(formatValue(value, formattedValues));
        }
        return formattedList;
    }
    
    private Map<Object, Object> getFormattedMap(Map<Object, Object> map, Map<Object, Object> formattedValues) throws PanteonException {
        if (map == null) {
            return null;
        }
        
        Map<Object, Object> formattedMap;
        if (map instanceof SortedMap) {
            formattedMap = new TreeMap<>();
        } else if (map instanceof LinkedHashMap) {
            formattedMap = new LinkedHashMap<>();
        } else {
            formattedMap = new HashMap<>();
        }
        
        for (Map.Entry<Object, Object> keyVal : map.entrySet()) {
            Object key = keyVal.getKey();
            Class<?> keyClass = key.getClass();
            if (!isSimpleType(keyClass) && !keyClass.isEnum()) {
                throw new PanteonException(CodeError.HERACLES_FAIL, "Wrong type(" + keyClass.getSimpleName() + ") for key in the map.");
            }
            
            Object val = keyVal.getValue();
            formattedMap.put(key, formatValue(val, formattedValues));
        }
        return formattedMap;
    }
    
    private Object formatValue(Object value, Map<Object, Object> formattedValues) throws PanteonException {
        if (value == null) {
            return null;
        }
        if (formattedValues != null && formattedValues.containsKey(value)) {
            return formattedValues.get(value);
        }
        
        Class<?> clazz = value.getClass();
        if (clazz.isEnum() || value instanceof Enum) {
            return ((Enum<?>) value).name();
        }
        if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) value;
            return getFormattedMap(map, formattedValues);
        }
        if (value instanceof Iterable) {
            @SuppressWarnings("unchecked")
            Iterable<Object> iterable = (Iterable<Object>) value;
            return getFormattedList(iterable, formattedValues);
        }
        if (isSimpleType(clazz)) {
            return value;
        }
        if (value instanceof byte[]) {
            return Base64.getEncoder().encodeToString((byte[]) value);
        }
        return new Heracles(value, formattedValues, null);
    }
    
    private boolean isSimpleType(Class<?> clazz) {
        return ESimpleTypes.contains(clazz);
    }
    
    public Heracles setValue(String key, Object value) throws PanteonException {
        values.put(key, formatValue(value, null));
        return this;
    }
    
    public Map<String, Object> getValues() { 
        return values; 
    }
    
    public PanteonException getError() { 
        return (error == null) ? null : error.asPanteonException(); 
    }
    
    public Heracles setError(PanteonException error) {
        this.error = (error == null) ? null : new ErrorInfo(error);
        return this;
    }
    
    /**
     * Get value by key. If key doesn't exist - throw {@link PanteonException}.
     * If key exists but value is null - return {@code defaultValue}.
     */
    public <T> T getValueDef(String key, T defaultValue) throws PanteonException {
        if (error != null) {
            throw error.asPanteonException();
        }
        return getValueDefSafe(key, defaultValue);
    }
    
    /**
     * Get value by key. If key doesn't exist - return {@code defaultValue}.
     * If key exists but value is null - return {@code defaultValue}.
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueDefSafe(String key, T defaultValue) {
        if (values.containsKey(key)) {
            T t = (T) values.get(key);
            return t;
        }
        return defaultValue;
    }
    
    /**
     * Get value by key. If key doesn't exist - return {@code defaultValue}.
     * If key exists but value is null - return {@code defaultValue}.
     */
    public <T> T getValueDefNull(String key, T defaultValue) throws PanteonException {
        T ret = getValueDef(key, defaultValue);
        if (ret == null) {
            ret = defaultValue;
        }
        return ret;
    }
    
    /**
     * Get value by key. If key doesn't exist - return {@code defaultValue}.
     * If key exists but value is null or empty - return {@code defaultValue}.
     */
    public String getValueDefEmpty(String key, String defaultValue) throws PanteonException {
        String ret = getValueDef(key, defaultValue);
        if (ret == null || ret.isEmpty()) {
            ret = defaultValue;
        }
        return ret;
    }
    
    /**
     * Get value by key. If key doesn't exist - throw {@link PanteonException}.
     */
    public <T> T getValue(String key) throws PanteonException {
        if (error != null) {
            throw error.asPanteonException();
        }
        return getValueSafe(key);
    }
    
    /**
     * Get value by key. If key doesn't exist - throw {@link PanteonException},
     * even if it exists.
     */
    public byte[] getValueBytes(String key) throws PanteonException {
        if (error != null) {
            throw error.asPanteonException();
        }
        String val = getValueSafe(key);
        if (val == null) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(val);
        } catch (Exception ex) {
            throw new PanteonException(CodeError.HERACLES_FAIL, ex);
        }
    }
    
    /**
     * Get value by key. If key doesn't exist - throw {@link PanteonException}.
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueSafe(String key) throws PanteonException {
        if (values.containsKey(key)) {
            T t = (T) values.get(key);
            return t;
        }
        throw new PanteonException(CodeError.HERACLES_FAIL, "Requested parameter is not set by key '" + key + "'.");
    }
    
    public boolean hasValue(String key) { 
        return values.containsKey(key); 
    }
    
    public boolean hasError() { 
        return error != null; 
    }
    
    public boolean isEmpty() { 
        return values.isEmpty(); 
    }
    
    /**
     * Return shadow string (hide provided fields)
     */
    @Override
    public String toString() {
        return HeraclesHelper.toString(this);
    }
    
    @Override
    public int hashCode() {
        if (recursiveObjHash != null) {
            return recursiveObjHash.intValue();
        }
        
        try {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((error == null) ? 0 : error.hashCode());
            recursiveObjHash = result;
            result = prime * result + ((values == null) ? 0 : values.hashCode());
            return result;
        } finally {
            recursiveObjHash = null;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        if (recursiveObjEquals != null) {
            return obj == recursiveObjEquals;
        }
        
        recursiveObjEquals = obj;
        Heracles other = (Heracles) obj;
        try {
            return ((this.error == null) ? (other.error == null) : this.error.equals(other.error))
                && ((this.values == null) ? (other.values == null) : this.values.equals(other.values));
        } finally {
            recursiveObjEquals = null;
        }
    }
    
    private enum ESimpleTypes {
        HERACLES(Heracles.class),
        STRING(String.class),
        LONG(Long.class),
        DOUBLE(Double.class),
        INTEGER(Integer.class),
        FLOAT(Float.class),
        BIGDECIMAL(BigDecimal.class),
        DATE(Date.class),
        TIMESTAMP(java.sql.Timestamp.class),
        DATESQL(java.sql.Date.class),
        TIMESQL(java.sql.Time.class),
        BOOL(Boolean.class),
        GREGORIANCALENDAR(GregorianCalendar.class),
        PROPERTIES(Properties.class),
        CHAR(Character.class),
        SHORT(Short.class),
        BYTE(Byte.class);
        
        private Class<?> type;
        
        ESimpleTypes(Class<?> type) { 
            this.type = type; 
        }
        
        public static boolean contains(Class<?> clazz) {
            for (ESimpleTypes e : values()) {
                if (e.type == clazz) {
                    return true;
                }
            }
            return false;
        }
    }
}
