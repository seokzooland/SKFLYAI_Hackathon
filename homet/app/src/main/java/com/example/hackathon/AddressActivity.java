package com.example.hackathon;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class AddressActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "https://5450-2001-e60-8753-a52f-45ad-2ab8-d938-98d4.jp.ngrok.io/accounts";

    public WebView SearchAddress;
    private ProgressBar ProgressBar;

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("data", data);
            intent.putExtras(extra);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        SearchAddress = (WebView) findViewById(R.id.wv_address);
        SearchAddress.getSettings().setJavaScriptEnabled(true);
        SearchAddress.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        SearchAddress.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                SearchAddress.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });
//        SearchAddress.loadUrl("file:///android_asset/webview/address.html");
        SearchAddress.loadUrl(IP_ADDRESS + "/address");
        // 경고! 위 주소대로 서비스에 사용하시면 파일이 삭제됩니다.
        // 꼭 자신의 웹 서버에 해당 파일을 복사해서 주소를 변경해 사용하세요.
    }
}