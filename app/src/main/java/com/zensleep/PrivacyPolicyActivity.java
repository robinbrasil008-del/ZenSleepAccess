package com.zensleep;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        WebView webView = findViewById(R.id.webPolicy);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.loadUrl("file:///android_asset/privacy_policy.html");
    }
}
