package com.edwardvanraak.medicalapp.model;

/**
 * Created by neha on 3/19/2018..
 * Developer :- Ganesh Kulkarni.
 */

public class MedicalBillsModel {
    private String presc_id;
    private int billsimag;

    public MedicalBillsModel(int imag1) {
        this.billsimag =imag1;
    }

    public String getPresc_id() {
        return presc_id;
    }

    public void setPresc_id(String presc_id) {
        this.presc_id = presc_id;
    }

    public int getBillsimag() {
        return billsimag;
    }

    public void setBillsimag(int billsimag) {
        this.billsimag = billsimag;
    }
}
