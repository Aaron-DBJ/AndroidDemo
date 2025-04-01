package com.aaron.hookpractice.contact.callback;

import android.app.PendingIntent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aaron.hookpractice.MyApplication;
import com.aaron.hookpractice.R;
import com.aaron.hookpractice.contact.adapter.ContactAdapter;
import com.aaron.hookpractice.contact.interfaces.IDragSwipe;
import com.aaron.hookpractice.utils.ScreenUtils;

/**
 * author: dubojun
 * date: 2025/3/31
 * description:
 **/
public class DragSwipeCallback extends ItemTouchHelper.Callback {
    private static final String TAG = "DragSwipeCallback";
    private IDragSwipe adapter;

    public DragSwipeCallback(IDragSwipe adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder targetViewHolder) {
        Log.d(TAG, "onMove: ");
        adapter.onItemSwapped(viewHolder.getAdapterPosition(), targetViewHolder.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d(TAG, "onSwiped: ");
        if (direction == ItemTouchHelper.END) {
            adapter.onItemDone(viewHolder.getAdapterPosition());
        } else if (direction == ItemTouchHelper.START) {
            adapter.onItemDeleted(viewHolder.getAdapterPosition());
        }
    }

    /**
     * 拖拽、滑动时如何绘制列表
     * actionState只会为ACTION_STATE_DRAG或者ACTION_STATE_SWIPE
     */
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Log.d(TAG, "onChildDraw: viewHolder = " + viewHolder+ "; dX = " + dX);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            int deleteContainerWidth = getSlideLimitation(viewHolder);
            float halfParentWidth = (float) recyclerView.getWidth() / 2;
            if (Math.abs(dX) < deleteContainerWidth) {
                viewHolder.itemView.scrollTo((int) -dX, 0);
            }  else if (Math.abs(dX) <= halfParentWidth) {
                int maxIconSize =  ScreenUtils.dip2px(recyclerView.getContext(), 50);
                double remainDistance = halfParentWidth - deleteContainerWidth;
                double factor = maxIconSize / remainDistance;
                double diff = (Math.abs(dX) - deleteContainerWidth) * factor;
                if (diff >=  maxIconSize) {
                    diff = maxIconSize;
                }
                viewHolder.itemView.findViewById(R.id.tv_delete).setVisibility(View.GONE);
                viewHolder.itemView.findViewById(R.id.iv_delete).setVisibility(View.VISIBLE);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewHolder.itemView.findViewById(R.id.iv_delete).getLayoutParams();
                layoutParams.width = (int) (ScreenUtils.dip2px(recyclerView.getContext(), 40) + diff);
                layoutParams.height = (int) (ScreenUtils.dip2px(recyclerView.getContext(), 40) + diff);
                viewHolder.itemView.findViewById(R.id.iv_delete).setLayoutParams(layoutParams);
            } else {

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder == null) {
            return;
        }

        Log.d(TAG, "onSelectedChanged: viewHolder = " + viewHolder);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            // 拖拽时，如果是isCurrentlyActive，则设置translationZ，否则复位
            viewHolder.itemView.setTranslationZ(12);
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            viewHolder.itemView.findViewById(R.id.ll_delete_container).setBackgroundColor(Color.RED);
            viewHolder.itemView.findViewById(R.id.ll_delete_container).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.tv_delete).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.iv_delete).setVisibility(View.GONE);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        Log.d(TAG, "clearView: viewHolder = " + viewHolder);
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof ContactAdapter.MyViewHolder) {
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.itemView.setTranslationZ(0);
            viewHolder.itemView.findViewById(R.id.ll_delete_container).setBackgroundColor(Color.TRANSPARENT);
            viewHolder.itemView.findViewById(R.id.ll_delete_container).setVisibility(View.GONE);
            viewHolder.itemView.findViewById(R.id.tv_delete).setVisibility(View.VISIBLE);
            viewHolder.itemView.findViewById(R.id.iv_delete).setVisibility(View.GONE);
        }
    }

    /**
     * 获取删除方块的宽度
     */
    public int getSlideLimitation(RecyclerView.ViewHolder viewHolder) {
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        View target = viewGroup.findViewById(R.id.ll_delete_container);
        if (target == null || target.getLayoutParams() == null) {
            return 0;
        }
        return target.getLayoutParams().width;
    }
}
