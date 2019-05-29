package com.example.commonlibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.example.commonlibrary.rxbus.RxBusManager;
import com.example.commonlibrary.rxbus.event.NetStatusEvent;
import com.example.commonlibrary.utils.CommonLogger;

/**
 * 项目名称:    NewFastFrame
 * 创建人:      陈锦军
 * 创建时间:    2018/12/10     14:42
 */
public class ScreenNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        CommonLogger.e("接收到的action" + action);
        if (action == null) {
            return;
        }
        switch (action) {
            case "android.net.conn.CONNECTIVITY_CHANGE":
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = null;
                if (connectivityManager != null) {
                    networkInfo = connectivityManager.getActiveNetworkInfo();
                }
                if (networkInfo != null) {
                    RxBusManager.getInstance().post(new NetStatusEvent(networkInfo.isConnected(), networkInfo.getType()));
                } else {
                    RxBusManager.getInstance().post(new NetStatusEvent(false, 0));
                }
                break;
        }
    }
}
