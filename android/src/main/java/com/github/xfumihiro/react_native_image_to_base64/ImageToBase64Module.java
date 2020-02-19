package com.github.xfumihiro.react_native_image_to_base64;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.provider.MediaStore;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.FileNotFoundException;

public class ImageToBase64Module extends ReactContextBaseJavaModule {
  Context context;

  public ImageToBase64Module(ReactApplicationContext reactContext) {
    super(reactContext);
    this.context = (Context) reactContext;
  }

  @Override
  public String getName() {
    return "RNImageToBase64";
  }

  @ReactMethod
  public void getBase64String(String uri,String fileName, Callback callback) {
    if(isImage(fileName.toLowerCase())){
      try {
        Bitmap image = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), Uri.parse(uri));
        if (image == null) {
          callback.invoke("Failed to decode Bitmap, uri: " + uri);
        } else {
          callback.invoke(null, bitmapToBase64(image));
        }
      } catch (IOException e) {
      }
    }else{
      getFileBase64String(uri,callback);
    }

  }



  public void getFileBase64String(String uri, Callback callback) {
    String base64 =  convertFileToByteArray(Uri.parse(uri));
    if (base64 == null || base64.length()==0) {
      callback.invoke("Failed to decode Bitmap, uri: " + uri);
    } else {
      callback.invoke(null, base64);
    }
  }

  private String bitmapToBase64(Bitmap bitmap) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
    byte[] byteArray = byteArrayOutputStream.toByteArray();
    return Base64.encodeToString(byteArray, Base64.DEFAULT);
  }

  private String convertFileToByteArray(Uri uri) {
    byte[] byteArray = null;
    try {
      InputStream inputStream = this.context.getContentResolver().openInputStream(uri);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      byte[] b = new byte[1024 * 11];
      int bytesRead = 0;
      if(inputStream==null){
        return "";
      }
      while ((bytesRead = inputStream.read(b)) != -1) {
        bos.write(b, 0, bytesRead);
      }

      byteArray = bos.toByteArray();

      Log.e("Byte array", ">" + byteArray);

    } catch (IOException e) {
      e.printStackTrace();
    }
    return Base64.encodeToString(byteArray, Base64.DEFAULT);
  }

  private boolean isImage(String fileName){
    return (fileName.contains("jpg")||fileName.contains("jpeg")||fileName.contains("png")||fileName.contains("gif"));
  }

}
