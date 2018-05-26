package com.edwardvanraak.medicalapp.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.activity.AddNewInfoActivity;
import com.edwardvanraak.medicalapp.activity.MedicalRecordYearActivity;
import com.edwardvanraak.medicalapp.utils.AppConstants;
import com.edwardvanraak.medicalapp.utils.Constants;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment_profile initialization parameters, e.g. ARG_ITEM_NUMBER
    Button button;
    ProgressDialog progressdialog;
    public static final int Progress_Dialog_Progress = 0;
    public static final int REQUEST_CAMERA = 5;
    public static File destination;
    String camImage;
    protected static final int REQUEST_STORAGE_ACCESS_PERMISSION = 102;
    String GetPath;
    SlidingUpPanelLayout mLayout;
    SharedPreferences pref;
    ImageView iv_qrcode, iv_medicalrecord, iv_profile, iv_addnewInfo;
    TextView tv_name, tv_age;
    private int GALLERY = 1, CAMERA = 2;
    SharedPreferences.Editor editor;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String image, fullname, dob, profileimagge;
    int year, month, day;
    Boolean flag = false;
    File file;
    FileOutputStream fileoutputstream;
    private OnFragmentInteractionListener mListener;
    Drawable drawable;
    ProgressBar progress_item;
    ImageView iv_profilepicdialog;

    public HomeFragment() {
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment_profile
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);


        pref = getActivity().getApplicationContext().getSharedPreferences(Constants.SHAREPREFERNCE, MODE_PRIVATE);

        tv_age = (TextView) rootView.findViewById(R.id.tv_age);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        iv_qrcode = (ImageView) rootView.findViewById(R.id.iv_qrcode);
        progressdialog = new ProgressDialog(getActivity());
        progressdialog.setMessage("Downloading Image ...");
        iv_medicalrecord = (ImageView) rootView.findViewById(R.id.iv_medicalrecord);
        iv_addnewInfo = (ImageView) rootView.findViewById(R.id.iv_addnewInfo);
        iv_profile = (ImageView) rootView.findViewById(R.id.iv_profile);
        progress_item = (ProgressBar) rootView.findViewById(R.id.progress);

        progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressdialog.setCancelable(false);

        drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_account_circle_black_24dp);

        image = pref.getString(Constants.IMAGE, null);
        fullname = pref.getString(Constants.FULLNAME, null);
        dob = pref.getString(Constants.DATE_OF_BIRTH, null);
        profileimagge = pref.getString(Constants.PROFILE_IMAGE, null);


        if (profileimagge == null || profileimagge.equals("")) {
            iv_profile.setImageResource(R.drawable.ic_account_circle_black_24dp);
            progress_item.setVisibility(View.GONE);
        } else {

            try {
                Glide.with(getActivity())
                        .load(profileimagge)
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
        String[] separated = dob.split("-");

        year = Integer.valueOf(separated[0]);
        day = Integer.valueOf(separated[1]);
        month = Integer.valueOf(separated[2]);
        getAge(year, month, day);

        if (image == null || image.equals("")) {
            iv_qrcode.setImageResource(R.drawable.ic_qrcode);
        } else {
            Picasso.with(getActivity()).load(image).into(iv_qrcode);
        }
        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (profileimagge != null) {

                    final Dialog dialog = new Dialog(getActivity());
                    // Include dialog.xml file
                    dialog.setContentView(R.layout.custom_layout);
                    dialog.setTitle("");
                    // Set dialog title
                    // set values for custom dialog components - text, image and button
                    dialog.show();

                    Button btn_change = (Button) dialog.findViewById(R.id.btn_change);
                    iv_profilepicdialog = (ImageView) dialog.findViewById(R.id.iv_profile);
                    Button btn_remove = (Button) dialog.findViewById(R.id.btnremovove);
                    final ProgressBar progress_item = (ProgressBar) dialog.findViewById(R.id.progress);

                    // if decline button is clicked, close the custom dialog
/*
                    try {
                        Glide.with(getActivity())
                                .load(profileimagge)
                                //.error(R.drawable.no_image)
                                .centerCrop()
                                .crossFade()
// .placeholder(R.mipmap.defaulteventimagesmall)

                                .into(iv_profilepic
                                );
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }*/


                    btn_remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            editor = pref.edit();
                            editor.putString(Constants.PROFILE_IMAGE, "");
                            editor.commit();

                            try {
                                Glide.with(getActivity())
                                        .load("")
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
                                        .into(iv_profilepicdialog);
                            } catch (Exception e) {
                                Log.e("Exception", e.getMessage());
                            }
                        }
                    });
                    if (profileimagge == null || profileimagge.equals("")) {
                        iv_profile.setImageResource(R.drawable.ic_account_circle_black_24dp);
                        progress_item.setVisibility(View.GONE);
                    } else {

                        try {
                            Glide.with(getActivity())
                                    .load(profileimagge)
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
                                    .into(iv_profilepicdialog);
                        } catch (Exception e) {
                            Log.e("Exception", e.getMessage());
                        }


                    }

                    btn_change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Close dialog

                            showPictureDialog();
                        }
                    });

                } else {


                    showPictureDialog();
                }

            }
        });
        iv_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //saveImageLocally(bitmap);

                // new DownloadFileFromURL().execute(image);
                progressdialog.show();

                if (shouldAskPermissions()) {
                    askPermissions();
                } else {
                    filecreate();
                }


            }
        });
        tv_name.setText(fullname);


        iv_medicalrecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*MedicalRecordFragment fragment_profile = new MedicalRecordFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                *//*fragmentTransaction.setCustomAnimations(R.anim.fade,
                        android.R.anim.fade_out);*//*
                fragmentTransaction.setCustomAnimations(R.anim.slide_left, R.anim.slide_right
                );
                fragmentTransaction.replace(R.id.frame, fragment_profile, "");
                fragmentTransaction.commitAllowingStateLoss();*/

                Intent intent = new Intent(getActivity(), MedicalRecordYearActivity.class);
                startActivity(intent);
            }
        });
        iv_addnewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewInfoActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        tv_age.setText(ageS);

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Constants.SHAREPREFERNCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.USER_AGE, ageS);
        editor.commit();

        System.out.println("Date Of Age" + ageS);
        return ageS;
    }





    /* Checks if external storage is available for read and write */


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    filecreate();
                }
            }
        }
    }

    protected boolean shouldAskPermissions() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return false;
        }
        int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (permission != PackageManager.PERMISSION_GRANTED);
    }

    public void filecreate() {
        File file;
        BitmapDrawable draw = (BitmapDrawable) iv_qrcode.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        // Get the external storage directory path
        String path = Environment.getExternalStorageDirectory().toString();


        // Create a file to save the image
        file = new File(path, "BarCodeImage" + ".jpg");

        try {

            OutputStream stream = null;


            stream = new FileOutputStream(file);


            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);


            stream.flush();


            stream.close();

        } catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
            System.out.println("ImageExaception" + e);
        }

        // Parse the saved image path to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());
        progressdialog.dismiss();
        Toast.makeText(getActivity(), "Image saved in external storage.\n" + savedImageURI, Toast.LENGTH_LONG).show();

    }

    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
  /*  int requestCode = 200;
    requestPermissions(permissions, requestCode);*/
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(), permissions, 1);
        }
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
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
                    String imagePath12 = destination.getAbsolutePath();
                    editor = pref.edit();
                    editor.putString(Constants.PROFILE_IMAGE, imagePath12);
                    editor.commit();


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

                    try {
                        Glide.with(getActivity())
                                .load(imagePath12)
                                //.error(R.drawable.no_image)
                                .centerCrop()
                                .crossFade()
// .placeholder(R.mipmap.defaulteventimagesmall)

                                .into(iv_profilepicdialog);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == REQUEST_CAMERA) {


            System.out.println("REQUEST_CAMERA");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            String imagePath = destination.getAbsolutePath();
            Log.i("Path", imagePath);
            camImage = imagePath;
            Toast.makeText(getActivity(), camImage, Toast.LENGTH_SHORT).show();

            editor = pref.edit();
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
            try {
                Glide.with(getActivity())
                        .load(camImage)
                        //.error(R.drawable.no_image)
                        .centerCrop()
                        .crossFade()
// .placeholder(R.mipmap.defaulteventimagesmall)

                        .into(iv_profilepicdialog);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }
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


        getActivity().startActivityForResult(intent, REQUEST_CAMERA);
    }
}

