package com.kodi.recap.kodirecap;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.ByteArrayInputStream;

public class WebClientIntercepter extends WebViewClient {
    private String site_key = "";
    private String domain = "";
    private TextView textView = null;

    public WebClientIntercepter(String domain, String key, TextView textView)
    {
        this(domain,key);
        this.textView = textView;
    }

    public WebClientIntercepter(String domain, String key)
    {
        super();
        this.site_key = key;
        this.domain = domain;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if(Uri.parse(url).getHost() == null)
            return super.shouldInterceptRequest(view, url);
        if (Uri.parse(url).getHost().equals(this.domain)) {
            return preparedResponse();
        }
        return super.shouldInterceptRequest(view, url);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if(request.getUrl().getHost() == null)
            return super.shouldInterceptRequest(view, request);
        if (request.getUrl().getHost().equals(this.domain)) {
            return preparedResponse();
        }
        return super.shouldInterceptRequest(view, request);
    }

    private WebResourceResponse preparedResponse()
    {
        String res = utils.getInterceptorHtml();
        res = res.replace("{site-key}",this.site_key);
        return new WebResourceResponse("text/html","UTF-8",new ByteArrayInputStream(res.getBytes()));
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if(this.textView != null)
            this.textView.setText(view.getUrl());
    }
}
