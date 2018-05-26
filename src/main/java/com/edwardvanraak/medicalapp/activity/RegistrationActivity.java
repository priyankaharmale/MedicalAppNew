package com.edwardvanraak.medicalapp.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.utils.AppConstants;
import com.edwardvanraak.medicalapp.utils.AppUtils;
import com.edwardvanraak.medicalapp.utils.ValidationMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shree on 25-Jan-18.
 */

public class RegistrationActivity extends AppCompatActivity {
    TextView tv_login;
    Button btn_register;
    ProgressDialog progressDialog;
    EditText et_fullname, et_emailid, et_recovery, et_dob, et_emergency1, et_emergency2, et_password, et_confirmpwd, et_pin;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tv_login = (TextView) findViewById(R.id.tv_login);
        et_fullname = (EditText) findViewById(R.id.et_fullname);
        et_emailid = (EditText) findViewById(R.id.et_emailid);
        et_recovery = (EditText) findViewById(R.id.et_recovery);
        et_dob = (EditText) findViewById(R.id.et_dob);
        et_emergency1 = (EditText) findViewById(R.id.et_emergency1);
        et_emergency2 = (EditText) findViewById(R.id.et_emergency2);
        et_password = (EditText) findViewById(R.id.et_password);
        et_confirmpwd = (EditText) findViewById(R.id.et_confirmpwd);
        et_pin = (EditText) findViewById(R.id.et_pin);

        btn_register = (Button) findViewById(R.id.btn_register);

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        et_dob.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    datePicker();
                }
                return false;
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            final String password = et_password.getText().toString();
            final String confirmPassword = et_confirmpwd.getText().toString();

            @Override
            public void onClick(View view) {
                ValidationMethods vm = new ValidationMethods();
                if (!vm.isValidName(et_fullname.getText().toString())) {
                    et_fullname.setError("Enter Valid  Name");
                    et_fullname.requestFocus();
                    return;
                } else if (!vm.isValidEmail(et_emailid.getText().toString())) {
                    et_emailid.setError("Enter Valid Email ID");
                    et_emailid.requestFocus();
                    return;
                } else if (!vm.isValidEmail(et_recovery.getText().toString())) {
                    et_recovery.setError("Enter Valid REcovery Email ID");
                    et_recovery.requestFocus();
                    return;
                } else if (et_dob.getText().toString().equals("")) {
                    et_dob.setError("Please Select Date");
                    et_dob.requestFocus();
                    return;
                } else if (et_emergency1.getText().toString().equals("")) {
                    et_emergency1.setError("Enter Valid Number");
                    et_emergency1.requestFocus();
                    return;
                } else if (!vm.isValidNumber(et_emergency1.getText().toString())) {
                    et_emergency1.setError("Enter Valid Number");
                    et_emergency1.requestFocus();
                    return;
                } else if ((et_emergency1.getText().toString().length() < 10)) {
                    et_emergency1.setError("Enter 10 Digit Number");
                    et_emergency1.requestFocus();
                    return;
                } else if (et_emergency2.getText().toString().equals("")) {
                    et_emergency2.setError("Enter Valid Number ");
                    et_emergency2.requestFocus();
                    return;
                } else if (!vm.isValidNumber(et_emergency2.getText().toString())) {
                    et_emergency2.setError("Enter Valid Number");
                    et_emergency2.requestFocus();
                    return;
                } else if ((et_emergency2.getText().toString().length() < 10)) {
                    et_emergency2.setError("Enter 10 Digit Number");
                    et_emergency2.requestFocus();
                    return;
                } else if (et_password.getText().toString().length() < 7) {
                    et_password.setError("You must have 7 characters in your password ");
                    et_password.requestFocus();
                    return;
                }
                if (et_confirmpwd.getText().toString().length() < 7) {
                    et_confirmpwd.setError("You must have 7 characters in your password ");
                    et_confirmpwd.requestFocus();
                    return;
                } else if (!et_password.getText().toString().equals(et_confirmpwd.getText().toString())) {
                    Toast.makeText(RegistrationActivity.this, "Password Not matching ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (et_pin.getText().toString().equals("")) {
                    et_pin.setError("Enter the Pin");
                    et_pin.requestFocus();
                    return;
                } else if (et_pin.getText().toString().length() < 4) {
                    et_pin.setError("Please Enter PIN as 4 Digit..");
                    et_pin.requestFocus();
                    return;

                } else {
                    register();
                }
            }

        });
    }

    private void register() {
        progressDialog = ProgressDialog.show(RegistrationActivity.this, null, "Please wait....", false, false);

        //==================================================================================================
        /*SharedPreferences objecs*/


        final String fullname = et_fullname.getText().toString();
        final String email = et_emailid.getText().toString();
        final String recoveremail = et_recovery.getText().toString();
        final String dob = et_dob.getText().toString();
        final String emergency1 = et_emergency1.getText().toString();
        final String emergency2 = et_emergency2.getText().toString();
        final String password = et_password.getText().toString();
        final String pin = et_pin.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConstants.REGISTRATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response...." + response);

                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            JSONObject jsonObject1response = jsonObject1.getJSONObject("response");
                            String message = jsonObject1response.getString("msg");
                            int flag = jsonObject1response.getInt("flag");
                            if (flag == 1) {
                                //  Toast.makeText(getApplicationContext(), "Registration Successfully Done..", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                                builder.setMessage(message);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                android.support.v7.app.AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                progressDialog.dismiss();
                            } else {
                                // showAlertDialog(message);
                                //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                System.out.println("flag123...." + flag);

                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
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
                                progressDialog.dismiss();

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
                        progressDialog.dismiss();
                        String reason = AppUtils.getVolleyError(RegistrationActivity.this, error);
                        showAlertDialog(reason);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("full_name", fullname);
                    params.put("email", email);
                    params.put("recovery_email", recoveremail);
                    params.put("emergency_contact1", emergency1);
                    params.put("emergency_contact2", emergency2);
                    System.out.println("Date" + dob);
                    params.put("dob", dob);
                    params.put("password", password);
                    params.put("pin_code", pin);
                    params.put("username", "");
                    params.put("address", "");
                    params.put("add_date", "add_date");
                    params.put("user_contact", "");
                } catch (Exception e)

                {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
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

    void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {

                //  et_pref_day.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                // et_dob.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                et_dob.setText(year + "-" + (dayOfMonth) + "-" + (monthOfYear + 1));


                System.out.println("Date.." + year + "-" + (dayOfMonth) + "-" + (monthOfYear + 1));
                // YYYY-MM-DD
                et_dob.setError(null);

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();


    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[aA-zZ])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}
