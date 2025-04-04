package com.xupt.vaultree.analyse;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xupt.vaultree.Bill;
import com.xupt.vaultree.R;

import java.util.List;

public class BillShowAdapter extends RecyclerView.Adapter<BillShowAdapter.BillShowViewHolder> {

    private List<Bill> billInfoList;
    private Context context;

    public BillShowAdapter(List<Bill> bills, Context context) {
        this.billInfoList = bills;
        this.context = context;
    }

    @NonNull
    @Override
    public BillShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_bill, parent, false);
        return new BillShowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillShowViewHolder holder, int position) {
        Bill bill = billInfoList.get(position);

        if (bill.isIncome()) {
            holder.payMoney.setTextColor(context.getResources().getColor(R.color.orange));
            holder.payMoney.setText("+" + bill.getAmount());
        } else {
            holder.payMoney.setTextColor(context.getResources().getColor(R.color.black));
            holder.payMoney.setText("-" + bill.getAmount());
        }


        long dateMillis = bill.getDateMillis();
        TimestampConverter converter = new TimestampConverter(dateMillis);
        String formattedDate = converter.convertTimestampToString("MM-dd");

        holder.payTime.setText(formattedDate);
        holder.payName.setText(bill.getCategoryIconName());
        holder.image.setImageResource(bill.getCategoryIconResId());
    }

    @Override
    public int getItemCount() {
        return billInfoList.size();
    }

    public static class BillShowViewHolder extends RecyclerView.ViewHolder {
        TextView payName;
        TextView payTime;
        TextView payMoney;
        ImageView image;

        public BillShowViewHolder(@NonNull View itemView) {
            super(itemView);
            payName = itemView.findViewById(R.id.tv_pay_name);
            payTime = itemView.findViewById(R.id.tv_pay_time);
            payMoney = itemView.findViewById(R.id.tv_pay_money);
            image = itemView.findViewById(R.id.iv_edit_user_head);
        }

    }
}