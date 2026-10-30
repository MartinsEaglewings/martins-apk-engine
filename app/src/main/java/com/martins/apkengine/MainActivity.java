package com.martins.apkengine;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.view.ViewGroup;
import android.graphics.Color;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private WebView webView;

    private String readStartUrl() {
        try (InputStream input = getAssets().open("config.json")) {
            byte[] bytes = new byte[input.available()];
            int count = input.read(bytes);
            JSONObject config = new JSONObject(new String(bytes, 0, count, StandardCharsets.UTF_8));
            String url = config.optString("websiteUrl", "");
            if (url.startsWith("https://")) return url;
        } catch (Exception ignored) { }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(16, 24, 40));
        getWindow().setNavigationBarColor(Color.BLACK);
        String startUrl = readStartUrl();
        if (startUrl.isEmpty()) {
            TextView message = new TextView(this);
            message.setText("This app has no valid website configuration.");
            message.setTextSize(18);
            message.setPadding(32, 32, 32, 32);
            setContentView(message);
            return;
        }
        webView = new WebView(this);
        webView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setSupportZoom(false);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        setContentView(webView);
        webView.loadUrl(startUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
