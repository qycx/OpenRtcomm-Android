package com.qycx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qycx.base.TableItem;
import com.qycx.base.displayShortTime;
import com.qycx.base.qyDefs;

import java.util.List;

public class Adapter_msgList extends RecyclerView.Adapter<Adapter_msgList.CardVH> {

    public interface Listener {
        void onItemClick(int position);
    }

    private final LayoutInflater inflater;
    private final List<TableItem.TableItem_msg> items;
    private Listener listener;

    public Adapter_msgList(LayoutInflater inflater, List<TableItem.TableItem_msg> items) {
        this.inflater = inflater;
        this.items = items;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.layout_imobj_section_card, parent, false);
        return new CardVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardVH holder, int position) {
        holder.container.removeAllViews();

        if (items.isEmpty()) {
            inflater.inflate(R.layout.layout_msg_empty_inner, holder.container, true);
            return;
        }

        View titleView = inflater.inflate(R.layout.layout_msg_section_title_inner, holder.container, false);
        TextView tvTitle = titleView.findViewById(R.id.tv_msg_section_title);
        tvTitle.setText(String.format("最近消息(%d)", items.size()));
        holder.container.addView(titleView);

        for (int i = 0; i < items.size(); i++) {
            TableItem.TableItem_msg item = items.get(i);
            View rowView = inflater.inflate(R.layout.layout_msg_row_inner, holder.container, false);
            bindRow(rowView, item, i == items.size() - 1, i);
            holder.container.addView(rowView);
        }
    }

    private void bindRow(View rowView, TableItem.TableItem_msg item, boolean lastRow, int position) {
        View rowDivider = rowView.findViewById(R.id.row_divider);
        rowDivider.setVisibility(lastRow ? View.GONE : View.VISIBLE);

        ImageView iv = rowView.findViewById(R.id.Image);
        TextView tvName = rowView.findViewById(R.id.Text2);
        TextView tvTime = rowView.findViewById(R.id.Time);
        TextView tvPreview = rowView.findViewById(R.id.Text3);
        TextView tvBadge = rowView.findViewById(R.id.tv_badge);
        View rowContent = rowView.findViewById(R.id.row_content);

        iv.setImageResource(item.ImageResourceId);

        boolean isGroup = item.uiObjType == qyDefs.Consts.CONST_objType_imGrp;
        if (isGroup) {
            tvName.setText(item.grpName != null ? item.grpName : String.valueOf(item.ui64Id));
            tvBadge.setVisibility(View.VISIBLE);
        } else {
            String name = item.syr;
            if (name == null || name.isEmpty()) {
                name = String.valueOf(item.ui64Id);
            }
            tvName.setText(name);
            tvBadge.setVisibility(View.GONE);
        }

        tvTime.setText(displayShortTime.qyFuncs_Droid.displayShortTime(item.tRecvTime));

        String speaker = item.speaker != null ? item.speaker : "";
        String msg = item.msg != null ? item.msg : "";
        if (speaker.isEmpty()) {
            tvPreview.setText(msg);
        } else {
            tvPreview.setText(String.format("%s：%s", speaker, msg));
        }

        rowContent.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static final class CardVH extends RecyclerView.ViewHolder {
        final LinearLayout container;

        CardVH(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.section_container);
        }
    }
}
