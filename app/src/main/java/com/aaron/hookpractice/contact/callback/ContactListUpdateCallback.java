package com.aaron.hookpractice.contact.callback;

import android.support.annotation.Nullable;
import android.support.v7.util.ListUpdateCallback;
import android.util.Log;

import com.aaron.hookpractice.contact.adapter.ContactAdapter;

/**
 * author: dubojun
 * date: 2024/9/29
 * description:
 **/
public class ContactListUpdateCallback implements ListUpdateCallback {
    private static final String TAG = "ContactListUpdateCb";
    private ContactAdapter mAdapter;

    public ContactListUpdateCallback(ContactAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    public void onInserted(int position, int count) {
        Log.d(TAG, "onInserted: " + position);
        if (mAdapter != null) {
            mAdapter.notifyItemRangeInserted(position, count);
        }
    }

    @Override
    public void onRemoved(int position, int count) {
        Log.d(TAG, "onRemoved: " + position);
        if (mAdapter != null) {
            mAdapter.notifyItemRangeRemoved(position, count);
        }
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoved: " + fromPosition);
        if (mAdapter != null) {
            mAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onChanged(int position, int count, @Nullable Object payload) {
        Log.d(TAG, "onChanged: " + position);
        if (mAdapter != null) {
            mAdapter.notifyItemRangeChanged(position, count, payload);
        }
    }
}
