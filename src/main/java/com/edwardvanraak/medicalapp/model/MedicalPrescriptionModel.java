package com.edwardvanraak.medicalapp.model;

/**
 * Created by neha on 3/19/2018..
 * Developer :- Ganesh Kulkarni.
 */

public class MedicalPrescriptionModel {
    private String presc_id;
private int prescimag;
    private int medicalImages;

    public MedicalPrescriptionModel(int imag1) {
        this.prescimag=imag1;
    }

    public String getPresc_id() {
        return presc_id;
    }

    public void setPresc_id(String presc_id) {
        this.presc_id = presc_id;
    }

    public int getPrescimag() {
        return prescimag;
    }

    public int getMedicalImages() {
        return medicalImages;
    }

    public void setMedicalImages(int medicalImages) {
        this.medicalImages = medicalImages;
    }

    public void setPrescimag(int prescimag) {
        this.prescimag = prescimag;
    }
}
