package com.isi.isicashierlibrary.printer.classes;

import com.isi.isiapi.classes.isicash.IsiCashElementBill;

public class ElementBillQuantity{

    private final IsiCashElementBill elementBill;

    private int quantity = 1;
    private float price;

    public ElementBillQuantity(IsiCashElementBill elementBill, float price, int quantity) {
        this.elementBill = elementBill;
        this.price = price;
        this.quantity = quantity;
    }

    public IsiCashElementBill getElementBill() {
        return elementBill;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity(int quantity){
        this.quantity += quantity;
    }

    public void increasePrice(float price){
        this.price += price;
    }

    public float getPrice() {
        return price;
    }
}
