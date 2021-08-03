package com.sns.snsmini.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class AppHelperUtil {
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public boolean changeSharedPrefValue(SharedPreferences sh, JSONObject visitor) {

        boolean result = false;

        SharedPreferences.Editor editor = sh.edit();

        try {
            JSONObject visitorInfo = visitor.getJSONObject("visitorInfoVO");

            editor.putString("userName", visitor.getString("userName"));
            editor.putString("gender", visitor.getString("gender"));
            editor.putString("userBirthYear", visitorInfo.getString("userBirthYear"));
            editor.putString("userBirthMonth", visitorInfo.getString("userBirthMonth"));
            editor.putString("userBirthDay", visitorInfo.getString("userBirthDay"));
            editor.putString("userLanguage", visitorInfo.getString("userLanguage"));
            editor.putString("userPicUploadPath", visitorInfo.getString("userPicUploadPath"));
            editor.putString("userPicUploadNm", visitorInfo.getString("userPicUploadNm"));

            result = true;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            editor.commit();
        }

        return result;
    }
}
