package org.ch.calendar.service;

import android.app.Dialog;
import android.app.ProgressDialog;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.content.Context;

import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;

import android.webkit.CookieSyncManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Display Foursquare authentication dialog.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class CalendarAuthenticateDialog extends Dialog {
	static final float[] DIMENSIONS_LANDSCAPE = {460, 260};
    static final float[] DIMENSIONS_PORTRAIT = {280, 420};
    static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                         						ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;

    private String mUrl;
    private CalendarAuthenticateDialogListener mListener;
    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;
    private TextView mTitle;
 
    private static final String TAG = "CalendarAuthenticateDialog";
    
	public CalendarAuthenticateDialog(Context context, String url, CalendarAuthenticateDialogListener listener) {
		super(context);
		mUrl		= url;
		mListener	= listener;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    Log.d(TAG, "onCreate");
	    super.onCreate(savedInstanceState);
        
        mSpinner = new ProgressDialog(getContext());
        
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        mContent = new LinearLayout(getContext());
        
        mContent.setOrientation(LinearLayout.VERTICAL);
        
        setUpTitle();
        setUpWebView();
        
        Display display 	= getWindow().getWindowManager().getDefaultDisplay();
        final float scale 	= getContext().getResources().getDisplayMetrics().density;
        float[] dimensions 	= (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
        
        addContentView(mContent, new FrameLayout.LayoutParams((int) (dimensions[0] * scale + 0.5f),
        							(int) (dimensions[1] * scale + 0.5f)));
        
        CookieSyncManager.createInstance(getContext()); 
    	
    	CookieManager cookieManager = CookieManager.getInstance();
    	
    	//cookieManager.removeAllCookie();
    }
	
	 private void setUpTitle() {
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        
	        //Drawable icon = getContext().getResources().getDrawable(R.drawable.ic_launcher);
	        
	        mTitle = new TextView(getContext());
	        
	        mTitle.setText("Foursquare");
	        mTitle.setTextColor(Color.WHITE);
	        mTitle.setTypeface(Typeface.DEFAULT_BOLD);
	        mTitle.setBackgroundColor(0xFF0cbadf);
	        mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
	        mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
	        mTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	        
	        mContent.addView(mTitle);
	    }

	    private void setUpWebView() {
	        mWebView = new WebView(getContext());
	        
	        mWebView.setVerticalScrollBarEnabled(false);
	        mWebView.setHorizontalScrollBarEnabled(false);
	        mWebView.setWebViewClient(new TwitterWebViewClient());
	        mWebView.getSettings().setJavaScriptEnabled(true);
	        mWebView.loadUrl(mUrl);
	        mWebView.setLayoutParams(FILL);
	        mWebView.setScrollContainer(true);
	        
	        mContent.addView(mWebView);
	    }

	    private class TwitterWebViewClient extends WebViewClient {

	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        	Log.d(TAG, "Redirecting URL " + url);
	        	
	        	view.loadUrl(url);
	        	return false;
	        }

	        
	        @Override
	        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	        	Log.e(TAG, "Page error: " + description);
	        	
	            super.onReceivedError(view, errorCode, description, failingUrl);
	      
	            mListener.onError(description);
	            
	            CalendarAuthenticateDialog.this.dismiss();
	        }

	        @Override
	        public void onPageStarted(WebView view, String url, Bitmap favicon) {
	            Log.d(TAG, "onPageStarted: " + url);
	            super.onPageStarted(view, url, favicon);
	            mSpinner.show();
	        }

	        @Override
	        public void onPageFinished(WebView view, String url) {
	            Log.d(TAG, "onPageFinished: Loaded URL " + url);
	            
                super.onPageFinished(view, url);
	            String title = mWebView.getTitle();
	            if (title != null && title.length() > 0) {
	                mTitle.setText(title);
	            }
	            
	            mSpinner.dismiss();
	            
	            if (view.getTitle() != null && view.getTitle().startsWith("Success")) {
                    
	                String urls[] = view.getTitle().replaceAll(" ", "").split("=");
                    
                    mListener.onComplete(urls[1]);
                    
                    CalendarAuthenticateDialog.this.dismiss();
                }  
	            
	        }

	    }
	    

}