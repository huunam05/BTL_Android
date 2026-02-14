package com.example.btl_android.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.btl_android.R;
import com.example.btl_android.model.dao.KyHocDAO;
import com.example.btl_android.model.dao.MonHocDAO;
import com.example.btl_android.model.entity.KyHoc;
import com.example.btl_android.model.entity.MonHoc;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class LayDuLieuWebViewActivity extends AppCompatActivity {

    private WebView wvLaydiem;
    private Button btnLaydiem;
    private ProgressBar progressBar;
    private MonHocDAO monHocDAO;
    private KyHocDAO kyHocDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lay_dl_tu_webview);

        initViews();
        setupWebView();

        monHocDAO = new MonHocDAO(this);
        kyHocDAO = new KyHocDAO(this);

        btnLaydiem.setOnClickListener(v -> runScrapingScript());
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        wvLaydiem = findViewById(R.id.wvLaydiem);
        btnLaydiem = findViewById(R.id.btnLaydiem);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupWebView() {
        WebSettings webSettings = wvLaydiem.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        wvLaydiem.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        wvLaydiem.setWebChromeClient(new WebChromeClient());
        wvLaydiem.addJavascriptInterface(new WebDataInterface(), "AndroidBridge");

        // Tự động mở trang web của trường
        wvLaydiem.loadUrl("https://sv.haui.edu.vn/student/result/examresult");
    }

    private void runScrapingScript() {
        // Script được tối ưu cho cấu trúc HTML HaUI thực tế
        String script = "javascript:(function() { " +
                "   var rows = document.querySelectorAll('tr.kTableRow, tr.kTableAltRow'); " +
                "   var data = []; " +
                "   rows.forEach(function(row) { " +
                "       var cols = row.querySelectorAll('td'); " +
                "       if (cols.length >= 14) { " + // Kiểm tra số lượng cột
                "           var item = { " +
                "               maHP: cols[1].innerText.trim(), " +
                "               tenMon: cols[3].innerText.trim(), " +
                "               soTinChi: cols[5].innerText.trim(), " +
                "               diemThi: cols[7].innerText.trim(), " +
                "               diemTK10: cols[11].innerText.trim(), " +
                "               diemTK4: cols[12].innerText.trim(), " +
                "               diemChu: cols[13].innerText.trim() " +
                "           }; " +
                "           if (item.tenMon !== '') data.push(item); " +
                "       } " +
                "   }); " +
                "   AndroidBridge.sendData(JSON.stringify(data)); " +
                "})();";
        wvLaydiem.loadUrl(script);
    }

    public class WebDataInterface {
        @JavascriptInterface
        public void sendData(String jsonData) {
            try {
                JSONArray jsonArray = new JSONArray(jsonData);
                if (jsonArray.length() == 0) {
                    runOnUiThread(() -> Toast.makeText(LayDuLieuWebViewActivity.this, 
                        "Không tìm thấy bảng điểm. Hãy đảm bảo bạn đã đăng nhập!", Toast.LENGTH_LONG).show());
                    return;
                }

                int targetKyHocId = getOrCreateWebKyHoc();
                int count = 0;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String tenMon = obj.getString("tenMon");
                    
                    // Chỉ lưu nếu môn này chưa có điểm (hoặc bạn có thể check theo mã HP)
                    MonHoc mh = new MonHoc();
                    mh.setKyHocId(targetKyHocId);
                    mh.setTenMon(tenMon);
                    
                    try {
                        mh.setSoTinChi((int) Float.parseFloat(obj.getString("soTinChi")));
                        mh.setDiemThi(parseSafeFloat(obj.getString("diemThi")));
                        mh.setDiemTongKet10(parseSafeFloat(obj.getString("diemTK10")));
                        mh.setDiemTongKet4(parseSafeFloat(obj.getString("diemTK4")));
                        mh.setDiemChu(obj.getString("diemChu"));
                        mh.setTrangThai("Đã qua"); 
                        
                        if (monHocDAO.insertMonHoc(mh) > 0) count++;
                    } catch (Exception e) {
                        Log.e("DATA_PARSE", "Lỗi dòng " + i + ": " + e.getMessage());
                    }
                }

                int finalCount = count;
                runOnUiThread(() -> {
                    Toast.makeText(LayDuLieuWebViewActivity.this, 
                        "Thành công! Đã lấy " + finalCount + " môn học.", Toast.LENGTH_LONG).show();
                    finish();
                });

            } catch (Exception e) {
                Log.e("SCRAPING_ERROR", "Lỗi JSON: " + e.getMessage());
            }
        }
    }

    private float parseSafeFloat(String val) {
        if (val == null || val.trim().isEmpty() || val.equals("-")) return 0.0f;
        try {
            return Float.parseFloat(val.replace(",", "."));
        } catch (Exception e) {
            return 0.0f;
        }
    }

    private int getOrCreateWebKyHoc() {
        List<KyHoc> list = kyHocDAO.getAllKyHoc(1);
        for (KyHoc kh : list) {
            if (kh.getTenKy().equals("Dữ liệu từ Website")) return kh.getId();
        }
        KyHoc newKy = new KyHoc();
        newKy.setSinhVienId(1);
        newKy.setTenKy("Dữ liệu từ Website");
        newKy.setTrangThai(true);
        return (int) kyHocDAO.insertKyHoc(newKy);
    }
}