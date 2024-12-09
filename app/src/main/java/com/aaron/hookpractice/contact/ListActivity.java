package com.aaron.hookpractice.contact;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.AdapterListUpdateCallback;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.aaron.dibinder.EasyBinder;
import com.aaron.diview.DIView;
import com.aaron.hookpractice.R;
import com.aaron.hookpractice.activity.BaseActivity;
import com.aaron.hookpractice.contact.adapter.ContactAdapter;
import com.aaron.hookpractice.contact.bean.ListItemBean;
import com.aaron.hookpractice.contact.callback.ContactDiffCallback;
import com.aaron.hookpractice.contact.callback.ContactItemCallback;
import com.aaron.hookpractice.contact.callback.ContactListUpdateCallback;
import com.aaron.hookpractice.utils.ListUtils;
import com.aaron.hookpractice.utils.MToast;

import java.util.ArrayList;
import java.util.List;

/**
 * author: dubojun
 * date: 2024/9/29
 * description:
 **/
public class ListActivity extends BaseActivity {
    private final List<ListItemBean> data = new ArrayList<ListItemBean>() {
        {
            add(new ListItemBean(1, "Tom", "2344535"));
            add(new ListItemBean(2, "Mike", "5645056"));
            add(new ListItemBean(3, "Kobe", "0984532"));
            add(new ListItemBean(4, "Tina", "2346239"));
            add(new ListItemBean(5, "Sam", "58349235"));
            add(new ListItemBean(6, "Welly", "4596832"));
            add(new ListItemBean(7, "Ada", "9454234"));
            add(new ListItemBean(8, "Jake", "8534223"));
            add(new ListItemBean(9, "Bob", "3259854"));
            add(new ListItemBean(10, "Obama", "2328712"));
            add(new ListItemBean(11, "Cindy", "9485741"));
            add(new ListItemBean(12, "Dove", "7424134"));
            add(new ListItemBean(13, "Ellen", "2145843"));
            add(new ListItemBean(14, "Freeman", "9114751"));
            add(new ListItemBean(15, "Grace", "3534912"));
            add(new ListItemBean(16, "Hoff", "0134151"));
            add(new ListItemBean(17, "Kevin", "3582359"));
        }
    };

    @DIView(R.id.recyclerView)
    RecyclerView recyclerView;

    @DIView(R.id.cl_bottom_container)
    ConstraintLayout mBottomContainer;

    @DIView(R.id.iv_icon1)
    ImageView icon1;

    @DIView(R.id.iv_icon2)
    ImageView icon2;

    @DIView(R.id.iv_icon3)
    ImageView icon3;

    private ContactAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        EasyBinder.bind(this);
        mAdapter = new ContactAdapter(this, data);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(mAdapter);

        icon1.setOnClickListener(v -> {
            List<ListItemBean> diff = new ArrayList<>();
            ListItemBean bean1 = new ListItemBean(45, "张三", "3856584");
            ListItemBean bean2 = new ListItemBean(53, "李四", "4387361");
            diff.add(bean1);
            diff.add(bean2);
            addNewData(2, diff);
//            addNewDataAsync(3, diff);
            MToast.toast(ListActivity.this, "位置 3 添加联系人 张三");
        });

        icon2.setOnClickListener(v -> {
//            removeData(4);
            removeDataAsync(5);
        });

        icon3.setOnClickListener(v -> {
            updateData(8);
        });

    }

    private void addNewData(int index, List<ListItemBean> diffList) {
        List<ListItemBean> newData = getNewData(index, diffList);
        ContactDiffCallback diffCallback = new ContactDiffCallback(data, newData);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        diffResult.dispatchUpdatesTo(new ContactListUpdateCallback(mAdapter));
        data.clear();
        data.addAll(newData);
    }

    private void addNewDataAsync(int index, List<ListItemBean> diffList) {
        List<ListItemBean> newData = getNewData(index, diffList);
        AsyncListDiffer<ListItemBean> asyncListDiffer = new AsyncListDiffer<>(mAdapter, new ContactItemCallback());
        asyncListDiffer.submitList(newData);
        data.clear();
        data.addAll(newData);
    }

    private List<ListItemBean> getNewData(int index, List<ListItemBean> diffList) {
        List<ListItemBean> results = new ArrayList<>(data);
        results.addAll(index, diffList);
        return results;
    }


    private void removeData(int index) {
        List<ListItemBean> list = new ArrayList<>(data);
        list.remove(index);
        ContactDiffCallback diffCallback = new ContactDiffCallback(data, list);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback);
        result.dispatchUpdatesTo(new ContactListUpdateCallback(mAdapter));
        data.clear();
        data.addAll(list);
    }

    private void removeDataAsync(int index) {
        AsyncListDiffer<ListItemBean> asyncListDiffer = new AsyncListDiffer<>(mAdapter, new ContactItemCallback());
        List<ListItemBean> results = new ArrayList<>(data);
        results.remove(index);
        asyncListDiffer.submitList(results);
        data.clear();
        data.addAll(results);
    }

    private void updateData(int index) {
        List<ListItemBean> old = new ArrayList<>(data);
        ListItemBean bob = ListUtils.getElement(data, index);
        bob.phone = "9999999";
        ContactDiffCallback diffCallback = new ContactDiffCallback(old, data);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffCallback);
        result.dispatchUpdatesTo(new ContactListUpdateCallback(mAdapter));
    }
}
