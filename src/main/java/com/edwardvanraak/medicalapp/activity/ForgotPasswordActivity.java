package com.edwardvanraak.medicalapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.edwardvanraak.medicalapp.utils.Constants;
import com.edwardvanraak.medicalapp.utils.ValidationMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shree on 17-Feb-18.
 */

public class ForgotPasswordActivity extends AppCompatActivity {

    Button btn_submit;
    EditText et_emailId;
    ProgressDialog progressDialog;
    String message;
    int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_emailId = (EditText) findViewById(R.id.et_emailid);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ValidationMethods vm = new ValidationMethods();
                if (!vm.isValidEmail(et_emailId.getText().toString())) {
                    et_emailId.setError("Enter Valid Email-Id");
                    et_emailId.requestFocus();
                    return;
                } else {
                    forgotPassword();
                }
            }
        });
    }

    private void forgotPassword() {
        progressDialog = ProgressDialog.show(ForgotPasswordActivity.this, null, "Please wait....", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstants.FORGOTPASSWORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        System.out.println("response" + response.toString());
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            JSONObject j = jsonObject1.getJSONObject(Constants.RESPONSE);
                            flag = j.getInt(Constants.FLAG);
                            message = j.getString(Constants.MESSAGE);
                            System.out.println("message123" + message);
                            if (flag == 1) {
                                message = j.getString(Constants.MESSAGE);
                                System.out.println("message23432" + message);
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("jsonexeption" + error.toString());
                        progressDialog.dismiss();
                        String reason = AppUtils.getVolleyError(ForgotPasswordActivity.this, error);
                        showAlertDialog(reason);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(Constants.USER_EMAILID, et_emailId.getText().toString());
                    System.out.println("params123" + params);
                } catch (Exception e) {
                    System.out.println("erroralocbloc" + e.toString());
                }
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    //==================================================================================================
    private void showAlertDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        builder.setTitle("Message")
                .setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
    }

