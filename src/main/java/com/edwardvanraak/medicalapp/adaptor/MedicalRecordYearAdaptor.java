package com.edwardvanraak.medicalapp.adaptor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.activity.MedicalRecordDiseaseActivity;
import com.edwardvanraak.medicalapp.model.MedicalYearRecordModel;
import com.edwardvanraak.medicalapp.utils.Constants;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class MedicalRecordYearAdaptor extends BaseAdapter {
    Context context;
    ArrayList<MedicalYearRecordModel> medicalYearRecordModes;
    LayoutInflater inflter;
    LinearLayout ll_top;
    ImageView iv_folder;

    public MedicalRecordYearAdaptor(Context context, ArrayList<MedicalYearRecordModel> medicalYearRecordModes) {
        this.context = context;
        this.medicalYearRecordModes = medicalYearRecordModes;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return medicalYearRecordModes.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.adaptor_yearrecord, null); // inflate the layout
        final MedicalYearRecordModel medicalYearRecordModel = medicalYearRecordModes.get(i);
        TextView tv_year = (TextView) view.findViewById(R.id.tv_year);
        ll_top = (LinearLayout) view.findViewById(R.id.ll_top); // get the reference of ImageView
        iv_folder = (ImageView) view.findViewById(R.id.iv_folder); // get the reference of ImageView
        tv_year.setText(medicalYearRecordModel.getYear_name()); // set logo images

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(context, MedicalRecordDiseaseActivity.class);

                Bundle bundleObject = new Bundle();
                bundleObject.putString("Year", medicalYearRecordModel.getYear_name());
                SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Constants.SHAREPREFERNCE, MODE_PRIVATE);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constants.YEAR, medicalYearRecordModel.getYear_name());
                editor.commit();
                intent1.putExtras(bundleObject);
                context.startActivity(intent1);
            }
        });
        return view;
    }
}
