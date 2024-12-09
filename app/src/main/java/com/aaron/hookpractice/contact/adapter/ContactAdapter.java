package com.aaron.hookpractice.contact.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aaron.hookpractice.R;
import com.aaron.hookpractice.contact.bean.ListItemBean;
import com.aaron.hookpractice.utils.ListUtils;

import java.util.List;

/**
 * author: dubojun
 * date: 2024/9/29
 * description:
 **/
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {
    private static final String TAG = "ContactAdapter";
    public static final int VIEW_TYPE_0 = 0;
    public static final int VIEW_TYPE_1 = 1;
    public static final int VIEW_TYPE_2 = 2;

    private Context mContext;
    private List<ListItemBean> mData;

    public ContactAdapter(Context context, List<ListItemBean> mData) {
        this.mContext = context;
        this.mData = mData;
    }

    @Override
    public int getItemViewType(int position) {
        if (ListUtils.isEmpty(mData)) {
            return VIEW_TYPE_0;
        }

        ListItemBean listItemBean = mData.get(position);
        if (listItemBean == null) {
            return VIEW_TYPE_0;
        }
        int phone = Integer.parseInt(listItemBean.phone);
        int type = phone % 3 ;
        Log.d(TAG, "getItemViewType: type = " + type);
        return type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case VIEW_TYPE_0:
                return new MyViewHolder(inflater.inflate(R.layout.item_contact, viewGroup,false));
            case VIEW_TYPE_1:
                return new MyViewHolder(inflater.inflate(R.layout.item_contact_v2, viewGroup, false));
            case VIEW_TYPE_2:
                return new MyViewHolder(inflater.inflate(R.layout.item_contact_v3, viewGroup, false));
        }

        return new MyViewHolder(inflater.inflate(R.layout.item_contact, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        if (ListUtils.isEmpty(mData)) {
            return;
        }

        ListItemBean itemBean = mData.get(position);
        if (itemBean == null) {
            return;
        }

        myViewHolder.nameView.setText(itemBean.name);
        myViewHolder.phoneView.setText(itemBean.phone);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView phoneView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.tv_name);
            phoneView = itemView.findViewById(R.id.tv_phone);
        }
    }
}
