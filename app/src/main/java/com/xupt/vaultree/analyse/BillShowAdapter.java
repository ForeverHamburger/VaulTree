package com.xupt.vaultree.analyse;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xupt.vaultree.Bill;
import com.xupt.vaultree.R;
import com.xupt.vaultree.bill.BillListActivity;

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


        // 添加从右侧滑入的动画
        Animation slideInAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f
        );
        slideInAnimation.setDuration(300); // 动画持续时间，单位毫秒
        slideInAnimation.setFillAfter(true); // 动画结束后保持最后状态
        holder.itemView.startAnimation(slideInAnimation);
        holder.bind(bill);
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

        public void bind(Bill bill) {
            itemView.setOnClickListener(view -> {
                Log.e("logaa", "bind: " );
                Context context = itemView.getContext();
                Intent intent = new Intent(context, BillListActivity.class);
                intent.putExtra("billId", bill.getId());
                if (context instanceof Activity) {
                    Log.e("logaa", "Activity: " );
                    ((Activity) context).startActivity(intent);
                } else {
                    Log.e("logaa", "Activityno " );
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }

    }
}