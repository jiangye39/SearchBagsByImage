package com.jy.utils;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jy.activity.R;
import com.jy.model.Item;


public class MyItemAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Item> itemList;
	private int resourceId;
	private	Item item ;
	private String image_link="";
	private RequestQueue queue;
	private ImageLoader imageLoader;

	public MyItemAdapter(Context context, int resource, List<Item> objects) {
		inflater = LayoutInflater.from(context);
		this.itemList = objects;
		resourceId = resource;
		//volley
		queue = Volley.newRequestQueue(context);
		imageLoader = new ImageLoader(queue, new BitmapCache());
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		item = (Item) getItem(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(resourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.itemImage = (NetworkImageView) convertView
					.findViewById(R.id.item_image);
			viewHolder.itemName = (TextView) convertView
					.findViewById(R.id.item_name);
			viewHolder.itemPrice = (TextView) convertView
					.findViewById(R.id.item_price);
			convertView.setTag(viewHolder); // 将ViewHolder以TAG形式存放在View中
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		 image_link = item.getImage_link(); // 获取图片连接
//		 viewHolder.itemImage.setTag(image_link); //设置imageview的tag，传给imageloader用于判断，防止图片错位
		 //volley
		 if (image_link != null && !image_link.equals("")) {
			 viewHolder.itemImage.setDefaultImageResId(R.drawable.ic_launcher);
			 viewHolder.itemImage.setErrorImageResId(R.drawable.ic_launcher);
			 viewHolder.itemImage.setImageUrl(image_link, imageLoader);
			 }
		 if (!TextUtils.isEmpty(item.getName())) {
				viewHolder.itemName.setText(item.getName());
			}
		if (!TextUtils.isEmpty(item.getPrice())) {
			viewHolder.itemPrice.setText("￥" + item.getPrice());
		}
		return convertView;
	}

	class ViewHolder {
		NetworkImageView  itemImage;
		TextView itemName, itemPrice;
	}

}
