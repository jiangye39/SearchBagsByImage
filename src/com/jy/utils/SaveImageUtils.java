package com.jy.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.util.Log;

public class SaveImageUtils {
	
	private static final String TAG = "SaveImageUtils";

	 public static void saveBitmap(Bitmap bitmap,String path) {
		  Log.e(TAG, "保存图片");
		  File f = new File(path);
		  if (f.exists()) {
		   f.delete();
		  }
		  try {
		   FileOutputStream out = new FileOutputStream(f);
		   bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		   out.flush();
		   out.close();
		   Log.i(TAG, "已经保存");
		  } catch (FileNotFoundException e) {
		   e.printStackTrace();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
	 }
}

