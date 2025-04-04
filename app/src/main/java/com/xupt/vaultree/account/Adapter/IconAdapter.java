package com.xupt.vaultree.account.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xupt.vaultree.R;
import com.xupt.vaultree.account.database.IconItem;
import com.xupt.vaultree.databinding.ItemIconBinding;

import java.util.ArrayList;
import java.util.List;
public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {
    private List<IconItem> iconList;
    private OnItemClickListener listener;
    private int selectedPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(IconItem item, int position);
    }

    public IconAdapter(List<IconItem> iconList, OnItemClickListener listener) {
        this.iconList = iconList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIconBinding binding = ItemIconBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new IconViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        IconItem currentItem = iconList.get(position);
        holder.bind(currentItem, position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;
            int oldSelected = selectedPosition;
            selectedPosition = adapterPosition;
            if (oldSelected != -1) notifyItemChanged(oldSelected);
            notifyItemChanged(selectedPosition);

            listener.onItemClick(iconList.get(adapterPosition), adapterPosition);
        });
    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    public class IconViewHolder extends RecyclerView.ViewHolder {
        private final ItemIconBinding binding;

        public IconViewHolder(ItemIconBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(IconItem item, boolean isSelected) {
            binding.iconImageView1.setImageResource(item.getIconResId());
            binding.iconNameTextView.setText(item.getIconName());
            itemView.setSelected(isSelected);
        }
    }

    // 更新数据方法
    public void updateList(List<IconItem> newList) {
        iconList = newList;
        selectedPosition = -1; // 重置选中状态
        notifyDataSetChanged();
    }
}