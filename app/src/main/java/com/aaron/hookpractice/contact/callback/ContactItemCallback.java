package com.aaron.hookpractice.contact.callback;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.aaron.diview.DIView;
import com.aaron.hookpractice.contact.bean.ListItemBean;

/**
 * author: dubojun
 * date: 2024/9/30
 * description:
 **/
public class ContactItemCallback extends DiffUtil.ItemCallback<ListItemBean> {

    @Override
    public boolean areItemsTheSame(@NonNull ListItemBean oldItem, @NonNull ListItemBean newItem) {
        return oldItem.id == newItem.id;
    }

    @Override
    public boolean areContentsTheSame(@NonNull ListItemBean oldItem, @NonNull ListItemBean newItem) {
        return false;
    }
}
