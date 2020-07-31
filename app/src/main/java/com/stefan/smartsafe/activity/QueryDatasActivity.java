package com.stefan.smartsafe.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.stefan.smartsafe.Constant;
import com.stefan.smartsafe.R;
import com.stefan.smartsafe.base.BaseActivity;
import com.stefan.smartsafe.utils.LogUtil;
import com.stefan.smartsafe.utils.SPHelper;
import com.stefan.smartsafe.view.TableTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QueryDatasActivity extends BaseActivity {
    private static String TAG = "MainActivity";
    private Context mContext;
    private String mJsonData;

    private LinearLayout mainLinerLayout;
    private RelativeLayout relativeLayout;
    private String[] name = {"设备ID", "时间", "状态"};
    private SPHelper spHelper;
    private String mDeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_datas);
        Intent intent = getIntent();
        mJsonData = intent.getStringExtra("jsonData");
        LogUtil.d(TAG, "jsonData:" + mJsonData);
        spHelper = SPHelper.getInstant(getApplicationContext());
        mDeviceId = spHelper.getStringFromSP(getApplicationContext(), Constant.DEVICE_ID);
        mContext = this;
        initView();
        initData();
    }

    private void initView() {
        initHeadView();
        setHeadVisable(true);
        initLeftTitleView("返回");
        setLeftTitleView(true);
        initTitleView("查询传感数据");
        setRithtTitleViewVisable(false);

        mainLinerLayout = this.findViewById(R.id.MyTable);
    }

    //绑定数据
    @SuppressLint("CutPasteId")
    private void initData() {
        //初始化标题
        relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
        @SuppressLint("CutPasteId") TableTextView title = relativeLayout.findViewById(R.id.list_1_1);
        title.setText(name[0]);
        title.setTextColor(Color.BLUE);
        title = relativeLayout.findViewById(R.id.list_1_2);
        title.setText(name[1]);
        title.setTextColor(Color.BLUE);
        title = relativeLayout.findViewById(R.id.list_1_3);
        title.setText(name[2]);
        mainLinerLayout.addView(relativeLayout);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(mJsonData);
            JSONObject resultObj = (JSONObject) jsonObject.get("ResultObj");
            JSONArray dataPoints = resultObj.getJSONArray("DataPoints");
            LogUtil.d(TAG, "dataPoints size:" + dataPoints.length());
            LogUtil.d(TAG, "dataPoints:" + dataPoints);

            JSONArray pointDTO = dataPoints.getJSONObject(0).getJSONArray("PointDTO");
            LogUtil.d(TAG, "pointDTO size:" + pointDTO.length());
            LogUtil.d(TAG, "pointDTO:" + pointDTO);
            for (int i = 0; i < pointDTO.length(); i++) {
                JSONObject subObject = pointDTO.getJSONObject(i);
                double value = (double) subObject.get("Value");
                String recordTime = (String) subObject.get("RecordTime");
                relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.table, null);
                TableTextView txt = relativeLayout.findViewById(R.id.list_1_1);
                txt.setText(mDeviceId);

                txt = relativeLayout.findViewById(R.id.list_1_2);
                txt.setText(recordTime);

                txt = relativeLayout.findViewById(R.id.list_1_3);
                txt.setText(String.valueOf(value));
                mainLinerLayout.addView(relativeLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}