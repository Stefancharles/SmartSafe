package com.stefan.smartsafe.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * @Author: Stefan Charles
 * @Date: 2020-07-31
 * @Website: www.stefancharles.cn
 * @E-mail: stefancharles@qq.com
 * @Copyright: Copyright (c) 2020 Security Plus.All rights reserved.
 **/
public class TableTextView extends androidx.appcompat.widget.AppCompatTextView{
    Paint paint = new Paint();

    public TableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画TextView的4个边
        canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint);
        canvas.drawLine(0, 0, 0, this.getHeight() - 1, paint);
        canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight() - 1, paint);
        canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, paint);
    }
}
