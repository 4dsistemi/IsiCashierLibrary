package com.isi.isicashierlibrary.printer.classes;

public class RepartoQuantity{

    public int reparto;
    public float quantity;

    public RepartoQuantity(int reparto, float quantity) {
        this.reparto = reparto;
        this.quantity = quantity;
    }

    public void updateQuantity(float quantity){
        this.quantity += quantity;
    }
}
