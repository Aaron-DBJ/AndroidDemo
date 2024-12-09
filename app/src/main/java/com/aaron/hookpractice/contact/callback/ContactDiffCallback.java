package com.aaron.hookpractice.contact.callback;

import android.support.v7.util.DiffUtil;

import com.aaron.hookpractice.contact.bean.ListItemBean;
import com.aaron.hookpractice.utils.ListUtils;

import java.util.List;

/**
 * author: dubojun
 * date: 2024/9/29
 * description:
 **/
public class ContactDiffCallback extends DiffUtil.Callback {
    private List<ListItemBean> oldData;
    private List<ListItemBean> newData;

    public ContactDiffCallback(List<ListItemBean> oldData, List<ListItemBean> newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    @Override
    public int getOldListSize() {
        return ListUtils.getCount(oldData);
    }

    @Override
    public int getNewListSize() {
        return ListUtils.getCount(newData);
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ListItemBean oldItem = ListUtils.getElement(oldData, oldItemPosition);
        ListItemBean newItem = ListUtils.getElement(newData, newItemPosition);

        return oldItem !=null && newItem != null && oldItem.id == newItem.id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }
}
