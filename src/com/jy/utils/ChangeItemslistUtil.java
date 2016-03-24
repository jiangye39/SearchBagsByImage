package com.jy.utils;

import java.util.List;

import android.util.Log;

import com.jy.model.Item;

public class ChangeItemslistUtil {
	private static final String TAG = "ChangeItemslistUtil";
		//������������б�
		public static int changeItemList(String string,List<Item> itemList) {
			String[] items = string.split("\n");
			Log.d(TAG, "��Ʒ����" + (items.length));
			for (int i = 0; i < items.length; i++) {
				String[] info = items[i].split(",");
				Item item = new Item(info[0], info[1], info[2], info[3]);
				itemList.add(item);
			}
			return items.length;
		}

}
