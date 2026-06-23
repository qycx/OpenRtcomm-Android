package com.qycx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qycx.base.TableItem;

import java.util.ArrayList;
import java.util.List;

public class Adapter_imObjTreeList extends RecyclerView.Adapter<Adapter_imObjTreeList.VH> {

    public static final int TYPE_CATEGORY = 0;
    public static final int TYPE_GROUP = 1;
    public static final int TYPE_CONTACT = 2;

    public static class TreeRow {
        public int type;
        public String title;
        public String subtitle;
        public int imageRes;
        public long id;
        public boolean expanded;
        public boolean online;
        public int indentDp;
    }

    public interface Listener {
        void onCategoryClick(int categoryKey);

        void onGroupClick(long grpId);

        void onGroupLongClick(long grpId);

        void onContactClick(long userId);
    }

    private final LayoutInflater inflater;
    private final List<TreeRow> rows = new ArrayList<>();
    private Listener listener;

    public Adapter_imObjTreeList(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setRows(List<TreeRow> newRows) {
        rows.clear();
        if (newRows != null) {
            rows.addAll(newRows);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.layout_imobj_tree_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        TreeRow row = rows.get(position);
        float density = h.itemView.getResources().getDisplayMetrics().density;
        int padStart = (int) (row.indentDp * density);
        h.rowContent.setPadding(padStart, h.rowContent.getPaddingTop(), h.rowContent.getPaddingRight(), h.rowContent.getPaddingBottom());

        if (row.type == TYPE_CATEGORY || row.type == TYPE_GROUP) {
            h.ivExpand.setVisibility(View.VISIBLE);
            h.ivExpand.setImageResource(row.expanded
                    ? R.drawable.ic_imobj_tree_chevron_down
                    : R.drawable.ic_imobj_tree_chevron_right);
        } else {
            h.ivExpand.setVisibility(View.INVISIBLE);
        }

        if (row.type == TYPE_CATEGORY) {
            h.cardView.setVisibility(View.GONE);
            h.tv1.setVisibility(View.GONE);
            h.tv2.setText(row.title);
            h.tv2.setTextSize(18);
        } else {
            h.cardView.setVisibility(View.VISIBLE);
            h.ivImage.setImageResource(row.imageRes);
            h.tv1.setVisibility(View.VISIBLE);
            h.tv1.setText(row.subtitle != null ? row.subtitle : "");
            h.tv2.setText(row.title != null ? row.title : "");
            h.tv2.setTextSize(18);
            h.vOnlineDot.setVisibility(row.online ? View.VISIBLE : View.GONE);
        }

        h.rowContent.setOnClickListener(v -> {
            if (listener == null) return;
            int pos = h.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            TreeRow clicked = rows.get(pos);
            if (clicked.type == TYPE_CATEGORY) {
                listener.onCategoryClick((int) clicked.id);
            } else if (clicked.type == TYPE_GROUP) {
                listener.onGroupClick(clicked.id);
            } else if (clicked.type == TYPE_CONTACT) {
                listener.onContactClick(clicked.id);
            }
        });

        h.rowContent.setOnLongClickListener(v -> {
            if (listener == null) return false;
            int pos = h.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return false;
            TreeRow clicked = rows.get(pos);
            if (clicked.type == TYPE_GROUP) {
                listener.onGroupLongClick(clicked.id);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static final class VH extends RecyclerView.ViewHolder {
        final View rowContent;
        final ImageView ivExpand;
        final View cardView;
        final ImageView ivImage;
        final View vOnlineDot;
        final TextView tv1;
        final TextView tv2;

        VH(@NonNull View itemView) {
            super(itemView);
            rowContent = itemView.findViewById(R.id.row_content);
            ivExpand = itemView.findViewById(R.id.iv_expand);
            cardView = itemView.findViewById(R.id.cardView1);
            ivImage = itemView.findViewById(R.id.Image);
            vOnlineDot = itemView.findViewById(R.id.v_online_dot);
            tv1 = itemView.findViewById(R.id.Text1);
            tv2 = itemView.findViewById(R.id.Text2);
        }
    }
}
