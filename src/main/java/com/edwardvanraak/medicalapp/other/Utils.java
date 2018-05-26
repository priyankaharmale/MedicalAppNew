package com.edwardvanraak.medicalapp.other;

import android.os.Environment;
import android.util.Log;

import java.io.File;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Shree on 21-Feb-18.
 */

public class Utils {

    public static File getAlbumStorageDir(String albumName) {

        // Get the directory for the user's public pictures directory.
        File file =
                new File(Environment.getExternalStorageDirectory(), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }
}
