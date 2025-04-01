package com.aaron.hookpractice.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.aaron.hookpractice.R;

/**
 * author: dubojun
 * date: 2025/1/20
 * description:
 **/
public class MyBottomDialogFragment extends BottomSheetDialogFragment {
    public MyBottomDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_bottom_sheet_dialog, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.dimAmount = 0f;

        getDialog().getWindow().setAttributes(layoutParams);
    }
}
