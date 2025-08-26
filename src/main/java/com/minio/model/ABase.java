package com.minio.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class ABase implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Дата создания записи
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_create")
    protected Date dateCreate = new Date();
    
    /**
     * Дата последней модификации записи
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update")
    protected Date lastUpdate = new Date();
    
    /**
     * Уникальный идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Long id;
    
    // Getters and setters
    
    public Date getDateCreate() {
        return dateCreate;
    }
    
    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }
    
    public Date getLastUpdate() {
        return lastUpdate;
    }
    
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdate = new Date();
    }
}
