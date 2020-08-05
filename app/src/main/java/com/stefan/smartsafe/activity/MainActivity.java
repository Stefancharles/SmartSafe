package com.stefan.smartsafe.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.stefan.smartsafe.Constant;
import com.stefan.smartsafe.R;
import com.stefan.smartsafe.base.BaseActivity;
import com.stefan.smartsafe.bean.DeviceInfo;
import com.stefan.smartsafe.utils.DataCache;
import com.stefan.smartsafe.utils.LogUtil;
import com.stefan.smartsafe.utils.SPHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.com.newland.nle_sdk.responseEntity.SensorDataPageDTO;
import cn.com.newland.nle_sdk.responseEntity.SensorInfo;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import cn.com.newland.nle_sdk.util.Tools;

public class MainActivity extends BaseActivity {
    private static String TAG = "MainActivity";
    private Context mContext;
    private TextView mBoxStatusText;
    private TextView mBoxDefenceStatusText;
    private TextView mBoxDefenceOpenIconText;
    private TextView mBoxDefenceCloseIconText;

    private ImageView mBoxControlImage;
    private ImageView mBoxDefenceLine;
    private ImageView mBoxDefenceOpenStateImage;
    private ImageView mBoxDefenceCloseStateImage;
    private TextView mPostResultTv;

    private Button mQueryDatasBtn;

    private NetWorkBusiness mNetWorkBusiness;
    private Dialog mBottomDialog;

    private static final int GET_BOX_STATUS = 101;
    private static final int GET_BOX_STATUS_DELAY = 10000;

    private SPHelper spHelper;
    private String mDeviceId;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == GET_BOX_STATUS) {
                querySensorStatus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spHelper = SPHelper.getInstant(getApplicationContext());
        mDeviceId = spHelper.getStringFromSP(getApplicationContext(), Constant.DEVICE_ID);
        mContext = this;
        initView();
        mNetWorkBusiness = new NetWorkBusiness(DataCache.getAccessToken(getApplicationContext()), DataCache.getBaseUrl(getApplicationContext()));
        querySensorStatus();
    }

    private void initView() {
        initHeadView();
        setHeadVisable(true);
        initLeftTitleView("返回");
        setLeftTitleView(true);
        initTitleView(this.getString(R.string.app_title));
        setRithtTitleViewVisable(false);

        mBoxStatusText = findViewById(R.id.box_status_text);
        mBoxDefenceStatusText = findViewById(R.id.box_defence_status_text);
        mBoxDefenceOpenIconText = findViewById(R.id.box_defence_open_state_icon_text);
        mBoxDefenceCloseIconText = findViewById(R.id.box_defence_close_state_icon_text);

        mBoxControlImage = findViewById(R.id.box_control_switch_image);
        mBoxDefenceLine = findViewById(R.id.box_defence_line);
        mBoxDefenceOpenStateImage = findViewById(R.id.box_defence_open_state_image);
        mBoxDefenceCloseStateImage = findViewById(R.id.box_defence_close_state_image);
        mBoxDefenceLine.setTag(true);
        mPostResultTv = findViewById(R.id.postResult);

        mQueryDatasBtn = findViewById(R.id.query_datas_btn);

        mBoxControlImage.setOnClickListener(new OpenBoxListener());
        mBoxDefenceLine.setOnClickListener(new DenfenceListener());
        mQueryDatasBtn.setOnClickListener(new QueryListener());
    }

    class OpenBoxListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            LogUtil.d(TAG, "OpenBoxListener click");
            if ((boolean) mBoxControlImage.getTag()) {
                LogUtil.d(TAG, "box is open, not support close");
                Toast.makeText(getApplicationContext(), R.string.box_control_tip_message, Toast.LENGTH_LONG).show();
                return;
            }
            final Gson gson = new Gson();
            mNetWorkBusiness.control(mDeviceId, DeviceInfo.apiTagBoxControl, DeviceInfo.openBoxValue, new retrofit2.Callback<BaseResponseEntity>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<BaseResponseEntity> call, @NonNull retrofit2.Response<BaseResponseEntity> response) {
                    BaseResponseEntity baseResponseEntity = response.body();
                    LogUtil.d(TAG, "open box, gson.toJson(baseResponseEntity):" + gson.toJson(baseResponseEntity));

                    if (baseResponseEntity != null) {
                        Tools.printJson(mPostResultTv, gson.toJson(baseResponseEntity));
                        try {
                            JSONObject jsonObject = new JSONObject(gson.toJson(baseResponseEntity));
                            int status = (int) jsonObject.get("Status");
                            LogUtil.d(TAG, "Status:" + status);
                            if (0 == status) {
                                displayBoxStatusOpen();
                            } else {
                                LogUtil.d(TAG, "return status value is error, open box fail");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LogUtil.d(TAG, "请求出错 : 请求参数不合法或者服务出错");
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<BaseResponseEntity> call, @NonNull Throwable t) {
                    LogUtil.d(TAG, "请求出错 : \n" + t.getMessage());
                }
            });
        }
    }


    class DenfenceListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final Gson gson = new Gson();
            boolean status = (boolean) mBoxDefenceLine.getTag();
            Object value = 0;
            if (status) {
                value = DeviceInfo.closeDefenceValue;
            } else {
                value = DeviceInfo.openDefenceValue;
            }
            //调用命令控制接口
            /* *
             * param String deviceId：设备ID
             * param String apiTag：API标签
             * param Object data：命令值
             * param Callback<BaseResponseEntity> callback回调对象
             * */
            mNetWorkBusiness.control(mDeviceId, DeviceInfo.apiTagBoxDefence, value, new retrofit2.Callback<BaseResponseEntity>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<BaseResponseEntity> call, @NonNull retrofit2.Response<BaseResponseEntity> response) {
                    BaseResponseEntity baseResponseEntity = response.body();
                    LogUtil.d(TAG, "control, gson.toJson(baseResponseEntity):" + gson.toJson(baseResponseEntity));

                    if (baseResponseEntity != null) {
                        try {
                            //通过返回值获取JSON对象，并通过具体的key值获取想要的结果内容
                            JSONObject jsonObject = new JSONObject(gson.toJson(baseResponseEntity));
                            int status = (int) jsonObject.get("Status");
                            LogUtil.d(TAG, "Status:" + status);
                            displayDefenceStatus((boolean) mBoxDefenceLine.getTag(), status);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Tools.printJson(mPostResultTv, gson.toJson(baseResponseEntity));
                    } else {
                        LogUtil.d(TAG, "请求出错 : 请求参数不合法或者服务出错");
                    }
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<BaseResponseEntity> call, @NonNull Throwable t) {
                    LogUtil.d(TAG, "请求出错 : \n" + t.getMessage());
                }
            });
        }
    }

    private void displayDefenceStatus(boolean value, int status) {
        if (status == 0) {
            if (value) {
                dispalyDefenceClose();
            } else {
                dispalyDefenceOpen();
            }
        }
    }

    private void dispalyDefenceOpen() {
        mBoxDefenceOpenIconText.setTextColor(getResources().getColor(R.color.text_blue));
        mBoxDefenceCloseIconText.setTextColor(getResources().getColor(R.color.text_gray));
        mBoxDefenceOpenStateImage.setBackground(getResources().getDrawable(R.mipmap.icon_protection_blue));
        mBoxDefenceLine.setBackground(getResources().getDrawable(R.mipmap.btn_switch_left));
        mBoxDefenceCloseStateImage.setBackground(getResources().getDrawable(R.mipmap.icon_removal_grey));
        mBoxDefenceStatusText.setText(R.string.box_defence_open);
        mBoxDefenceStatusText.setTextColor(getResources().getColor(R.color.text_blue));
        mBoxDefenceLine.setTag(true);
    }

    private void dispalyDefenceClose() {
        mBoxDefenceOpenIconText.setTextColor(getResources().getColor(R.color.text_gray));
        mBoxDefenceCloseIconText.setTextColor(getResources().getColor(R.color.text_light_red));
        mBoxDefenceOpenStateImage.setBackground(getResources().getDrawable(R.mipmap.icon_protection_grey));
        mBoxDefenceLine.setBackground(getResources().getDrawable(R.mipmap.btn_switch_right));
        mBoxDefenceCloseStateImage.setBackground(getResources().getDrawable(R.mipmap.icon_removal_red));
        mBoxDefenceStatusText.setText(R.string.box_defence_close);
        mBoxDefenceStatusText.setTextColor(getResources().getColor(R.color.text_light_red));
        mBoxDefenceLine.setTag(false);
    }


    private void querySensorStatus() {
        LogUtil.d(TAG, "querySensorStatus");
        final Gson gson = new Gson();

        mNetWorkBusiness.getSensor(mDeviceId, DeviceInfo.apiTagBoxAlarm, new NCallBack<BaseResponseEntity<SensorInfo>>(MainActivity.this) {
            @Override
            protected void onResponse(BaseResponseEntity<SensorInfo> response) {
                SensorInfo resultObj = response.getResultObj();
                LogUtil.d(TAG, "Json apiTagBoxAlarm resultObj: " + gson.toJson(resultObj));
                if (resultObj != null) {
                    String value = resultObj.getValue();
                    LogUtil.d(TAG, "apiTagBoxAlarm value:" + value);
                    Tools.printJson(mPostResultTv, gson.toJson(resultObj));
                    displayBoxStatus(Integer.parseInt(value));
                } else {
                    LogUtil.d(TAG, "apiTagBoxAlarm不存在，请在云平台新建！");
                }
            }
        });

        mNetWorkBusiness.getSensor(mDeviceId, DeviceInfo.apiTagBoxDefenceStatus, new NCallBack<BaseResponseEntity<SensorInfo>>(MainActivity.this) {
            @Override
            protected void onResponse(BaseResponseEntity<SensorInfo> response) {
                SensorInfo resultObj = response.getResultObj();
                LogUtil.d(TAG, "Json apiTagBoxDefenceStatus resultObj: " + gson.toJson(resultObj));
                if (resultObj != null) {
                    String value = resultObj.getValue();
                    LogUtil.d(TAG, "apiTagBoxDefenceStatus value:" + value);
                    Tools.printJson(mPostResultTv, gson.toJson(resultObj));
                    displayBoxStatus(Integer.parseInt(value));
                } else {
                    LogUtil.d(TAG, "apiTagBoxDefenceStatus不存在，请在云平台新建！");
                }
            }
        });

        mHandler.sendEmptyMessageDelayed(GET_BOX_STATUS, GET_BOX_STATUS_DELAY);
    }


    private void displayBoxStatus(int value) {
        if (value == Constant.BOX_STATUS_CLOSE) {
            dismissDialog();
            displayBoxStatusClose();
        } else if (value == Constant.BOX_STATUS_OPEN) {
            dismissDialog();
            displayBoxStatusOpen();
        } else if (value == Constant.BOX_STATUS_ALARM) {
            displayAlarmDialog();
        }
    }

    private void displayBoxStatusOpen() {
        mBoxStatusText.setText(R.string.box_status_open);
        mBoxStatusText.setTextColor(getResources().getColor(R.color.text_blue));
        mBoxControlImage.setBackground(getResources().getDrawable(R.mipmap.icon_safe));
        mBoxControlImage.setTag(true);
    }

    private void displayBoxStatusClose() {
        mBoxStatusText.setText(R.string.box_status_close);
        mBoxStatusText.setTextColor(getResources().getColor(R.color.text_red));
        mBoxControlImage.setBackground(getResources().getDrawable(R.mipmap.icon_safe_lock));
        mBoxControlImage.setTag(false);
    }

    private void displayAlarmDialog() {
        if (null != mBottomDialog) {
            LogUtil.d(TAG, "mBottomDialog has created, return");
            return;
        }
        mBottomDialog = new Dialog(this, R.style.bottom_dialog);
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.alarm_bottom_dialog, null);
        linearLayout.findViewById(R.id.close_alarm_image).setOnClickListener(btnlistener);
        linearLayout.findViewById(R.id.alarm_remove_btn).setOnClickListener(btnlistener);
        mBottomDialog.setContentView(linearLayout);
        Window dialogWindow = mBottomDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = getResources().getDisplayMetrics().widthPixels; // 宽度
        linearLayout.measure(0, 0);
        lp.height = linearLayout.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mBottomDialog.show();
    }

    private View.OnClickListener btnlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId) {
                case R.id.close_alarm_image:
                    dismissDialog();
                    break;
                case R.id.alarm_remove_btn:
                    dismissDialog();
                    break;
            }
        }
    };

    private void dismissDialog() {
        if (null != mBottomDialog) {
            mBottomDialog.dismiss();
        }
    }

    class QueryListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            LogUtil.d(TAG, "click query_datas_btn");
            final Gson gson = new Gson();
            mNetWorkBusiness.getSensorData(mDeviceId, DeviceInfo.apiTagBoxAlarm, "3", "30", getStartTime(), getEndTime(), "DESC", "100", "1", new NCallBack<BaseResponseEntity<SensorDataPageDTO>>(MainActivity.this) {
                @Override
                protected void onResponse(BaseResponseEntity<SensorDataPageDTO> response) {
                    SensorDataPageDTO resultObj = response.getResultObj();
                    if (resultObj != null) {
                        LogUtil.d(TAG, "queryDatas, SensorDataPageDTO resultObj:" + gson.toJson(resultObj));
                        Tools.printJson(mPostResultTv, gson.toJson(resultObj));
                        Intent intent = new Intent(mContext, QueryDatasActivity.class);
                        intent.putExtra("jsonData", gson.toJson(resultObj));
                        startActivity(intent);
                    } else {
                        mPostResultTv.setText("请求出错 : 请求参数不合法或者服务出错");
                    }
                }

            });
        }
    }

    private String getStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 30);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = df.format(calendar.getTime());
        LogUtil.d(TAG, "getStartTime: " + startTime);
        return startTime;
    }

    private String getEndTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endTime = df.format(new Date());
        LogUtil.d(TAG, "getEndTime: " + endTime);
        return endTime;
    }
}