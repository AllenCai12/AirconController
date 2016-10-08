package com.EMA.AirconControl.NetStateListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by kylin on 16-9-28.
 */
public class NetStateReceiver extends BroadcastReceiver {

    private final static String TAG = "NetStateReceiver";

    /** 储存所有的网络状态观察者集合   */
    private static ArrayList<NetChangeObserver> netChangeObserverArrayList = new ArrayList<NetChangeObserver>();
    private static boolean networkAvailable = true;

    State wifiState = null;
    State mobileState = null;
    public static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private boolean isWifiConnected = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (ACTION.equals(intent.getAction())) {
            //获取手机的连接服务管理器，这里是连接管理器类
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

            if( (wifiState != null && State.CONNECTED == wifiState) || (mobileState !=null && State.CONNECTED == mobileState) )
            {
                Log.d(TAG, "======手机有网络===========");
                if(wifiState != null && State.CONNECTED != wifiState)
                {
                    isWifiConnected = false;
                    Log.d(TAG, "=======wifi disConnect=====");
                }
                networkAvailable = true;
            }
            else
            {
               Log.d(TAG, "======手机无网络===========");
                isWifiConnected = false;
                networkAvailable =false;
            }
            notifyObserver();

        }
    }

    public static ArrayList<NetChangeObserver> getNetChangeObserverArrayList() {
        return netChangeObserverArrayList;
    }

    /**
     * 添加/注册网络连接状态观察者
     * @param observer
     */
    public static void registerNetStateObserver(NetChangeObserver observer){
        if(netChangeObserverArrayList == null){
            netChangeObserverArrayList = new ArrayList<NetChangeObserver>();
        }
        netChangeObserverArrayList.add(observer);
    }


    /**
     * 删除/注销网络连接状态观察者
     * @param observer
     */
    public static void unRegisterNetStateObserver(NetChangeObserver observer){
        if(netChangeObserverArrayList != null){
            netChangeObserverArrayList.remove(observer);
        }
    }


    /**
     * 向所有的观察者发送通知：网络状态发生改变咯...
     */
    private void notifyObserver(){
        if(netChangeObserverArrayList !=null && netChangeObserverArrayList.size() >0){
            for(NetChangeObserver observer : netChangeObserverArrayList){
                if(observer != null){
                    if(networkAvailable){
                        observer.OnConnect(isWifiConnected);
                    }else{
                        observer.OnDisConnect();
                    }
                }
            }
        }
    }
}
