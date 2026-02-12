package com.example.btl_android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.btl_android.R;
import com.example.btl_android.model.entity.MonHoc;

import java.util.List;

public class MonHocAdapter extends BaseAdapter {

    private Context context;
    private List<MonHoc> listMonHoc;
    private LayoutInflater inflater;

    public MonHocAdapter(Context context, List<MonHoc> listMonHoc) {
        this.context = context;
        this.listMonHoc = listMonHoc;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listMonHoc != null ? listMonHoc.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return listMonHoc.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mon_hoc, parent, false);
            holder = new ViewHolder();

            holder.tvTenMonHoc = convertView.findViewById(R.id.tvTenMonHoc);
            holder.tvSoTinChi = convertView.findViewById(R.id.tvSoTinChi);
            holder.tvTrangThaiMon = convertView.findViewById(R.id.tvTrangThaiMon);
            holder.tvDiemTx1 = convertView.findViewById(R.id.tvDiemTx1);
            holder.tvDiemTx2 = convertView.findViewById(R.id.tvDiemTx2);
            holder.tvDiemTx3 = convertView.findViewById(R.id.tvDiemTx3);
            holder.tvDiemThi = convertView.findViewById(R.id.tvDiemThi);
            holder.tvDiemTongKet10 = convertView.findViewById(R.id.tvDiemTongKet10);
            holder.tvDiemTongKet4 = convertView.findViewById(R.id.tvDiemTongKet4);
            holder.tvDiemChu = convertView.findViewById(R.id.tvDiemChu);
            holder.layoutTx3 = convertView.findViewById(R.id.layoutTx3);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MonHoc monHoc = listMonHoc.get(position);

        // Set tên môn và số tín chỉ
        holder.tvTenMonHoc.setText(monHoc.getTenMon());
        holder.tvSoTinChi.setText(monHoc.getSoTinChi() + " tín chỉ");

        // Set trạng thái môn
        String trangThai = monHoc.getTrangThai();
        holder.tvTrangThaiMon.setText(trangThai);

        // Màu sắc theo trạng thái
        switch (trangThai) {
            case "Đã qua":
            case "Qua môn":
                holder.tvTrangThaiMon.setBackgroundResource(R.drawable.bg_status_qua);
                break;
            case "Đang học":
                holder.tvTrangThaiMon.setBackgroundColor(Color.parseColor("#FF9800"));
                break;
            case "Trượt":
                holder.tvTrangThaiMon.setBackgroundColor(Color.parseColor("#F44336"));
                break;
            case "Cải thiện":
                holder.tvTrangThaiMon.setBackgroundColor(Color.parseColor("#9C27B0"));
                break;
            default:
                holder.tvTrangThaiMon.setBackgroundColor(Color.parseColor("#607D8B"));
                break;
        }

        // Set điểm thành phần
        holder.tvDiemTx1.setText(formatDiem(monHoc.getDiemTx1()));
        holder.tvDiemTx2.setText(formatDiem(monHoc.getDiemTx2()));

        // Xử lý TX3 (có thể null)
        if (monHoc.getDiemTx3() != null) {
            holder.layoutTx3.setVisibility(View.VISIBLE);
            holder.tvDiemTx3.setText(formatDiem(monHoc.getDiemTx3()));
        } else {
            holder.layoutTx3.setVisibility(View.GONE);
        }

        holder.tvDiemThi.setText(formatDiem(monHoc.getDiemThi()));

        // Set điểm tổng kết
        holder.tvDiemTongKet10.setText(formatDiem(monHoc.getDiemTongKet10()));
        holder.tvDiemTongKet4.setText(formatDiem(monHoc.getDiemTongKet4()));

        // Set điểm chữ
        String diemChu = monHoc.getDiemChu();
        holder.tvDiemChu.setText(diemChu != null && !diemChu.isEmpty() ? diemChu : "--");

        // Màu điểm tổng kết theo mức
        float diemTK = monHoc.getDiemTongKet4();
        if (diemTK >= 3.5) {
            holder.tvDiemTongKet10.setTextColor(Color.parseColor("#4CAF50")); // Xanh lá
        } else if (diemTK >= 2.5) {
            holder.tvDiemTongKet10.setTextColor(Color.parseColor("#FF9800")); // Cam
        } else if (diemTK >= 2.0) {
            holder.tvDiemTongKet10.setTextColor(Color.parseColor("#FFC107")); // Vàng
        } else {
            holder.tvDiemTongKet10.setTextColor(Color.parseColor("#F44336")); // Đỏ
        }

        return convertView;
    }

    // Format điểm
    private String formatDiem(float diem) {
        if (diem == 0) {
            return "--";
        }
        // Nếu là số nguyên thì không hiển thị phần thập phân
        if (diem == (int) diem) {
            return String.valueOf((int) diem);
        }
        return String.format("%.1f", diem);
    }

    // ViewHolder pattern
    static class ViewHolder {
        TextView tvTenMonHoc;
        TextView tvSoTinChi;
        TextView tvTrangThaiMon;
        TextView tvDiemTx1;
        TextView tvDiemTx2;
        TextView tvDiemTx3;
        TextView tvDiemThi;
        TextView tvDiemTongKet10;
        TextView tvDiemTongKet4;
        TextView tvDiemChu;
        LinearLayout layoutTx3;
    }

    // Cập nhật dữ liệu
    public void updateData(List<MonHoc> newList) {
        this.listMonHoc = newList;
        notifyDataSetChanged();
    }
}