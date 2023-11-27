package com.isi.isicashierlibrary.printer.classes;

import java.util.ArrayList;

public class PaymentType{

    public String paymentType;
    public float quantity;

    public PaymentType(String paymentType, float quantity) {
        this.paymentType = paymentType;
        this.quantity = quantity;
    }

    public void updateQuantity(float quantity){
        this.quantity += quantity;
    }

    public static ArrayList<Payment> createPayments() {
        ArrayList<Payment> payments = new ArrayList();
        payments.add(new Payment("T1", "T1", "Contanti"));
        payments.add(new Payment("T4", "T4", "POS"));
        payments.add(new Payment("T4", "T8", "SatisPay"));
        payments.add(new Payment("T2", "T2", "Non riscosso"));
        payments.add(new Payment("T3", "T3", "Assegno"));
        payments.add(new Payment("T5", "T5", "Non stampare"));
        payments.add(new Payment("T6", "T6", "Non fiscale"));
        return payments;
    }
}
