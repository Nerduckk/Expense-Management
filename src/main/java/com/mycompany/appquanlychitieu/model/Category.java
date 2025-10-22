/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.appquanlychitieu.model;

/**
 *
 * @author Duck
 */
public class Category {
     private int id;
    private String name;
    private String description;

    public Category() {}

    public Category(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public void createCategory(String name, String des) {
        this.name = name;
        this.description = des;
        System.out.println("Category created: " + name);
    }

    public void updateDetails(String new_name, String new_des) {
        this.name = new_name;
        this.description = new_des;
        System.out.println("Category updated: " + new_name);
    }

    public double getTotalSpent(String startDate, String endDate) {
        // Giả lập tính tổng chi tiêu trong khoảng thời gian
        System.out.println("Calculating total spent from " + startDate + " to " + endDate);
        return 0.0;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
