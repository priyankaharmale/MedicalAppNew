package com.edwardvanraak.medicalapp.adaptor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.activity.MedicalDiseaseDetailsActivity;
import com.edwardvanraak.medicalapp.model.DiseaseModel;
import com.edwardvanraak.medicalapp.utils.Constants;

import java.util.ArrayList;

/**
 * Created by hnwebmarketing on 2/7/2018.
 */

public class MedicalDieaseAdapter extends RecyclerView.Adapter<MedicalDieaseAdapter.MyViewHolder> {

    private ArrayList<DiseaseModel> diseaseModels;
    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_diseasename;

        public MyViewHolder(View view) {
            super(view);
            tv_diseasename = (TextView) view.findViewById(R.id.tv_diseasename);

        }
    }


    public MedicalDieaseAdapter(ArrayList<DiseaseModel> productModelArrayList, Context context) {
        this.diseaseModels = productModelArrayList;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adaptor_medical_diesea, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final DiseaseModel diseaseModel = diseaseModels.get(position);

        holder.tv_diseasename.setText(diseaseModel.getDiesease_name());
       /* holder.tv_gps_for.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(AppConstant.KEY_GPS_FOR, productModel.getGps_name());

                Fragment fragment_profile = new GPSForLifeFragment();
                fragment_profile.setArguments(bundle);
                FragmentTransaction ft = ((DashBoardActivity)context).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment_profile);
                ft.addToBackStack(fragment_profile.getClass().getName());
                ft.commit();
            }
        });*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(context, MedicalDiseaseDetailsActivity.class);
                Bundle bundleObject = new Bundle();
                bundleObject.putString(Constants.DISEASE_ID, diseaseModel.getDiesease_id());
                intent1.putExtras(bundleObject);
                context.startActivity(intent1);
            }
        });
    }


    @Override
    public int getItemCount() {
        return diseaseModels.size();
    }

}

