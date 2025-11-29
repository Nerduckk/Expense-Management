package com.mycompany.appquanlychitieu.service;

import com.mycompany.appquanlychitieu.model.Category;
import java.util.List;

public class CategoryService {

    public List<Category> getAll() {
        return DataStore.categories;
    }

    public void add(Category c) {
        DataStore.categories.add(c);
        DataStore.saveData();
    }

    public void delete(Category c) {
        DataStore.categories.remove(c);
        DataStore.saveData();
    }

    public void saveChanges() {
        DataStore.saveData();
    }
}
