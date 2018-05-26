package com.edwardvanraak.medicalapp.adaptor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.model.MedicalPrescriptionModel;

import java.util.ArrayList;

/**
 * Created by hnwebmarketing on 2/7/2018.
 */

public class MedicalPrescriptionAdapter extends RecyclerView.Adapter<MedicalPrescriptionAdapter.MyViewHolder> {

    private ArrayList<MedicalPrescriptionModel> diseaseModels;
    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_prescimag;

        public MyViewHolder(View view) {
            super(view);
            iv_prescimag = (ImageView) view.findViewById(R.id.iv_prescimag);

        }
    }


    public MedicalPrescriptionAdapter(ArrayList<MedicalPrescriptionModel> productModelArrayList, Context context) {
        this.diseaseModels = productModelArrayList;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_medical_prescrpimag, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final MedicalPrescriptionModel diseaseModel = diseaseModels.get(position);

        holder.iv_prescimag.setImageResource(diseaseModel.getPrescimag());

    }


    @Override
    public int getItemCount() {
        return diseaseModels.size();
    }

}

