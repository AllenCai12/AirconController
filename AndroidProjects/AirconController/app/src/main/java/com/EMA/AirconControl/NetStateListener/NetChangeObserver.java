package com.EMA.AirconControl.NetStateListener;

/*
 * Created by kylin on 16-9-28.
 */
public interface NetChangeObserver {
    /**
     * 网络状态连接时调用
     */
    public void OnConnect(Boolean isWifiConnected);

    /**
     * 网络状态断开时调用
     */
    public void OnDisConnect();
}