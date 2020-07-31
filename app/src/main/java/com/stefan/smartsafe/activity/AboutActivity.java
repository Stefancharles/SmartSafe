package com.stefan.smartsafe.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.stefan.smartsafe.R;
import com.stefan.smartsafe.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }

    private void initView() {
        initHeadView();
        setHeadVisable(true);
        initLeftTitleView("关于");
        setLeftTitleView(true);
        setTitleViewVisable(false);
        setRithtTitleViewVisable(false);
    }
}