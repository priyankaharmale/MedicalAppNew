package com.edwardvanraak.medicalapp.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.utils.AppConstants;
import com.edwardvanraak.medicalapp.utils.AppUtils;
import com.edwardvanraak.medicalapp.utils.Constants;
import com.edwardvanraak.medicalapp.utils.ValidationMethods;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.view.View.OnClickListener;
/**
 * Created by Shree on 25-Jan-18.
 */

public class LoginActivity extends AppCompatActivity {
    TextView tv_register,tv_forgotpwd;
    EditText et_email,et_password;
    Button btn_login;
    ImageView imageView_qr;
    String camImage;
    public static File destination;
    public static final int REQUEST_CAMERA = 5;
    protected static final int REQUEST_STORAGE_ACCESS_PERMISSION = 102;
    ProgressDialog progressDialog;
    public static final String BARCODE_KEY = "BARCODE";
    private Barcode barcodeResult;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv_register=(TextView) findViewById(R.id.tv_register);
        tv_forgotpwd=(TextView) findViewById(R.id.tv_forgotpwd);
        et_email=(EditText) findViewById(R.id.et_emailId);
        et_password=(EditText) findViewById(R.id.et_password);
        imageView_qr=(ImageView) findViewById(R.id.imageView_qrr);
        btn_login=(Button) findViewById(R.id.btn_login);
        pref = getApplicationContext().getSharedPreferences(Constants.SHAREPREFERNCE, MODE_PRIVATE);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(LoginActivity.this,RegistrationActivity.class);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
                startActivity(i);

            }
        });

        imageView_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  isPermissionGrantedImage();

                startScan();
            }
        });
        tv_forgotpwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidationMethods vm = new ValidationMethods();
               if (!vm.isValidEmail(et_email.getText().toString()))
                {
                    et_email.setError("Enter Valid Email ID");
                    et_email.requestFocus();
                    return;
                }else
                if(et_password.getText().toString().length()<7){
                    et_password.setError("You must have 7 characters in your password ");
                    et_password.requestFocus();
                    return;
                }

                  else
               /* if(et_password.getText().toString().length()<8 &&!isValidPassword(et_password.getText().toString())){
                    et_password.setError("Enter Valid Password ");
                    et_password.requestFocus();
                    return;

                }else{*/
                    login();
                }





        });
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
    private void login() {
        progressDialog = ProgressDialog.show(LoginActivity.this, null, "Please wait....", false, false);
        //==================================================================================================
        /*SharedPreferences objecs*/
        final String email = et_email.getText().toString();
        final String password = et_password.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConstants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response123...." + response);

                        try {
                            JSONObject jsonObject1=new JSONObject(response);
                            JSONObject jsonObject1response= jsonObject1.getJSONObject(Constants.RESPONSE);
                            String message= jsonObject1response.getString(Constants.MESSAGE);
                            int flag=jsonObject1response.getInt(Constants.FLAG);

                            System.out.println("flag1234234234...." + flag);
                            if(flag==1)
                            {
                                String userId= jsonObject1response.getString(Constants.USER_ID);
                                String fullname= jsonObject1response.getString(Constants.FULLNAME);
                                String dob= jsonObject1response.getString(Constants.DATE_OF_BIRTH);
                                String image= jsonObject1response.getString(Constants.IMAGE);
                                String profileimage= jsonObject1response.getString("profileimage");
                                Toast.makeText(getApplicationContext(), "Login Successfully Done..", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, MainActivityNew.class);
                                editor = pref.edit();
                                editor.putString(Constants.USER_ID, userId);
                                editor.putString(Constants.IMAGE, image);
                                editor.putString(Constants.FULLNAME, fullname);
                                editor.putString(Constants.DATE_OF_BIRTH, dob);
                                editor.putString(Constants.PROFILE_IMAGE, profileimage);
                                editor.commit();
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                System.out.println("flag123...." + flag);

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                            System.out.println("JsonException"+e);
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("jsonexeption" + error.toString());
                        progressDialog.dismiss();
                        String reason = AppUtils.getVolleyError(LoginActivity.this, error);
                        showAlertDialog(reason);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("email", email);
                    params.put("password", password);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

    public void isPermissionGrantedImage() {

        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            camerImage();
        }

    }

    public void camerImage() {
        System.out.println("Click Image11");
        String name = AppConstants.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".png");


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(LoginActivity.this, getApplicationContext().getPackageName() + ".my.package.name.provider", destination));
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == REQUEST_CAMERA) {

            System.out.println("REQUEST_CAMERA");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            String imagePath = destination.getAbsolutePath();
            Log.i("Path", imagePath);
            camImage = imagePath;
            Toast.makeText(LoginActivity.this,camImage, Toast.LENGTH_SHORT).show();
        }

    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(LoginActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
//                        result.setText(barcode.rawValue);
                        //   checkIn(barcode.rawValue);

                        if (barcode.rawValue == null) {
                            Toast.makeText(getApplicationContext(), "Please Scan the Product Barcode", Toast.LENGTH_LONG).show();

                        } else {
                           /* SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(Constant.PRODUCT_BARCODE, barcode.rawValue);
                            editor.commit();*/
                            Toast.makeText(getApplicationContext(),
                                    barcode.rawValue, Toast.LENGTH_LONG).show();

                            emailverify(barcode.rawValue);
                           /* Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(intent);*/
                        }

                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MaterialBarcodeScanner.RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScan();
            return;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }

public void emailverify(final  String email)
{
    StringRequest stringRequest = new StringRequest(Request.Method.POST,
            AppConstants.EMAILVERIFY_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("response123...." + response);
                    try {
                        JSONObject jsonObject1=new JSONObject(response);
                        JSONObject jsonObject1response= jsonObject1.getJSONObject(Constants.RESPONSE);
                        String message= jsonObject1response.getString(Constants.MESSAGE);
                        int flag=jsonObject1response.getInt(Constants.FLAG);
                        System.out.println("flag1234234234...." + flag);
                        if(flag==1)
                        {
                            pincodeVerify(email);
                        }
                        else
                        {
                            progressDialog.dismiss();
                            System.out.println("flag123...." + flag);
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                        System.out.println("JsonException"+e);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("jsonexeption" + error.toString());
                    progressDialog.dismiss();
                    String reason = AppUtils.getVolleyError(LoginActivity.this, error);
                    showAlertDialog(reason);
                }
            }) {
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();
            try {
                params.put("email", email);
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
    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
    requestQueue.add(stringRequest);
    stringRequest.setShouldCache(false);
}


public  void pincodeVerify(final String email)
{
    final Dialog dialog = new Dialog(LoginActivity.this);
    // Include dialog.xml file
    dialog.setContentView(R.layout.custom_dialog);
    dialog.setTitle("Enter the Pin");
    // Set dialog title
    // set values for custom dialog components - text, image and button
    final EditText text = (EditText) dialog.findViewById(R.id.et_pin);
    dialog.show();

    Button declineButton = (Button) dialog.findViewById(R.id.declineButton);
    // if decline button is clicked, close the custom dialog
    declineButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Close dialog
            if(text.getText().toString().equals("")){
                text.setError("Please Enter the Pin");
                text.requestFocus();
                return;
            }
            else
            if (text.getText().toString().length()>4)
            {
                text.setError("Please Enter PIN as 4 Digit..");
                text.requestFocus();
                return;
            }
            else
            {
                progressDialog = ProgressDialog.show(LoginActivity.this, null, "Please wait....", false, false);
                //==================================================================================================
        /*SharedPreferences objecs*/
                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        AppConstants.PINVERIFY_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("response123...." + response);
                                try {
                                    JSONObject jsonObject1=new JSONObject(response);
                                    JSONObject jsonObject1response= jsonObject1.getJSONObject(Constants.RESPONSE);
                                    String message= jsonObject1response.getString(Constants.MESSAGE);
                                    int flag=jsonObject1response.getInt(Constants.FLAG);
                                    System.out.println("flag1234234234...." + flag);
                                    if(flag==1)
                                    {
                                        String userId= jsonObject1response.getString(Constants.USER_ID);
                                        String fullname= jsonObject1response.getString(Constants.FULLNAME);
                                        String dob= jsonObject1response.getString(Constants.DATE_OF_BIRTH);
                                        String image= jsonObject1response.getString(Constants.IMAGE);
                                        Toast.makeText(getApplicationContext(), "Pin Code successfully Match....", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, MainActivityNew.class);
                                        editor = pref.edit();
                                        editor.putString(Constants.USER_ID, userId);
                                        editor.putString(Constants.IMAGE, image);
                                        editor.putString(Constants.FULLNAME, fullname);
                                        editor.putString(Constants.DATE_OF_BIRTH, dob);
                                        editor.putString(Constants.USER_EMAILID, et_email.getText().toString());
                                        editor.commit();
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        System.out.println("flag123...." + flag);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                                }catch (JSONException e)
                                {
                                    e.printStackTrace();
                                    System.out.println("JsonException"+e);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("jsonexeption" + error.toString());
                                progressDialog.dismiss();
                                String reason = AppUtils.getVolleyError(LoginActivity.this, error);
                                showAlertDialog(reason);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        try {
                            params.put(Constants.PIN_CODE, text.getText().toString());
                            params.put(Constants.USER_EMAILID,email);
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
                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                requestQueue.add(stringRequest);
                stringRequest.setShouldCache(false);
            }





        }
    });
}

}
