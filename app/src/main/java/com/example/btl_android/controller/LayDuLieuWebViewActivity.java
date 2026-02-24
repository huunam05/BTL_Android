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
        // Script cải tiến: Lấy cả môn chưa có điểm (diemTK10 rỗng hoặc '-')
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
                "           if (item.tenMon !== '' && !isNaN(parseFloat(item.soTinChi))) { " +
                "               data.push(item); " +
                "           } " +
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
                            "Không tìm thấy dữ liệu trên bảng điểm.", Toast.LENGTH_LONG).show());
                    return;
                }

                // Sử dụng Map để lọc trùng môn, ưu tiên môn có điểm cao nhất hoặc môn mới nhất (chưa thi)
                Map<String, MonHoc> distinctMonHocMap = new HashMap<>();
                int ignoredCount = 0;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String maHP = obj.getString("maHP");
                    String tenMon = obj.getString("tenMon");

                    // BỘ LỌC: Loại bỏ môn Quân sự/Thể chất/TA cơ bản/Kỹ năng CNTT
                    if (isIgnoredSubject(maHP, tenMon)) {
                        ignoredCount++;
                        continue;
                    }

                    String rawDiemTK10 = obj.getString("diemTK10");
                    float diemTK10 = parseSafeFloat(rawDiemTK10);
                    float diemThi = parseSafeFloat(obj.getString("diemThi"));

                    MonHoc mh = new MonHoc();
                    mh.setTenMon(tenMon);
                    mh.setSoTinChi((int) parseSafeFloat(obj.getString("soTinChi")));
                    mh.setDiemThi(diemThi);
                    mh.setDiemTongKet10(diemTK10);
                    mh.setDiemTongKet4(parseSafeFloat(obj.getString("diemTK4")));
                    mh.setDiemChu(obj.getString("diemChu"));
                    
                    // Xác định trạng thái dựa trên việc có điểm thi hay chưa
                    if (diemThi <= 0 && (rawDiemTK10.isEmpty() || rawDiemTK10.equals("-"))) {
                        mh.setTrangThai("Đang học");
                    } else {
                        mh.setTrangThai("Đã qua");
                    }

                    // Kiểm tra trùng môn: 
                    // 1. Nếu chưa có môn này trong map -> thêm vào.
                    // 2. Nếu đã có, chỉ ghi đè nếu môn mới có điểm cao hơn.
                    // 3. Nếu cả hai đều chưa có điểm, giữ lại môn hiện tại (hoặc môn sau cùng).
                    if (distinctMonHocMap.containsKey(tenMon)) {
                        if (diemTK10 > distinctMonHocMap.get(tenMon).getDiemTongKet10()) {
                            distinctMonHocMap.put(tenMon, mh);
                        }
                    } else {
                        distinctMonHocMap.put(tenMon, mh);
                    }
                }

                List<MonHoc> monHocList = new ArrayList<>(distinctMonHocMap.values());
                int countChuaThi = 0;
                StringBuilder dataToShow = new StringBuilder();
                for (MonHoc mh : monHocList) {
                    String status = mh.getDiemChu().isEmpty() || mh.getDiemChu().equals("-") ? "[CHƯA THI]" : "(" + mh.getDiemChu() + ")";
                    if (status.equals("[CHƯA THI]")) countChuaThi++;
                    dataToShow.append("- ").append(mh.getTenMon()).append(" ").append(status).append("\n");
                }

                String finalSummary = "Tổng cộng: " + monHocList.size() + " môn học.\n" 
                                    + "- " + (monHocList.size() - countChuaThi) + " môn đã có điểm.\n"
                                    + "- " + countChuaThi + " môn chưa có điểm thi.\n"
                                    + "(Đã loại bỏ " + ignoredCount + " môn không tính tích lũy)\n\n"
                                    + dataToShow.toString();

                runOnUiThread(() -> showConfirmationDialog(monHocList, finalSummary));

            } catch (Exception e) {
                Log.e("SCRAPING_ERROR", "Lỗi: " + e.getMessage());
            }
        }
    }

    private boolean isIgnoredSubject(String maHP, String tenMon) {
        String code = maHP.toUpperCase();
        String name = tenMon.toLowerCase();

        // 1. Loại bỏ môn theo yêu cầu đặc biệt
        if (name.contains("kỹ năng sử dụng công nghệ thông tin")) return true;

        // 2. Lọc theo mã học phần (Chuẩn HaUI)
        if (code.startsWith("DC") || code.startsWith("PE")) return true;

        // 3. Lọc Ngoại ngữ cơ bản
        if (name.contains("tiếng") && name.contains("cơ bản")) return true;

        // 4. Lọc theo từ khóa dự phòng
        String[] ignoredKeywords = {
            "qp&an", "quân sự", "thể dục", "bóng", "bơi", "cầu lông", "đá cầu"
        };
        for (String k : ignoredKeywords) {
            if (name.contains(k)) return true;
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
                .setTitle("Xác nhận dữ liệu học tập")
                .setView(scrollView)
                .setPositiveButton("Lưu vào máy", (dialog, which) -> {
                    saveDataToDatabase(monHocList);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveDataToDatabase(List<MonHoc> monHocList) {
        int targetKyHocId = getOrCreateWebKyHoc();
        monHocDAO.deleteMonHocByKyHocId(targetKyHocId);

        int count = 0;
        for (MonHoc mh : monHocList) {
            mh.setKyHocId(targetKyHocId);
            if (monHocDAO.insertMonHoc(mh) > 0) count++;
        }
        Toast.makeText(this, "Đã lưu " + count + " môn học thành công!", Toast.LENGTH_LONG).show();
        finish();
    }

    private float parseSafeFloat(String val) {
        if (val == null || val.trim().isEmpty() || val.equals("-")) return 0.0f;
        try { return Float.parseFloat(val.replace(",", ".")); } 
        catch (Exception e) { return 0.0f; }
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