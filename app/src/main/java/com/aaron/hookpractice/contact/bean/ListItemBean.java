package com.aaron.hookpractice.contact.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: dubojun
 * date: 2024/9/29
 * description:
 **/
public class ListItemBean implements Parcelable {
    public long id;

    public String name;

    public String phone;

    public ListItemBean(long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    protected ListItemBean(Parcel in) {
        id = in.readLong();
        name = in.readString();
        phone = in.readString();
    }

    public static final Creator<ListItemBean> CREATOR = new Creator<ListItemBean>() {
        @Override
        public ListItemBean createFromParcel(Parcel in) {
            return new ListItemBean(in);
        }

        @Override
        public ListItemBean[] newArray(int size) {
            return new ListItemBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(phone);
    }
}
