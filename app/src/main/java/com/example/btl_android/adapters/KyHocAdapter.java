package com.example.btl_android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.btl_android.R;
import com.example.btl_android.model.entity.KyHoc;

import java.util.List;

public class KyHocAdapter extends BaseAdapter {

    private Context context;
    private List<KyHoc> listKyHoc;
    private LayoutInflater inflater;

    public KyHocAdapter(Context context, List<KyHoc> listKyHoc) {
        this.context = context;
        this.listKyHoc = listKyHoc;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listKyHoc != null ? listKyHoc.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return listKyHoc.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_ky_hoc, parent, false);
            holder = new ViewHolder();
            holder.tvTenKy = convertView.findViewById(R.id.tvTenKy);
            holder.tvTrangThai = convertView.findViewById(R.id.tvTrangThai);
            holder.tvGpaKy = convertView.findViewById(R.id.tvGpaKy);
            holder.tvTongTinChi = convertView.findViewById(R.id.tvTongTinChi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        KyHoc kyHoc = listKyHoc.get(position);

        // Set dữ liệu
        holder.tvTenKy.setText(kyHoc.getTenKy());

        // Set trạng thái
        if (kyHoc.isTrangThai()) {
            holder.tvTrangThai.setText("Đã xong");
            holder.tvTrangThai.setBackgroundResource(R.drawable.bg_trang_thai);
        } else {
            holder.tvTrangThai.setText("Đang học");
            holder.tvTrangThai.setBackgroundResource(R.drawable.bg_trang_thai_dang_hoc);
        }

        // Set GPA - nếu đang học thì hiển thị "--"
        if (kyHoc.isTrangThai()) {
            holder.tvGpaKy.setText(String.format("%.2f", kyHoc.getGpaKy()));
        } else {
            holder.tvGpaKy.setText("--");
        }

        // Set tổng tín chỉ
        holder.tvTongTinChi.setText(String.valueOf(kyHoc.getTongTinChiKy()));

        return convertView;
    }

    // ViewHolder pattern để tối ưu hiệu suất
    static class ViewHolder {
        TextView tvTenKy;
        TextView tvTrangThai;
        TextView tvGpaKy;
        TextView tvTongTinChi;
    }

    // Phương thức cập nhật dữ liệu
    public void updateData(List<KyHoc> newList) {
        this.listKyHoc = newList;
        notifyDataSetChanged();
    }
}