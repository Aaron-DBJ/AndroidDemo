package com.aaron.hookpractice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.aaron.dibinder.EasyBinder;
import com.aaron.diview.DIView;

/**
 * @author dbj
 * @date 2/21/24
 * @description
 */
public class ThirdActivity extends BaseActivity {
    @DIView(R.id.btn_jump)
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        EasyBinder.bind(this);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(ThirdActivity.this, SecondActivity.class);
            startActivity(intent);
        });
    }
}
