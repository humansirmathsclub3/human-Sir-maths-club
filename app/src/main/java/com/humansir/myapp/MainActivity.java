package com.humansir.myapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.CookieManager;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String LAST_CACHE_CLEAR_KEY = "last_cache_clear";
    private static final long ONE_WEEK_MS = 7L * 24 * 60 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // Handle Cache Clearing (1 week)
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long lastClear = prefs.getLong(LAST_CACHE_CLEAR_KEY, 0);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClear > ONE_WEEK_MS) {
            webView.clearCache(true);
            prefs.edit().putLong(LAST_CACHE_CLEAR_KEY, currentTime).apply();
        }

        // Enable Cookies (including third-party for Google Login)
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl("https://script.google.com/macros/s/AKfycbyjeN_3GMou0x45M1nC_d5IALv1kc8V_W7tZtF57OllKuJoWfiCfaBrIDHzszvbCdMd8w/exec");
        }

        android.widget.TextView privacyLink = findViewById(R.id.privacy_policy_link);
        String privacyUrl = "https://policies.google.com/privacy";
        if (privacyUrl != null && !privacyUrl.trim().isEmpty()) {
            privacyLink.setVisibility(android.view.View.VISIBLE);
            privacyLink.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(privacyUrl));
                    startActivity(browserIntent);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
