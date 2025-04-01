package com.aaron.hookpractice.contact.interfaces;

/**
 * author: dubojun
 * date: 2025/3/31
 * description:
 **/
public interface IDragSwipe {
    void onItemSwapped(int fromPosition, int toPosition);

    void onItemDeleted(int position);

    void onItemDone(int position);
}
