package com.example.shohel.geopoll.classes;

/**
 * Created by BDDL-102 on 11/19/2018.
 */

public class TransactionData {
    private String paymentMethod;
    private String amount;
    private String paymentStatus;
    private String paymentTime;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getAmount() {
        return amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentMethod(String paymentMethod) {

        this.paymentMethod = paymentMethod;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }
}
