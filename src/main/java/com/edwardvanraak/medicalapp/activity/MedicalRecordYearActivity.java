package com.edwardvanraak.medicalapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.adaptor.MedicalRecordYearAdaptor;
import com.edwardvanraak.medicalapp.model.MedicalYearRecordModel;
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
 * Created by Shree on 24-Feb-18.
 */

public class MedicalRecordYearActivity extends AppCompatActivity {


    GridView simpleGrid;
    ArrayList<MedicalYearRecordModel> medicalYearRecordModels;
    ArrayList<MedicalYearRecordModel> medicalYearRecordModesnew;
    EditText et_year;
    ImageView iv_back;
    String user_Id;
    SharedPreferences pref;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicalrecordnw);
        simpleGrid = (GridView) findViewById(R.id.simpleGridView); // init GridView
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        setSupportActionBar(toolbar);


        et_year = (EditText) findViewById(R.id.et_year);
        pref = getApplicationContext().getSharedPreferences(Constants.SHAREPREFERNCE, MODE_PRIVATE);
        user_Id = pref.getString(Constants.USER_ID, null);
        System.out.println("USER_ID" + user_Id);
        progress = new ProgressDialog(MedicalRecordYearActivity.this);
        progress.setMessage("Please wait...");
        getYearList(user_Id);

        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        et_year.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                medicalYearRecordModesnew = new ArrayList<MedicalYearRecordModel>();
                //  progress.show();
                for (int i = 0; i < medicalYearRecordModels.size(); i++) {
                    MedicalYearRecordModel medicalYearRecordModel = new MedicalYearRecordModel();
                    if (medicalYearRecordModels.get(i).getYear_name().contains(s)) {
                        //     progress.dismiss();
                        medicalYearRecordModel.setYear_name(medicalYearRecordModels.get(i).getYear_name());
                        medicalYearRecordModesnew.add(medicalYearRecordModel);
                        MedicalRecordYearAdaptor customAdapter = new MedicalRecordYearAdaptor(MedicalRecordYearActivity.this, medicalYearRecordModesnew);
                        simpleGrid.setAdapter(customAdapter);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
              /*  if (s.length() != 0)
                    et_year.setText("");*/
            }
        });
       /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
   */
    }


    public void getYearList(final String user_Id) {
        progress.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConstants.MEDICAL_YEAR_LIST_URL,
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
                                medicalYearRecordModels = new ArrayList<>();

                                JSONArray jsonArray = jsonObject1response.getJSONArray("yearlist");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    MedicalYearRecordModel medicalYearRecordModel = new MedicalYearRecordModel();
                                    medicalYearRecordModel.setYear_name(jsonArray.getString(i));
                                    medicalYearRecordModels.add(medicalYearRecordModel);
                                    MedicalRecordYearAdaptor customAdapter = new MedicalRecordYearAdaptor(MedicalRecordYearActivity.this, medicalYearRecordModels);
                                    simpleGrid.setAdapter(customAdapter);
                                }
                            } else {
                                System.out.println("flag123...." + flag);

                                AlertDialog.Builder builder = new AlertDialog.Builder(MedicalRecordYearActivity.this);
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
                        String reason = AppUtils.getVolleyError(MedicalRecordYearActivity.this, error);
                        showAlertDialog(reason);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(Constants.USER_ID, user_Id);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MedicalRecordYearActivity.this);
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
