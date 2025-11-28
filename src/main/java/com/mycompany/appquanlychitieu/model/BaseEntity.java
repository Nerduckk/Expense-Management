package com.mycompany.appquanlychitieu.model;
import java.time.LocalDateTime;
/**
 *
 * @author Duck
 */

public abstract class BaseEntity {
    protected Long id;
    protected String name;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

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
            throw new IllegalArgumentException("Tên không được để trống.");
        }
        this.name = name.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
