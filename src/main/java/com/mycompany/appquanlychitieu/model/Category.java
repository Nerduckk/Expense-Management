/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;

/**
 *
 * @author Hoang
 */
import java.math.BigDecimal;

public class Category extends BaseEntity {
    private CategoryType type;
    private String icon;
    private String color;
    private BigDecimal budgetLimit;

    public Category() {}

    public Category(String name, CategoryType type, String icon, String color) {
        this.setName(name);
        this.type = type;
        this.icon = icon;
        this.color = color;
    }

    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public BigDecimal getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; }
}
