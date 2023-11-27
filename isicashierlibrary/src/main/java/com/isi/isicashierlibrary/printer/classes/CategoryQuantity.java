package com.isi.isicashierlibrary.printer.classes;

import com.isi.isiapi.classes.Category;

public class CategoryQuantity {

    private final Category category;
    private float total;

    public CategoryQuantity(Category category, float total) {
        this.category = category;
        this.total = total;
    }

    public void increaseTotal(float total){
        this.total += total;
    }

    public Category getCategory_id() {
        return category;
    }

    public float getTotal() {
        return total;
    }
}
