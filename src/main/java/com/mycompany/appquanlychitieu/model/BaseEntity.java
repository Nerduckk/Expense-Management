package com.mycompany.appquanlychitieu.model;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 *
 * @author Duck
 */
public abstract class BaseEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected Long id;
    protected String name;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    
    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BaseEntity(Long id, String name) {
        this.id = id;
        setName(name); 
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten khong duoc de trong.");
        }
        this.name = name.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
