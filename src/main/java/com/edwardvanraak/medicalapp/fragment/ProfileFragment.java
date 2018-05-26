package com.edwardvanraak.medicalapp.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.edwardvanraak.medicalapp.MultipartRequest.MultiPart_Key_Value_Model;
import com.edwardvanraak.medicalapp.MultipartRequest.MultipartFileUploaderAsync;
import com.edwardvanraak.medicalapp.MultipartRequest.OnEventListener;
import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.activity.MainActivityNew;
import com.edwardvanraak.medicalapp.utils.AppConstants;
import com.edwardvanraak.medicalapp.utils.AppUtils;
import com.edwardvanraak.medicalapp.utils.Constants;
import com.edwardvanraak.medicalapp.utils.Utilities;
import com.edwardvanraak.medicalapp.utils.Validations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by neha on 4/9/2018..
 */

public class ProfileFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {

    EditText et_recovery, et_contact, et_address, et_emergency1, et_emergency2;
    ProgressDialog progress;
    TextView tv_name, tv_age;
    String user_id, blood_group, full_name, profile_pic, address, contact_no, emergencycontact2, emergencycontact1, user_age, recovery_email;
    SharedPreferences sharedPreferences;
    String shar_user_Id, profileimagge;
    public ProgressBar progress_item;
    ImageView btn_save;
    String isclick = "1";
    String imagePath12;
    String[] bloodGroup = {"Select Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
    Spinner spinner_blood;
    CircleImageView iv_profile;
    SharedPreferences.Editor editor;
    private int GALLERY = 1, CAMERA = 2;
    public static final int REQUEST_CAMERA = 5;
    public static File destination;
    String camImage;
    protected static final int REQUEST_STORAGE_ACCESS_PERMISSION = 102;

    Drawable drawable;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences(Constants.SHAREPREFERNCE, MODE_PRIVATE);
        shar_user_Id = sharedPreferences.getString(Constants.USER_ID, null);
        user_age = sharedPreferences.getString(Constants.USER_AGE, null);
        profileimagge = sharedPreferences.getString(Constants.PROFILE_IMAGE, null);

        progress_item = (ProgressBar) rootView.findViewById(R.id.progress_item);
        drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_account_circle_black_24dp);

        et_recovery = (EditText) rootView.findViewById(R.id.et_recovery);
        et_contact = (EditText) rootView.findViewById(R.id.et_contact);
        et_address = (EditText) rootView.findViewById(R.id.et_address);
        et_emergency1 = (EditText) rootView.findViewById(R.id.et_emergency1);
        et_emergency2 = (EditText) rootView.findViewById(R.id.et_emergency2);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_age = (TextView) rootView.findViewById(R.id.tv_age);
        btn_save = (ImageView) rootView.findViewById(R.id.btn_save);
        spinner_blood = (Spinner) rootView.findViewById(R.id.spinner_blood);
        iv_profile = (CircleImageView) rootView.findViewById(R.id.iv_profile);
        spinner_blood.setClickable(false);


        tv_age.setText(user_age);
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Please wait...");

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isclick.equals("1")) {
                    btn_save.setImageResource(R.drawable.save);
                    et_recovery.setEnabled(true);
                    et_address.setEnabled(true);
                    et_contact.setEnabled(true);
                    et_emergency1.setEnabled(true);
                    et_emergency2.setEnabled(true);
                    spinner_blood.setClickable(true);

                    isclick = "0";


                } else {
                    if (checkValidation()) {
                        if (Utilities.isNetworkAvailable(getActivity())) {
                            if (blood_group.equals("Select Blood Group") || blood_group.equals("")) {
                                Toast.makeText(getActivity(), "Please Select Blood Group", Toast.LENGTH_LONG).show();
                            } else {
                                sendData(camImage);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please Connect to internet", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }
        });

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });


        getDetailsInfo(shar_user_Id);
        spinner_blood.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, bloodGroup);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner_blood.setAdapter(aa);
        return rootView;

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        //  Toast.makeText(getActivity(), bloodGroup[position], Toast.LENGTH_LONG).show();
        blood_group = bloodGroup[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    public void getDetailsInfo(final String user_Id) {
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AppConstants.PROFILE_DETAILS_URL,
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
                                JSONArray jsonArray = jsonObject1response.getJSONArray("details");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    user_id = jsonObject.getString(Constants.USER_ID);
                                    full_name = jsonObject.getString(Constants.FULLNAME);
                                    profile_pic = jsonObject.getString(Constants.PROFILE_PIC);
                                    address = jsonObject.getString(Constants.ADDRESS);
                                    contact_no = jsonObject.getString(Constants.USER_CONTACT);
                                    emergencycontact1 = jsonObject.getString(Constants.USER_EMERGENCYNO1);
                                    emergencycontact2 = jsonObject.getString(Constants.USER_EMERGENCYNO2);
                                    recovery_email = jsonObject.getString(Constants.USER_RECOVEREMAIL);
                                    blood_group = jsonObject.getString(Constants.USER_BLOODGROUP);
                                }
                                editor = sharedPreferences.edit();
                                editor.putString(Constants.PROFILE_IMAGE, profile_pic);
                                editor.commit();
                                if (profile_pic != null) {
                                    try {
                                        Glide.with(getActivity())
                                                .load(profile_pic)
                                                .error(drawable)
                                                .centerCrop()
                                                .crossFade()
                                                .listener(new RequestListener<String, GlideDrawable>() {
                                                    @Override
                                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                        progress_item.setVisibility(View.GONE);
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                        progress_item.setVisibility(View.GONE);
                                                        return false;
                                                    }
                                                })
                                                .into(iv_profile);
                                    } catch (Exception e) {
                                        Log.e("Exception", e.getMessage());
                                    }
                                }
                                et_address.setText(address);
                                et_contact.setText(contact_no);
                                et_emergency1.setText(emergencycontact1);
                                et_emergency2.setText(emergencycontact2);
                                et_recovery.setText(recovery_email);
                                tv_name.setText(full_name);
                                //  spinner_blood.setPrompt(blood_group);
                                for (int i = 0; i < bloodGroup.length; i++) {
                                    if (bloodGroup[i].equals(blood_group)) {
                                        spinner_blood.setSelection(i);
                                    }

                                }

                            } else {
                                System.out.println("flag123...." + flag);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        String reason = AppUtils.getVolleyError(getActivity(), error);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        stringRequest.setShouldCache(false);
    }

    private void showAlertDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void showPictureDialog() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                isPermissionGrantedImageGallery();
                                break;
                            case 1:
                                isPermissionGrantedImage();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void isPermissionGrantedImageGallery() {

        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            choosePhotoFromGallary();
        }

    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            new android.app.AlertDialog.Builder(getActivity())
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        }
    }

    public void camerImage() {
        System.out.println("Click Image11");
        String name = AppConstants.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".png");


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".my.package.name.provider", destination));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void isPermissionGrantedImage() {

        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            camerImage();
        }

    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                    // Log.i("Path", imagePath12);
                    FileOutputStream fo;
                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    imagePath12 = destination.getAbsolutePath();
                    editor = sharedPreferences.edit();
                    editor.putString(Constants.PROFILE_IMAGE, imagePath12);
                    editor.commit();
                    camImage = imagePath12;

                    try {
                        Glide.with(getActivity())
                                .load(imagePath12)
                                //.error(R.drawable.no_image)
                                .centerCrop()
                                .crossFade()
// .placeholder(R.mipmap.defaulteventimagesmall)

                                .into(iv_profile);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == REQUEST_CAMERA) {

            if (data != null) {
                System.out.println("REQUEST_CAMERA");

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                String imagePath = destination.getAbsolutePath();
                Log.i("Path", imagePath);
                camImage = imagePath;
                Toast.makeText(getActivity(), camImage, Toast.LENGTH_SHORT).show();

                editor = sharedPreferences.edit();
                editor.putString(Constants.PROFILE_IMAGE, camImage);
                editor.commit();

                try {
                    Glide.with(getActivity())
                            .load(camImage)
                            //.error(R.drawable.no_image)
                            .centerCrop()
                            .crossFade()
// .placeholder(R.mipmap.defaulteventimagesmall)

                            .into(iv_profile);
                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
            }
        }
    }


    public void sendData(String camImage) {
        progress.show();

        MultiPart_Key_Value_Model OneObject = new MultiPart_Key_Value_Model();
        Map<String, String> fileParams = new HashMap<>();
        if (camImage == null) {
            //  fileParams.put("profile_pic", "");

        } else {
            fileParams.put("profile_pic", camImage);

        }

        System.out.println("priya Op" + camImage);

        Map<String, String> stringparam = new HashMap<>();

        stringparam.put(Constants.USER_ID, user_id);
        stringparam.put(Constants.USER_RECOVEREMAIL, et_recovery.getText().toString());
        stringparam.put(Constants.USER_CONTACT, et_contact.getText().toString());
        stringparam.put(Constants.ADDRESS, et_address.getText().toString());
        stringparam.put(Constants.USER_EMERGENCYNO1, et_emergency1.getText().toString());
        stringparam.put(Constants.USER_EMERGENCYNO2, et_emergency2.getText().toString());
        stringparam.put("blood_group", blood_group);

        OneObject.setUrl(AppConstants.PROFILE_UPDATE_URL);
        OneObject.setFileparams(fileParams);
        System.out.println("file" + fileParams);
        System.out.println("UTL" + OneObject.toString());
        OneObject.setStringparams(stringparam);
        System.out.println("string" + stringparam);

        MultipartFileUploaderAsync someTask = new MultipartFileUploaderAsync(getActivity(), OneObject, new OnEventListener<String>() {
            @Override
            public void onSuccess(String object) {
                progress.dismiss();
                System.out.println("Result" + object);
                //    Toast.makeText(getActivity(), "ress" + object, Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonObject1 = new JSONObject(object);
                    JSONObject jsonObject1response = jsonObject1.getJSONObject(Constants.RESPONSE);
                    int flag = jsonObject1response.getInt(Constants.FLAG);

                    if (flag == 1) {


                        String message = jsonObject1response.getString(Constants.MESSAGE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                et_recovery.setEnabled(false);
                                et_address.setEnabled(false);
                                et_contact.setEnabled(false);
                                et_emergency1.setEnabled(false);
                                et_emergency2.setEnabled(false);
                                btn_save.setImageResource(R.drawable.edit);
                                isclick = "1";
                                Intent intent = new Intent(getActivity(), MainActivityNew.class);
                                startActivity(intent);

                            }
                        });
                        android.support.v7.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        String message = jsonObject1response.getString(Constants.MESSAGE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                    System.out.println("JSONException" + e);
                }
            }


            @Override
            public void onFailure(Exception e) {
                System.out.println("onFailure" + e);

            }
        });
        someTask.execute();
        return;
    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validations.isValidEmail(et_recovery, et_recovery.getText().toString(), "Please enter valid Email id", true))
            ret = false;
        if (!Validations.hasText(et_contact, "Please Enter Contact Number"))
            ret = false;
        if (!Validations.hasText(et_address, "Please Enter Address"))
            ret = false;
        if (!Validations.hasText(et_emergency1, "Please Enter Emergency Contact"))
            ret = false;
        if (!Validations.hasText(et_emergency2, "Please Enter Emergency Contact"))
            ret = false;


        return ret;
    }


}
