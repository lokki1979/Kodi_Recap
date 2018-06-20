package com.kodi.recap.kodirecap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;

public class CaptchaSolve extends AppCompatActivity {

    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_captcha_solve);

        Intent i = getIntent();
        String serverString = i.getStringExtra("server");

        JSONObject d = null;
        String domain = "";
        String msg = "";
        String key = "";
        try {
            d = new JSONObject(serverString);
            domain = d.getString("domain");
            msg = d.getString("message");
            key = d.getString("key");
        } catch (JSONException e) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED,returnIntent);
            finish();
        }


        CookieHandler.setDefault(new CookieManager());
        mWebView = (WebView) findViewById(R.id.activity_main_webview);

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webSettings.setSupportZoom(true);
        webSettings.setJavaScriptEnabled(true);

        mWebView.addJavascriptInterface(this, "Android");
        mWebView.setWebViewClient(new WebClientIntercepter(domain,key));
        mWebView.loadUrl("http://"+domain);
        if(msg.length() > 0)
        {
            Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
        }
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void sendToken(String toast) {
//        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Erfolg!", Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("key",toast);
        returnIntent.putExtra("index",getIntent().getLongExtra("index",0));
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
