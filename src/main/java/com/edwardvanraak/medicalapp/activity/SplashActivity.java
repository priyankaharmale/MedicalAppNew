package com.edwardvanraak.medicalapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.edwardvanraak.medicalapp.R;
import com.edwardvanraak.medicalapp.utils.AppConstants;
import com.edwardvanraak.medicalapp.utils.Constants;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Shree on 25-Jan-18.
 */

public class SplashActivity extends AppCompatActivity {

    SharedPreferences pref;
    String user_Id;
    private int GALLERY = 1, CAMERA = 2;

    Double longitude;
    public static final int Progress_Dialog_Progress = 0;
    public static final int REQUEST_CAMERA = 5;
    public static File destination;
    String camImage;
    Button btn_image;
    protected static final int REQUEST_STORAGE_ACCESS_PERMISSION = 102;

    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pref = getApplicationContext().getSharedPreferences(Constants.SHAREPREFERNCE, MODE_PRIVATE);
        user_Id = pref.getString(Constants.USER_ID, null);
        System.out.println("USerId" + user_Id);
        btn_image = (Button) findViewById(R.id.btn_image);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(user_Id==null)
                { Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }else
                {
                    Intent i = new Intent(SplashActivity.this, MainActivityNew.class);
                    startActivity(i);
                    finish();
                }

            }
        }, 3000);

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(SplashActivity.this);
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
    public void isPermissionGrantedImage() {

        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            camerImage();
        }

    }

    public void isPermissionGrantedImageGallery() {

        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            choosePhotoFromGallary();
        }

    }

    private void requestPermissions(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permission)) {
            new android.app.AlertDialog.Builder(SplashActivity.this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{permission}, requestCode);
        }
    }


    public void camerImage() {
        System.out.println("Click Image11");
        String name = AppConstants.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".png");


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(SplashActivity.this, getApplicationContext().getPackageName() + ".my.package.name.provider", destination));


        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

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


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SplashActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == REQUEST_CAMERA) {


            System.out.println("REQUEST_CAMERA");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            String imagePath = destination.getAbsolutePath();
            Log.i("Path", imagePath);
            camImage = imagePath;
            Toast.makeText(SplashActivity.this, camImage, Toast.LENGTH_SHORT).show();


        }
    }
}
