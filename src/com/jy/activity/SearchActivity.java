package com.jy.activity;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.jy.model.Item;
import com.jy.utils.ImageCutUtils;
import com.jy.utils.MyItemAdapter;
import com.jy.utils.NetUtils;
import com.jy.utils.SaveImageUtils;
  
public class SearchActivity extends Activity {  
    private ImageView im_Grabcut ;  
    private ImageView im_Resize ;     
    private Bitmap image ;			//裁剪后的的图像
    private Bitmap imageCut ;    	//分割后的的图像
    
    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.png";//图像路径
    private static final Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the bitmap
    private static final int CHOOSE_PICTURE = 1;//相册选图
    private static final int TAKE_PHOTO = 2;//拍照选图
    private static final int CUT_PHOTO_DONE = 3;//裁剪照片完成
    private static final String SERVICE_URL = "http://100.64.28.172:8080/Search/servlet/SearchServlet"; 
    private static final String IMAGE_PATH = "/sdcard/temp.png"; 
	private static final String TAG = "ImageCutActivity";	
	
	//listview
	private ListView list_Items;
	private static MyItemAdapter adapter;
	public static List<Item> itemList = new ArrayList<Item>();
	
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
	};
	//初始化OpenCV
	public void onResume()
	{
		super.onResume();
		if (!OpenCVLoader.initDebug()) {
			Log.d(TAG, "!!!!!!!!!!!Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
		} else {
			Log.d(TAG, "!!!!!!!!!!!!OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}
	}	
	
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.search_image);  
        
        im_Grabcut=(ImageView) findViewById(R.id.im_Grabcut); 
        im_Resize=(ImageView) findViewById(R.id.im_Resize); 
        list_Items=(ListView)findViewById(R.id.list_Items);
        
        adapter = new MyItemAdapter(this, R.layout.list_item, itemList);
        list_Items.setAdapter(adapter);	
        //点击listview中item跳转至网页展示
        list_Items.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Item item = itemList.get(position);
				Intent intent = new Intent(SearchActivity.this,
						WebViewActivity.class);
				intent.putExtra("itemlink", item.getItem_link());
				startActivity(intent);
			}
		});
    }  
      
	//从相册中选择图像
    public void OcAlbum(View v) {   	    	
    	Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
    	intent.setType("image/*");
    	crop(intent);
    	startActivityForResult(intent, CHOOSE_PICTURE);  
    }
	//拍照选择图像
    public void OcCamera(View v) {
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
    	startActivityForResult(intent, TAKE_PHOTO);   	
    }  
	public void OcGrabcut(View v) {
    	if(image!=null){    		
    		imageCut=Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
    		new Thread(new Runnable() {
    			@Override
    			public void run() {
    				final long startTime = new Date().getTime();	//开始时间
    				//图像分割
    				imageCut=ImageCutUtils.Cut(image);
    				//保存图像
    				SaveImageUtils.saveBitmap(imageCut,IMAGE_PATH);
    				final long endTime = new Date().getTime();	//结束时间
    				// 执行任务在主线程中
    				runOnUiThread(new Runnable() {
    					@Override
    					public void run() {
    						// 就是在主线程中操作
    						im_Grabcut.setImageBitmap(imageCut);
    						Toast.makeText(SearchActivity.this,"图像分割完成，用时："+(endTime-startTime)/1000+"秒", 1).show();
    					}
    				});
    	    		
    			}
    		}).start();
    	
    	}else {
    		Toast.makeText(this, "请先选择图像再进行分割", 0).show();
		}
    }   
	//发送按钮，用POST请求发送图像并接受返回的结果
    public void Send(View v) {
    	if(itemList!=null){
    		itemList.clear();// 清空上一次的数据
    	}
    	new Thread(new Runnable() {
			@Override
			public void run() {				
			    final String result = NetUtils.Post(SERVICE_URL, IMAGE_PATH,itemList);				
				// 执行任务在主线程中
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 就是在主线程中操作						
						adapter.notifyDataSetChanged();
						Toast.makeText(SearchActivity.this, result, 0).show();
					}
				});
			}
		}).start();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	switch (requestCode) {
    	
        case CHOOSE_PICTURE:
        Log.d(TAG, "CHOOSE_PICTURE: data = " + data);//it seems to be null
        if(imageUri != null){
        image = decodeUriAsBitmap(imageUri);//decode bitmap
        im_Resize.setImageBitmap(image);
        }
        break;
        
        case TAKE_PHOTO:
            Log.d(TAG, "TAKE_BIG_PICTURE: data = " + data);//it seems to be null            
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(imageUri, "image/*");
            crop(intent);
            startActivityForResult(intent, CUT_PHOTO_DONE);
         break;
         
        case CUT_PHOTO_DONE:
            Log.d(TAG, "CUT_PHOTO_DONE: data = " + data);//it seems to be null
            if(imageUri != null){
        	image = decodeUriAsBitmap(imageUri);//decode bitmap
        	im_Resize.setImageBitmap(image);
            }
            break;
        default:
        break;
        }    	
    }

    private Bitmap decodeUriAsBitmap(Uri uri){
        try {
    	image = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
		            e.printStackTrace();
		            return null;
            }
        return image;
        }     
    
    private void crop(Intent intent){    	
         intent.putExtra("crop", "true");
         intent.putExtra("aspectX", 1);
         intent.putExtra("aspectY", 1);
         intent.putExtra("outputX", 390);
         intent.putExtra("outputY", 390);
         intent.putExtra("scale", true);
         intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
         intent.putExtra("return-data", false);
         intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
         intent.putExtra("noFaceDetection", true); // no face detection
        }     
    
	
    @Override
	protected void onDestroy() {
		super.onDestroy();
		if(image!=null)
			image.recycle();  
		if(imageCut!=null)
			imageCut.recycle();
	}
}


