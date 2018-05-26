package com.edwardvanraak.medicalapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.adaptor.MedicalPrescriptionNewAdapter;
import com.edwardvanraak.medicalapp.model.MedicalBillsModel;
import com.edwardvanraak.medicalapp.model.MedicalPrescriptionModel;
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
 * Developer :- Ganesh Kulkarni.
 */

public class MedicalDiseaseDetailsActivity extends AppCompatActivity {

    String str_dieseaseId;
    TextView tv_diseasename;
    RecyclerView recyler_presimag, recyler_billsimag;
    private ArrayList<MedicalPrescriptionModel> data;
    private ArrayList<MedicalBillsModel> medicalBillsModels;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView tv_fromdate, tv_todate;
    ImageView iv_back;
    SharedPreferences pref;
    String user_Id, str_year;
    ProgressDialog progress;
    TextView tv_doctorname;
    ArrayList<String> medical_reports;
    ArrayList<String> medical_Bills;
    ImageView iv_bill, iv_medicalpresc;
    RelativeLayout rl_medicalpresc, rl_bills;
    String isClickMed = "1", isclickBills = "1";
    CardView card_bills, card_medical;
    String str_todate, str_fromdate, diesase_Name, doctor_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dieses_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        recyler_presimag = (RecyclerView) findViewById(R.id.recyler_presimag);
        recyler_billsimag = (RecyclerView) findViewById(R.id.recyler_billsimag);

        tv_fromdate = (TextView) findViewById(R.id.tv_durationfrom);
        tv_todate = (TextView) findViewById(R.id.tv_durationto);
        tv_doctorname = (TextView) findViewById(R.id.tv_doctorname);
        tv_diseasename = (TextView) findViewById(R.id.tv_diseasename);

        iv_medicalpresc = (ImageView) findViewById(R.id.iv_medicalpresc);
        iv_bill = (ImageView) findViewById(R.id.iv_bill);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        rl_medicalpresc = (RelativeLayout) findViewById(R.id.rl_medicalpresc);
        rl_bills = (RelativeLayout) findViewById(R.id.rl_bills);

        card_medical = (CardView) findViewById(R.id.card_medical);
        card_bills = (CardView) findViewById(R.id.card_bills);

        setSupportActionBar(toolbar);

        progress = new ProgressDialog(MedicalDiseaseDetailsActivity.this);
        progress.setMessage("Please wait...");
        pref = getApplicationContext().getSharedPreferences(Constants.SHAREPREFERNCE, MODE_PRIVATE);
        user_Id = pref.getString(Constants.USER_ID, null);
        str_year = pref.getString(Constants.YEAR, null);
      /*  data = fill_with_data();
        medicalBillsModels = fill_with_datBillsModels();*/




        if (getIntent().hasExtra(Constants.DISEASE_ID)) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                str_dieseaseId = bundle.getString(Constants.DISEASE_ID);

            } else {
                Log.e("null", "null");
            }
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getDetailsofDeisease(user_Id, str_year, str_dieseaseId);

        LinearLayoutManager layoutManagerGPS = new LinearLayoutManager(MedicalDiseaseDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyler_presimag.setLayoutManager(layoutManagerGPS);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MedicalDiseaseDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyler_billsimag.setLayoutManager(linearLayoutManager);

        rl_medicalpresc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClickMed.equals("1")) {
                    iv_medicalpresc.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                    isClickMed = "0";
                    card_medical.setVisibility(View.VISIBLE);
                } else {
                    iv_medicalpresc.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                    isClickMed = "1";
                    card_medical.setVisibility(View.GONE);

                }
            }
        });

        rl_bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isclickBills.equals("1")) {
                    iv_bill.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                    isclickBills = "0";
                    card_bills.setVisibility(View.VISIBLE);
                } else {
                    iv_bill.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                    isclickBills = "1";
                    card_bills.setVisibility(View.GONE);

                }
            }
        });

    }


    public void getDetailsofDeisease(final String user_Id, final String str_year, final String str_dieseaseId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConstants.MEDICAL_DISEASE_DETAILS_URL,
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

                            medical_Bills = new ArrayList<>();
                            medical_reports = new ArrayList<>();
                            System.out.println("flag1234234234...." + flag);
                            if (flag == 1) {

                                JSONArray jsonArray = jsonObject1response.getJSONArray("list");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    str_todate = jsonObject.getString("duration_to");
                                    str_fromdate = jsonObject.getString("duration_from");
                                    diesase_Name = jsonObject.getString("diease_name");
                                    doctor_name = jsonObject.getString("doctor_name");
                                    JSONArray jsonArrayMedicalReports = jsonObject.getJSONArray("medical_report");
                                    for (int j = 0; j < jsonArrayMedicalReports.length(); j++) {
                                        String images = jsonArrayMedicalReports.getString(j);
                                        medical_reports.add(images);

                                    }
                                    JSONArray jsonArrayMedicalbills = jsonObject.getJSONArray("medical_bill");
                                    for (int k = 0; k < jsonArrayMedicalbills.length(); k++) {
                                        String images = jsonArrayMedicalbills.getString(k);
                                        medical_Bills.add(images);


                                    }


                                }
                                MedicalPrescriptionNewAdapter adapter = new MedicalPrescriptionNewAdapter(medical_reports, MedicalDiseaseDetailsActivity.this);
                                recyler_presimag.setAdapter(adapter);
                                MedicalPrescriptionNewAdapter medicalPrescriptionNewAdapter = new MedicalPrescriptionNewAdapter(medical_Bills, MedicalDiseaseDetailsActivity.this);
                                recyler_billsimag.setAdapter(medicalPrescriptionNewAdapter);
                                tv_todate.setText(str_todate);
                                tv_fromdate.setText(str_fromdate);
                                tv_doctorname.setText(doctor_name);
                                tv_diseasename.setText(diesase_Name);
                            } else {
                                System.out.println("flag123...." + flag);

                                AlertDialog.Builder builder = new AlertDialog.Builder(MedicalDiseaseDetailsActivity.this);
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
                        String reason = AppUtils.getVolleyError(MedicalDiseaseDetailsActivity.this, error);
                        showAlertDialog(reason);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(Constants.USER_ID, user_Id);
                    params.put(Constants.YEAR, str_year);
                    params.put(Constants.DISEASE_ID, str_dieseaseId);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MedicalDiseaseDetailsActivity.this);
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

