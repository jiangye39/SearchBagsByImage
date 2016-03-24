package com.jy.utils;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageCutUtils {
	private static final String TAG = "GrabCut";
	 
	 //Í¼Ïñ·Ö¸î
	 public static Bitmap Cut(Bitmap bitmap){
	    	bitmap=grabCut(bitmap);
	    	return bitmap;
	    }
	//grabCut
	 private static Bitmap grabCut(Bitmap bitmap)
	   	{
	    	
			Scalar  color = new Scalar(255, 0, 0, 255);
			Mat  dst = new Mat();
		    Log.d(TAG, "bitmap: " + bitmap.getWidth() + "x" + bitmap.getHeight());

		    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		    Log.d(TAG, "bitmap 8888: " + bitmap.getWidth() + "x" + bitmap.getHeight());

		    Mat img = new Mat();
		    Utils.bitmapToMat(bitmap, img);
	        //¿ò´óÐ¡
		    Point p1 = new Point((img.cols()/16), (img.rows()/16));
		    Point p2 = new Point((img.cols()-img.cols()/16), (img.rows()-img.rows()/16));
		    Rect rect = new Rect(p1,p2);

		    int border = 20;
		    int border2 = border + border;
		    Rect rect2 = new Rect( border, border, img.cols() - border2, img.rows()-border2);
		    Log.d(TAG, "img size : " + img.cols() + "x" + img.rows());
		    
		    Mat mask = new Mat();
		    System.out.println("what?:  "+mask.type());
		    
		    mask.setTo(new Scalar(125));	    
		    Mat fgdModel = new Mat();
		    fgdModel.setTo(new Scalar(255, 255, 255));
		    Mat bgdModel = new Mat();
		    bgdModel.setTo(new Scalar(255, 255, 255));

		    Mat imgC3 = new Mat();
		    Imgproc.cvtColor(img, imgC3, Imgproc.COLOR_RGBA2RGB);
		    Log.d(TAG, "imgC3: " + imgC3);
		    Log.d(TAG, "Grabcut begins");
		    Imgproc.grabCut(imgC3, mask, rect, bgdModel, fgdModel, 2, Imgproc.GC_INIT_WITH_RECT);
		    Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(3.0));
		    //Do Sth
		    Core.compare(mask, source, mask, Core.CMP_EQ);
		    //Do Sth
		    Mat foreground = new Mat(img.size(), CvType.CV_8UC4, new Scalar(255, 255, 255));
		    img.copyTo(foreground, mask);
		    Utils.matToBitmap(foreground, bitmap);
	   		
		    img.release();
		    imgC3.release();
		    mask.release();
		    fgdModel.release();
		    bgdModel.release();
//		    bitmap= makeBlackTransparent(bitmap);
		    bitmap= makeBackgroundWhite(bitmap);
			return bitmap;
		}
	    
	 private static Bitmap makeBackgroundWhite(Bitmap image) {
	        // convert image to matrix
	        Mat src = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC4);
	        Utils.bitmapToMat(image, src);

	        // init new matrices
	        Mat dst = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC4);
	        Mat tmp = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC4);
	        Mat alpha = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC4);

	        // convert image to grayscale
	        Imgproc.cvtColor(src, tmp, Imgproc.COLOR_BGR2GRAY);

	        // threshold the image to create alpha channel with complete transparency in black background region and zero transparency in foreground object region.
	        Imgproc.threshold(tmp, alpha, 0, 255, Imgproc.THRESH_BINARY);

	        // split the original image into three single channel.
	        List<Mat> rgb = new ArrayList<Mat>(3);
	        Core.split(src, rgb);

	        // Create the final result by merging three single channel and alpha(BGRA order)
	        List<Mat> rgba = new ArrayList<Mat>(4);
	        rgba.add(rgb.get(0));
	        rgba.add(rgb.get(1));
	        rgba.add(rgb.get(2));
	        rgba.add(alpha);
	        Core.merge(rgba, dst);

	        // convert matrix to output bitmap
	        Bitmap output = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
	        Utils.matToBitmap(dst, output);
	        return output;
	    }
	
}
