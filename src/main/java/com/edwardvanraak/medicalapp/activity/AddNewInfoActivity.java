package com.edwardvanraak.medicalapp.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.edwardvanraak.medicalapp.MultipartRequest.MultiPart_Key_Value_Model;
import com.edwardvanraak.medicalapp.MultipartRequest.MultipartFileUploaderAsync;
import com.edwardvanraak.medicalapp.MultipartRequest.OnEventListener;
import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.adaptor.MedicalPrescriptionNewAdapter;
import com.edwardvanraak.medicalapp.utils.AppConstants;
import com.edwardvanraak.medicalapp.utils.Constants;
import com.edwardvanraak.medicalapp.utils.Utilities;
import com.edwardvanraak.medicalapp.utils.Validations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by neha on 3/20/2018..
 */

public class AddNewInfoActivity extends AppCompatActivity {

    EditText et_fromdate, et_todate, et_dieseaseName, et_doctorName;
    private int mYear, mMonth, mDay;
    ImageView iv_addnewReport, iv_addnewBills;
    ProgressDialog progressDialog;
    private ArrayList<String> mSelectPath;
    private ArrayList<String> mSelectBillsPath;
    private static final int REQUEST_IMAGE = 2;
    private static final int REQUESTBill_IMAGE = 3;
    Button btn_save;
    ImageView iv_back;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    String user_Id;
    MedicalPrescriptionNewAdapter adapter;
    SharedPreferences pref;
    RecyclerView recyler_presimag, recyler_billsimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addnewinfo);

        pref = getApplicationContext().getSharedPreferences(Constants.SHAREPREFERNCE, MODE_PRIVATE);
        user_Id = pref.getString(Constants.USER_ID, null);
        System.out.println("USER_ID" + user_Id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv_addnewReport = (ImageView) findViewById(R.id.iv_addnewReport);
        iv_addnewBills = (ImageView) findViewById(R.id.iv_addnewBills);
        iv_back = (ImageView) toolbar.findViewById(R.id.iv_back);
        et_fromdate = (EditText) findViewById(R.id.et_durationfrom);
        et_todate = (EditText) findViewById(R.id.et_durationto);
        et_dieseaseName = (EditText) findViewById(R.id.et_diseasename);
        et_doctorName = (EditText) findViewById(R.id.et_doctorname);
        recyler_presimag = (RecyclerView) findViewById(R.id.recyler_presimag);
        recyler_billsimage = (RecyclerView) findViewById(R.id.recyler_billsimages);
        btn_save = (Button) findViewById(R.id.btn_save);
        mSelectPath = new ArrayList<String>();
        mSelectBillsPath = new ArrayList<String>();

        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(AddNewInfoActivity.this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_dieseaseName.getText().toString().equals("") || !et_doctorName.getText().toString().equals("") ||
                        !et_fromdate.getText().toString().equals("") || !et_todate.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNewInfoActivity.this);
                    builder.setMessage("Are You Sure?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(AddNewInfoActivity.this, MainActivityNew.class);
                            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });
                    android.support.v7.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    onBackPressed();
                }            }
        });
        LinearLayoutManager layoutManagerMedicalReport = new LinearLayoutManager(AddNewInfoActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyler_presimag.setLayoutManager(layoutManagerMedicalReport);

        LinearLayoutManager layoutManagerBill = new LinearLayoutManager(AddNewInfoActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyler_billsimage.setLayoutManager(layoutManagerBill);

        et_fromdate.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    datePickerFrom();
                }
                return false;
            }
        });
        et_todate.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    datePickerTo();
                }
                return false;
            }
        });
        iv_addnewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        iv_addnewBills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickBillImage();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkValidation()) {
                    if (Utilities.isNetworkAvailable(AddNewInfoActivity.this)) {

                        if (mSelectPath.size() == 0) {
                            Toast.makeText(AddNewInfoActivity.this, "Please Add the Medical Report", Toast.LENGTH_SHORT).show();

                        } else if (mSelectBillsPath.size() == 0) {
                            Toast.makeText(AddNewInfoActivity.this, "Please Add the Bill", Toast.LENGTH_SHORT).show();

                        } else
                            sendData(mSelectPath, mSelectBillsPath);

                    } else {
                        Toast.makeText(getApplicationContext(), "Please Connect to internet", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }

    void datePickerFrom() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {


                System.out.println("Date.." + year + "-" + (dayOfMonth) + "-" + (monthOfYear + 1));

                String day_date;
                if (dayOfMonth < 10) {
                    day_date = "0" + dayOfMonth;
                } else {
                    day_date = String.valueOf(dayOfMonth);
                }
                int day_d = Integer.parseInt(day_date);


                int day_month = monthOfYear + 1;

                if (day_month < 10) {
                    //  tv_event_date.setText("0" + day_month + "/" + day_date + "/" + year);//11-20-2017
                    et_fromdate.setText(year + "-" + "0" + day_month + "-" + day_date);//11-20-2017

                    //  2018-02-22

                    et_fromdate.setError(null);
                    Log.d("date12", et_fromdate.getText().toString());
                } else {
                    // tv_event_date.setText(day_month + "/" + day_date + "/" + year);//11-20-2017
                    et_fromdate.setText(year + "-" + day_month + "-" + day_date);//11-20-2017

                    et_fromdate.setError(null);
                    Log.d("date12", et_fromdate.getText().toString());
                }

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();


    }

    void datePickerTo() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                System.out.println("Date.." + year + "-" + (dayOfMonth) + "-" + (monthOfYear + 1));
                // YYYY-MM-DD
                //  et_todate.setError(null);
                String day_date;
                if (dayOfMonth < 10) {
                    day_date = "0" + dayOfMonth;
                } else {
                    day_date = String.valueOf(dayOfMonth);
                }
                int day_d = Integer.parseInt(day_date);


                int day_month = monthOfYear + 1;

                if (day_month < 10) {
                    et_todate.setText(year + "-" + "0" + day_month + "-" + day_date);//11-20-2017
                    et_todate.setError(null);
                    Log.d("date12", et_todate.getText().toString());
                } else {
                    et_todate.setText(year + "-" + day_month + "-" + day_date);//11-20-2017

                    et_todate.setError(null);
                    Log.d("date12", et_todate.getText().toString());
                }

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();


    }


    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {

            int maxNum = 100;

            MultiImageSelector selector = MultiImageSelector.create(AddNewInfoActivity.this);


            selector.count(maxNum);

            selector.multi();

            selector.origin(mSelectPath);
            selector.start(AddNewInfoActivity.this, REQUEST_IMAGE);


        }
    }

    private void pickBillImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {

            int maxNum = 100;

            MultiImageSelector selector = MultiImageSelector.create(AddNewInfoActivity.this);


            selector.count(maxNum);

            selector.multi();

            selector.origin(mSelectBillsPath);
            selector.start(AddNewInfoActivity.this, REQUESTBill_IMAGE);


        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AddNewInfoActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }


    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("rescode" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {

                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);


                adapter = new MedicalPrescriptionNewAdapter(mSelectPath, AddNewInfoActivity.this);
                recyler_presimag.setAdapter(adapter);

                //  adapter.notifyDataSetChanged();

                StringBuilder sb = new StringBuilder();
                for (String p : mSelectPath) {
                    sb.append(p);
                    sb.append("\n");
                }
            }
        } else if (requestCode == REQUESTBill_IMAGE) {
            if (resultCode == RESULT_OK) {

                mSelectBillsPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);


                adapter = new MedicalPrescriptionNewAdapter(mSelectBillsPath, AddNewInfoActivity.this);
                recyler_billsimage.setAdapter(adapter);

                //  adapter.notifyDataSetChanged();

                StringBuilder sb = new StringBuilder();
                for (String p : mSelectBillsPath) {
                    sb.append(p);
                    sb.append("\n");
                }
            }
        }
    }


    public void sendData(ArrayList<String> mSelectPath, ArrayList<String> mSelectBillsPath) {
        progressDialog.setMessage("Saving Data..Please Wait.");
        progressDialog.show();

        MultiPart_Key_Value_Model OneObject = new MultiPart_Key_Value_Model();
        Map<String, String> fileParams = new HashMap<>();
        try {
            for (int i = 0; i < mSelectPath.size(); i++) {
                fileParams.put("medicalreport[" + i + "]", String.valueOf(mSelectPath.get(i)));
                System.out.println("priya Op" + "medicalreport[" + i + "]" + String.valueOf(mSelectPath));
            }
        } catch (Exception e) {
        }
        try {
            for (int i = 0; i < mSelectBillsPath.size(); i++) {
                fileParams.put("medicalbill[" + i + "]", String.valueOf(mSelectBillsPath.get(i)));
                System.out.println("priya Op" + "medicalbill[" + i + "]" + String.valueOf(mSelectBillsPath));
            }
        } catch (Exception e) {
        }
        Map<String, String> Stringparams = new HashMap<>();
        Stringparams.put(Constants.DIESEASE_NAME, et_dieseaseName.getText().toString());
        Stringparams.put(Constants.DOCTOR_NAME, et_doctorName.getText().toString());
        Stringparams.put(Constants.DURATION_FROM, et_fromdate.getText().toString());
        Stringparams.put(Constants.DURATION_TO, et_todate.getText().toString());
        Stringparams.put("userId", user_Id);
        OneObject.setUrl(AppConstants.ADD_NEW_INFO_URL);
        OneObject.setFileparams(fileParams);
        System.out.println("file" + fileParams);
        System.out.println("UTL" + OneObject.toString());
        OneObject.setStringparams(Stringparams);
        System.out.println("string" + Stringparams);

        MultipartFileUploaderAsync someTask = new MultipartFileUploaderAsync(getApplicationContext(), OneObject, new OnEventListener<String>() {
            @Override
            public void onSuccess(String object) {
                progressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "ress" + object, Toast.LENGTH_LONG).show();
                System.out.println("resArsh1" + object.toString());

                try {
                    JSONObject jsonObject1 = new JSONObject(object);
                    JSONObject jsonObject1response = jsonObject1.getJSONObject(Constants.RESPONSE);
                    int flag = jsonObject1response.getInt(Constants.FLAG);

                    if (flag == 1) {
                        String message = jsonObject1response.getString(Constants.MESSAGE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewInfoActivity.this);
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                Intent intent = new Intent(AddNewInfoActivity.this, MainActivityNew.class);
                                startActivity(intent);
                                finish();
                              /*  HomeFragment fragment_profile = new HomeFragment();
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                              *//*  fragmentTransaction.setCustomAnimations( R.anim.slide_left,R.anim.slide_right
                                );*//*
                                fragmentTransaction.replace(R.id.frame, fragment_profile, "");
                                fragmentTransaction.commitAllowingStateLoss();*/
                            }
                        });
                        android.support.v7.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        String message = jsonObject1response.getString(Constants.MESSAGE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewInfoActivity.this);
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        android.support.v7.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Exception e) {

            }
        });
        someTask.execute();
        return;
    }

    private boolean checkValidation() {


        boolean ret = true;


        if (!Validations.hasText(et_dieseaseName, "Please Enter Disease Name"))
            ret = false;

        if (!Validations.hasText(et_doctorName, "Please Enter Doctor Name"))
            ret = false;

        if (!Validations.hasText(et_fromdate, "Please Select From Date"))
            ret = false;

        if (!Validations.hasText(et_todate, "Please Select To Date"))
            ret = false;


        return ret;
    }

    @Override
    public void onBackPressed() {
        if (!et_dieseaseName.getText().toString().equals("") || !et_doctorName.getText().toString().equals("") ||
                !et_fromdate.getText().toString().equals("") || !et_todate.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddNewInfoActivity.this);
            builder.setMessage("Are You Sure?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Intent intent = new Intent(AddNewInfoActivity.this, MainActivityNew.class);
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });
            android.support.v7.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            super.onBackPressed();
        }
    }
}
