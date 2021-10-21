package com.isi.isicashierlibrary;

import com.google.gson.annotations.SerializedName;
import com.isi.isiapi.general.classes.BillProduct;
import com.isi.isiapi.general.classes.Discount;

import java.util.ArrayList;

public class Bill {

    @SerializedName("products")
    private final ArrayList<BillProduct> products = new ArrayList<>();
    @SerializedName("discount")
    private Discount discount;

    public void addProduct(BillProduct product){

        this.products.add(product);

    }

    public ArrayList<BillProduct> getProducts() {
        return products;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }
}
