package com.edwardvanraak.medicalapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.adaptor.MedicalRecordYearAdaptor;
import com.edwardvanraak.medicalapp.model.MedicalYearRecordModel;

import java.util.ArrayList;

/**
 * Created by Shree on 24-Feb-18.
 */

public class MedicalRecordFragment extends Fragment {

    RelativeLayout rl_back;
    GridView simpleGrid;
    ArrayList<MedicalYearRecordModel> medicalYearRecordModels;
    String[] year = {
            "2014",
            "2015",
            "2016",
            "2017",
            "2018",
            "2019"
    };
    private SettingsFragment.OnFragmentInteractionListener mListener;

    public MedicalRecordFragment() {
    }
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_medicalrecordnw, container, false);

        rl_back=(RelativeLayout) rootView.findViewById(R.id.rl_back);
        medicalYearRecordModels=new ArrayList<>();
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment fragment = new HomeFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations( R.anim.slide_left,R.anim.slide_right
                );
                fragmentTransaction.replace(R.id.frame, fragment, "");
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        simpleGrid = (GridView) rootView.findViewById(R.id.simpleGridView); // init GridView
        // Create an object of CustomAdapter and set Adapter to GirdView

        for(int i=0;i<year.length;i++) {
            MedicalYearRecordModel medicalYearRecordModel=new MedicalYearRecordModel();
            medicalYearRecordModel.setYear_name(year[i]);
            medicalYearRecordModels.add(medicalYearRecordModel);
        }
        MedicalRecordYearAdaptor customAdapter = new MedicalRecordYearAdaptor(getActivity(), medicalYearRecordModels);
        simpleGrid.setAdapter(customAdapter);
        // implement setOnItemClickListener event on GridView
        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set an Intent to Another Activity
               /* Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("image", logos[position]); // put image data in Intent
                startActivity(intent);*/ // start Intent
            }
        });
        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public void onBackPressed()
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }
    }
