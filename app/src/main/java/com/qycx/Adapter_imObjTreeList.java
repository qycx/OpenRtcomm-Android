package com.qycx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter_imObjTreeList extends RecyclerView.Adapter<Adapter_imObjTreeList.SectionVH> {

    public static final int TYPE_GROUP = 1;
    public static final int TYPE_CONTACT = 2;

    public static final int BADGE_NONE = 0;
    public static final int BADGE_GROUP = 1;
    public static final int BADGE_ONLINE = 2;

    public static class TreeRow {
        public int type;
        public String title;
        public String badgeText;
        public int badgeStyle;
        public int imageRes;
        public long id;
        public boolean expanded;
        public boolean online;
    }

    public static class SectionData {
        public int categoryKey;
        public String title;
        public boolean expanded;
        public final List<TreeRow> items = new ArrayList<>();
    }

    public interface Listener {
        void onCategoryClick(int categoryKey);

        void onGroupClick(long grpId);

        void onGroupLongClick(long grpId);

        void onContactClick(long userId);
    }

    private final LayoutInflater inflater;
    private final List<SectionData> sections = new ArrayList<>();
    private Listener listener;

    public Adapter_imObjTreeList(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setSections(List<SectionData> data) {
        sections.clear();
        if (data != null) {
            sections.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SectionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.layout_imobj_section_card, parent, false);
        return new SectionVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionVH holder, int position) {
        SectionData section = sections.get(position);
        holder.container.removeAllViews();

        View header = inflater.inflate(R.layout.layout_imobj_section_header_inner, holder.container, false);
        ImageView ivHeaderExpand = header.findViewById(R.id.iv_expand);
        TextView tvTitle = header.findViewById(R.id.tv_section_title);
        tvTitle.setText(section.title);
        ivHeaderExpand.setImageResource(section.expanded
                ? R.drawable.ic_imobj_tree_chevron_down
                : R.drawable.ic_imobj_tree_chevron_right);
        header.findViewById(R.id.row_content).setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(section.categoryKey);
            }
        });
        holder.container.addView(header);

        if (!section.expanded || section.items.isEmpty()) {
            return;
        }

        for (int i = 0; i < section.items.size(); i++) {
            TreeRow row = section.items.get(i);
            View itemView = inflater.inflate(R.layout.layout_imobj_card_row_inner, holder.container, false);
            bindRowItem(itemView, row, i == section.items.size() - 1);
            holder.container.addView(itemView);
        }
    }

    private void bindRowItem(View itemView, TreeRow row, boolean lastRow) {
        View rowContent = itemView.findViewById(R.id.row_content);
        View rowDivider = itemView.findViewById(R.id.row_divider);
        ImageView ivExpand = itemView.findViewById(R.id.iv_expand);
        CardView cardView = itemView.findViewById(R.id.cardView1);
        ImageView ivImage = itemView.findViewById(R.id.Image);
        TextView tvBadge = itemView.findViewById(R.id.tv_badge);
        TextView tvTitle = itemView.findViewById(R.id.Text2);
        ImageView ivChevron = itemView.findViewById(R.id.iv_chevron);

        rowDivider.setVisibility(lastRow ? View.GONE : View.VISIBLE);

        if (row.type == TYPE_GROUP) {
            ivExpand.setVisibility(View.VISIBLE);
            ivExpand.setImageResource(row.expanded
                    ? R.drawable.ic_imobj_tree_chevron_down
                    : R.drawable.ic_imobj_tree_chevron_right);
            ivChevron.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
            lp.removeRule(RelativeLayout.ALIGN_PARENT_START);
            lp.addRule(RelativeLayout.END_OF, R.id.iv_expand);
            lp.leftMargin = dp(itemView, 8);
            cardView.setLayoutParams(lp);
        } else {
            ivExpand.setVisibility(View.GONE);
            ivChevron.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
            lp.removeRule(RelativeLayout.END_OF);
            lp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            lp.leftMargin = 0;
            cardView.setLayoutParams(lp);
        }

        ivImage.setImageResource(row.imageRes);
        tvTitle.setText(row.title != null ? row.title : "");

        if (row.badgeStyle == BADGE_GROUP) {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText(row.badgeText);
            tvBadge.setBackgroundResource(R.drawable.bg_imobj_badge_group);
            tvBadge.setTextColor(0xFFD48806);
        } else if (row.badgeStyle == BADGE_ONLINE) {
            tvBadge.setVisibility(View.VISIBLE);
            tvBadge.setText("在线");
            tvBadge.setBackgroundResource(R.drawable.bg_imobj_badge_online);
            tvBadge.setTextColor(0xFF00B578);
        } else {
            tvBadge.setVisibility(View.GONE);
        }

        final long rowId = row.id;
        final int rowType = row.type;
        rowContent.setOnClickListener(v -> {
            if (listener == null || rowId <= 0) return;
            if (rowType == TYPE_GROUP) {
                listener.onGroupClick(rowId);
            } else if (rowType == TYPE_CONTACT) {
                listener.onContactClick(rowId);
            }
        });

        rowContent.setOnLongClickListener(v -> {
            if (listener == null || rowId <= 0) return false;
            if (rowType == TYPE_GROUP) {
                listener.onGroupLongClick(rowId);
                return true;
            }
            return false;
        });
    }

    private static int dp(View view, int value) {
        return (int) (value * view.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    static final class SectionVH extends RecyclerView.ViewHolder {
        final LinearLayout container;

        SectionVH(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.section_container);
        }
    }
}
