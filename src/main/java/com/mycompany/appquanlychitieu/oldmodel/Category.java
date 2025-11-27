package com.mycompany.appquanlychitieu.oldmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Category {
    private static int seq = 0;

    private int id;
    private String name;
    private String description;

    public Category() {
        this.id = ++seq;
        this.name = "";
        this.description = "";
    }

    public Category(int id, String name, String description) {
        setId(id);
        setName(name);
        setDescription(description);
        if (id > seq) seq = id;
    }

    public Category(String name, String description) {
        this.id = ++seq;
        setName(name);
        setDescription(description);
    }

    public int getId() { return id; }
    public void setId(int id) {
        if (id <= 0) throw new IllegalArgumentException("id phải > 0");
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name trống");
        this.name = name.trim();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = (description == null) ? "" : description.trim();
    }

    public void createCategory(String name, String des) {
        setName(name);
        setDescription(des);
    }

    public void updateDetails(String new_name, String new_des) {
        setName(new_name);
        setDescription(new_des);
    }

    public BigDecimal getTotalSpent(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate))
            throw new IllegalArgumentException("Khoảng ngày không hợp lệ");
        return BigDecimal.ZERO;
    }

    public double getTotalSpent(String startDate, String endDate) {
        return 0.0;
    }

    public void deleteCategory() {
        this.name = "";
        this.description = "";
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
