package com.qycx.anc;

import static com.qycx.anc.Consts.CONST_usRunningStatus_online;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.qycx.R;

import java.util.List;

public class ConfMemberAdapter extends BaseAdapter {
    private Context context;
    private Integer layoutId;
    private List<ConfMember> memberList;

    public ConfMemberAdapter(Context context, Integer layoutId, List<ConfMember> memberList) {
        this.context = context;
        this.layoutId = layoutId;
        this.memberList = memberList;
    }

    @Override
    public int getCount() {
        return memberList == null ? 0 : memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.iv_avatar = convertView.findViewById(R.id.avatar);
            holder.tv_desc = convertView.findViewById(R.id.desc);
            holder.tv_status = convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ConfMember member = memberList.get(position);
        // 关键修改：根据选中状态和成员类型设置不同的头像
        boolean isSelected = member.isSelected();
        if (isSelected) {
            // 用户被选中
            if (member.iConfMemType == Consts.CONST_confMemType_speaker) {
                // 正在发言的用户被选中，显示被选中的发言人头像
                holder.iv_avatar.setImageResource(R.mipmap.avatar_speaker_selected);
            } else {
                //
                if  (  member.usRunningStatus== CONST_usRunningStatus_online)  {
                    //
                    holder.iv_avatar.setImageResource(R.mipmap.avatar2);
                }
                else {
                    // 未发言的用户被选中，显示被选中的普通用户头像
                    holder.iv_avatar.setImageResource(R.mipmap.avatar1_selected);
                }
            }
        } else {
            //
            // 用户未被选中，显示原来的头像
            if  (  member.usRunningStatus  ==  CONST_usRunningStatus_online )   {
                //
                holder.iv_avatar.setImageResource(R.mipmap.avatar2);
            }
            else {
                //
                holder.iv_avatar.setImageResource(member.getAvatar());
            }
        }
        // 原有代码：设置描述和状态文本
        String desc = member.desc;
        if (member.uiObjType != 0) {
            desc += " " + member.uiObjType;
        }
        //
        if  (  true  ) {
            //desc += ":" + member.usIndex_activeMem;
        }
        //
        holder.tv_desc.setText(desc);
        //
        String statusStr = member.getStatusStr();
        //
        if(member.usRunningStatus  ==  CONST_usRunningStatus_online) statusStr +=" 在线";

        //
        long curTickCnt  =  myGetTickCount(null);
        long  iDiffInMs  = curTickCnt - member.conf_ui64Id_dwTickCnt_refreshed;
        if  (  iDiffInMs  < 40000)  {
            if  (  member.conf_ui64Id!=0) {
                statusStr += " 参会"  +  member.conf_ui64Id;
            }
        }


        //
        holder.tv_status.setText(statusStr);

        // 关键修改：根据三种状态设置不同背景色
        if (isSelected) {
            // 被选中的状态 - 使用蓝色背景 (#066cb4)
            convertView.setBackgroundResource(R.drawable.item_selected);
        } else if (member.iConfMemType == Consts.CONST_confMemType_speaker) {
            // 发言中但未被选中的状态 - 使用浅蓝色背景 (#abdaff)
            convertView.setBackgroundResource(R.drawable.item_speaking);
        } else {
            // 未发言且未被选中的状态 - 使用白色背景 (#FFFFFF)
            convertView.setBackgroundResource(R.drawable.item_normal);
        }

        //
        return convertView;
    }

    // 新增方法：更新适配器中的数据并通知GridView刷新
    /*
    public void updateData(List<ConfMember> newData) {
        this.memberList = newData;
        notifyDataSetChanged(); // 通知ListView或GridView数据已更改，需要刷新显示
    }
    */

    // 在MemberAdapter中添加此方法
    public void addMember(ConfMember member) {
        if (this.memberList != null && member != null) {
            this.memberList.add(member);
            notifyDataSetChanged();
        }
    }

    // ViewHolder模式提高性能
    private static class ViewHolder {
        ImageView iv_avatar;
        TextView tv_desc;
        TextView tv_status;
    }
}


