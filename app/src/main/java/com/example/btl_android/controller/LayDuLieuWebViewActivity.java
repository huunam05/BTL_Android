package com.example.btl_android.controller;

import android.content.DialogInterface;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.btl_android.R;
import com.example.btl_android.model.dao.KyHocDAO;
import com.example.btl_android.model.dao.MonHocDAO;
import com.example.btl_android.model.entity.KyHoc;
import com.example.btl_android.model.entity.MonHoc;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        wvLaydiem.loadUrl("https://sv.haui.edu.vn");
    }

    private void runScrapingScript() {
        String script = "javascript:(function() { " +
                "   var rows = document.querySelectorAll('tr.kTableRow, tr.kTableAltRow'); " +
                "   var data = []; " +
                "   rows.forEach(function(row) { " +
                "       var cols = row.querySelectorAll('td'); " +
                "       if (cols.length >= 14) { " +
                "           var item = { " +
                "               maHP: cols[1].innerText.trim(), " +
                "               tenMon: cols[3].innerText.trim(), " +
                "               soTinChi: cols[5].innerText.trim(), " +
                "               diemThi: cols[7].innerText.trim(), " +
                "               diemTK10: cols[11].innerText.trim(), " +
                "               diemTK4: cols[12].innerText.trim(), " +
                "               diemChu: cols[13].innerText.trim() " +
                "           }; " +
                "           if (item.tenMon !== '' && item.diemTK10 !== '') data.push(item); " +
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
                            "Không tìm thấy dữ liệu. Hãy chắc chắn bạn đang ở trang Kết quả học tập.", Toast.LENGTH_LONG).show());
                    return;
                }

                // Sử dụng Map để lọc trùng môn, chỉ giữ lại môn có điểm cao nhất
                Map<String, MonHoc> distinctMonHocMap = new HashMap<>();
                int ignoredCount = 0;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String tenMon = obj.getString("tenMon");

                    if (isIgnoredSubject(tenMon)) {
                        ignoredCount++;
                        continue;
                    }

                    float newDiemTK10 = parseSafeFloat(obj.getString("diemTK10"));

                    // Logic lọc trùng và giữ điểm cao nhất
                    if (distinctMonHocMap.containsKey(tenMon)) {
                        if (newDiemTK10 > distinctMonHocMap.get(tenMon).getDiemTongKet10()) {
                            distinctMonHocMap.put(tenMon, createMonHocFromObj(obj));
                        }
                    } else {
                        distinctMonHocMap.put(tenMon, createMonHocFromObj(obj));
                    }
                }

                List<MonHoc> monHocList = new ArrayList<>(distinctMonHocMap.values());
                StringBuilder dataToShow = new StringBuilder();
                for (MonHoc mh : monHocList) {
                    dataToShow.append("- ").append(mh.getTenMon()).append(" (").append(mh.getSoTinChi()).append(" TC) - Điểm: ").append(mh.getDiemChu()).append("\n");
                }

                String finalSummary = "Đã lọc và lấy " + monHocList.size() + " môn học tính lũy.\n"
                        + "(Đã loại bỏ " + ignoredCount + " môn không tính điểm và các lần học lại điểm thấp)\n\n"
                        + dataToShow.toString();

                runOnUiThread(() -> showConfirmationDialog(monHocList, finalSummary));

            } catch (Exception e) {
                Log.e("SCRAPING_ERROR", "Lỗi xử lý JSON: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(LayDuLieuWebViewActivity.this, "Có lỗi xảy ra khi xử lý dữ liệu.", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private MonHoc createMonHocFromObj(JSONObject obj) throws org.json.JSONException {
        MonHoc mh = new MonHoc();
        mh.setTenMon(obj.getString("tenMon"));
        mh.setSoTinChi((int) parseSafeFloat(obj.getString("soTinChi")));
        mh.setDiemThi(parseSafeFloat(obj.getString("diemThi")));
        mh.setDiemTongKet10(parseSafeFloat(obj.getString("diemTK10")));
        mh.setDiemTongKet4(parseSafeFloat(obj.getString("diemTK4")));
        mh.setDiemChu(obj.getString("diemChu"));
        mh.setTrangThai("Đã qua");
        return mh;
    }

    private boolean isIgnoredSubject(String tenMon) {
        String name = tenMon.toLowerCase();

        // Môn này có nhiều tên biến thể (cơ bản, nâng cao) và thường không tính vào tín chỉ tích luỹ chính thức ở HaUI
        if (name.contains("kỹ năng sử dụng công nghệ thông tin")) {
            return true;
        }
        if (name.contains("tiếng anh") && name.contains("cơ bản")) {
            return true;
        }

        String[] ignoredKeywords = {
            "đường lối qp&an", "quân sự chung", "kỹ thuật chiến đấu bộ binh", "công tác quốc phòng", "an ninh",
            "aerobic", "bơi", "bóng bàn", "bóng chuyền", "bóng đá", "bóng ném", "bóng rổ",
            "cầu lông", "cầu mây", "đá cầu", "futsal", "karate", "khiêu vũ", "pencak silat", "tennis", "thể dục"
        };

        for (String keyword : ignoredKeywords) {
            if (name.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private void showConfirmationDialog(List<MonHoc> monHocList, String data) {
        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setText(data);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding / 2, padding, padding / 2);
        scrollView.addView(textView);

        new AlertDialog.Builder(this)
                .setTitle("Kiểm tra dữ liệu tích lũy")
                .setView(scrollView)
                .setPositiveButton("Xác nhận Lưu", (dialog, which) -> {
                    saveDataToDatabase(monHocList);
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void saveDataToDatabase(List<MonHoc> monHocList) {
        int targetKyHocId = getOrCreateWebKyHoc();

        // Xóa sạch dữ liệu cũ trong kỳ "Dữ liệu từ Website" để làm mới
        monHocDAO.deleteMonHocByKyHocId(targetKyHocId);

        int count = 0;
        for (MonHoc mh : monHocList) {
            mh.setKyHocId(targetKyHocId);
            if (monHocDAO.insertMonHoc(mh) > 0) {
                count++;
            }
        }
        Toast.makeText(this, "Đã cập nhật mới " + count + " môn học!", Toast.LENGTH_LONG).show();
        finish();
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
