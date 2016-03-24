package com.jy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewActivity extends Activity {
	private WebView mywebview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_layout);
		mywebview = (WebView) findViewById(R.id.web_view);
		// mywebview.getSettings().setJavaScriptEnabled(true);
		mywebview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		Intent intent=getIntent();	
		mywebview.loadUrl(intent.getStringExtra("itemlink"));
	}

}
