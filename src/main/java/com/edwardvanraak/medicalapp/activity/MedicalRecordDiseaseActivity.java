package com.edwardvanraak.medicalapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.adaptor.MedicalDieaseAdapter;
import com.edwardvanraak.medicalapp.model.DiseaseModel;
import com.edwardvanraak.medicalapp.utils.AppConstants;
import com.edwardvanraak.medicalapp.utils.AppUtils;
import com.edwardvanraak.medicalapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by neha on 3/17/2018..
 */

public class MedicalRecordDiseaseActivity extends AppCompatActivity {
    RecyclerView recycler_view;
    ArrayList<DiseaseModel> diseaseModels;
    String[] diseases = {
            "Cardiology",
            "Dental",
            "Shoulder Dislocate",
            "Diabetes",
    };
    String str_year;
    Button btn_year;
    ProgressDialog progress;
    String user_Id;
    SharedPreferences pref;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diesese_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_year = (Button) findViewById(R.id.btn_year);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        setSupportActionBar(toolbar);

        progress = new ProgressDialog(MedicalRecordDiseaseActivity.this);
        progress.setMessage("Please wait...");
       /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        pref = getApplicationContext().getSharedPreferences(Constants.SHAREPREFERNCE, MODE_PRIVATE);
        user_Id = pref.getString(Constants.USER_ID, null);
/*
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
*/


/*
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
*/
        if (getIntent().hasExtra("Year")) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                str_year = bundle.getString("Year");

            } else {
                Log.e("null", "null");
            }
        }
        btn_year.setText(str_year);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MedicalRecordDiseaseActivity.this, LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(layoutManager);
        diseaseModels = new ArrayList<>();
        LinearLayoutManager layoutManagerGPS = new LinearLayoutManager(MedicalRecordDiseaseActivity.this, LinearLayoutManager.VERTICAL, false);

        recycler_view.setLayoutManager(layoutManagerGPS);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
       /* for(int i=0;i<diseases.length;i++) {
            DiseaseModel diseaseModel=new DiseaseModel();
            diseaseModel.setDiesease_name(diseases[i]);
            diseaseModels.add(diseaseModel);
        }
        MedicalDieaseAdapter adapter = new MedicalDieaseAdapter(diseaseModels, MedicalRecordDiseaseActivity.this);
        recycler_view.setAdapter(adapter);*/

        getDiseaseList(user_Id, str_year);
    }

    public void getDiseaseList(final String user_Id, final String str_year) {
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConstants.MEDICAL_DISEASE_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response123...." + response);
                        progress.dismiss();
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            JSONObject jsonObject1response = jsonObject1.getJSONObject(Constants.RESPONSE);
                            String message = jsonObject1response.getString(Constants.MESSAGE);
                            int flag = jsonObject1response.getInt(Constants.FLAG);

                            System.out.println("flag1234234234...." + flag);
                            if (flag == 1) {
                                diseaseModels = new ArrayList<>();

                                JSONArray jsonArray = jsonObject1response.getJSONArray("list");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    DiseaseModel medicalYearRecordModel = new DiseaseModel();
                                    medicalYearRecordModel.setDiesease_name(jsonObject.getString("dieseasename"));
                                    medicalYearRecordModel.setDiesease_id(jsonObject.getString("dieseaseid"));

                                    diseaseModels.add(medicalYearRecordModel);
                                    MedicalDieaseAdapter adapter = new MedicalDieaseAdapter(diseaseModels, MedicalRecordDiseaseActivity.this);
                                    recycler_view.setAdapter(adapter);
                                }
                            } else {
                                System.out.println("flag123...." + flag);

                                AlertDialog.Builder builder = new AlertDialog.Builder(MedicalRecordDiseaseActivity.this);
                                builder.setMessage(message);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                android.support.v7.app.AlertDialog alertDialog = builder.create();
                                alertDialog.show();                             // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("JsonException" + e);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("jsonexeption" + error.toString());
                        progress.dismiss();
                        String reason = AppUtils.getVolleyError(MedicalRecordDiseaseActivity.this, error);
                        showAlertDialog(reason);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(Constants.USER_ID, user_Id);
                    params.put(Constants.YEAR, str_year);
                } catch (Exception e) {
                    System.out.println("medicalapp" + e.toString());
                }
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        stringRequest.setShouldCache(false);
    }

    private void showAlertDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MedicalRecordDiseaseActivity.this);
        builder.setTitle("")
                .setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
