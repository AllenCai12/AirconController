package com.EMA.AirconControl.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.EMA.AirconControl.AlljoynClient.AlljoynClientImpl;
import com.EMA.AirconControl.Onboarding.Keys;
import com.EMA.AirconControl.Onboarding.OnboardingApplication;
import com.EMA.AirconControl.XMPPClient.AirconControlByXmppInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


/*
 * Created by kylin on 16-9-21.
 */
public class AirconConnectTransfer {

    private final static String TAG = "AirconConnectTranfer";
    private final static int UPDATE_LIST = 0;
    private Context m_context;
    private AirconConnectService.AirconConnectBinder m_airconConnectBinder;
    private AirconControlByXmppInterface m_xmppClient;
    private AlljoynClientImpl m_alljoynClient;
    private OnboardingApplication m_application;

    private HashMap<String, String> m_deviceMap;
    private Handler m_handler;

    private BroadcastReceiver m_receiver;

    private List<String> m_deviceNameList;//updated by xmpp or alljoyn
    private String m_deviceStatus; //updated by xmpp or alljoyn

    private String m_deviceID;

    /****
     *property should be set
     * ****/
    private Boolean m_isOpen;
    private double m_temperature;
    private String m_mode;
    private int m_windSpeed;



    public AirconConnectTransfer(Context context) {

        Log.d(TAG, "==========AirconConnectTransfer========");
        m_application=(OnboardingApplication)context.getApplicationContext();
        m_context = context;
        m_xmppClient = new AirconControlByXmppInterface(m_context);
        if(m_alljoynClient == null) {
            m_alljoynClient = new AlljoynClientImpl(m_application);
        }

        if(m_deviceNameList == null) {
            m_deviceNameList = new ArrayList<>();
            m_deviceMap = new HashMap<>();
        }

    }
    /*
    *get the device id from m_deviceMap by deviceName
    * */
    public void setDeviceId(String deviceName)
    {
       m_deviceID = m_deviceMap.get(deviceName);
        Log.d(TAG, "=======device id =======" + m_deviceID);
    }

    public  void setContext(Context context)
    {
        m_context = context;
    }

    public void  alljoynUpdateList()
    {
        if(!m_alljoynClient.isConnect())
        {
            Log.d(TAG, "=======start alljoynConnnect======");
            m_alljoynClient.allJoynConnect();
        }
        else
        {
            try {
                for(String tmp: m_alljoynClient.getAllAirconIDs()) {

                    m_deviceNameList.add(m_alljoynClient.getAirconName(tmp, "en"));
                    m_deviceMap.put(m_alljoynClient.getDeviceName(), tmp);
                }
            }catch (Exception e)
            {
               e.printStackTrace();
            }
        }

            m_deviceNameList = new ArrayList<>();
        m_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(Keys.Actions.ACTION_DEVICE_FOUND.equals(intent.getAction())){
                    Log.d(TAG, "=======before onReceive==========");
                    String appId;
                    appId = intent.getExtras().getString(Keys.Extras.EXTRA_DEVICE_ID);

                    try {
                        Log.d(TAG, "======appid======"+appId);
                        String deviceName;
                        m_alljoynClient.setDevice(appId);

                        if(m_alljoynClient.initProcyBusObject()) {

                            for (String tmp : m_alljoynClient.getAllAirconIDs()) {
                                deviceName = m_alljoynClient.getAirconName(tmp, "en");
                                m_deviceNameList.add(deviceName);
                                m_deviceMap.put(deviceName, tmp);
                            }

                            for (String tmp : m_deviceNameList) {
                                Log.d(TAG, "=============deviceNameList===" + tmp);
                            }

                            Intent broadIntent = new Intent(ViewBroadcast.UPDATE_LIST);
                            m_context.sendBroadcast(broadIntent);
                        }
                    } catch (Exception e)
                    {
                        Log.d("ERROR", "=====get device error======");
                    }

                }

                else if(Keys.Actions.ACTION_DEVICE_LOST.equals(intent.getAction())){
                    Log.d(TAG, "===========Device Lost =============");
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Keys.Actions.ACTION_DEVICE_FOUND);
        filter.addAction(Keys.Actions.ACTION_DEVICE_LOST);
        m_context.registerReceiver(m_receiver, filter);

    }

   /******
    *update the device status by xmpp or alljoyn
    * ******/
    public void updateDeviceStatus()// 通过当前网络类型，选择更新方式
    {
        if(m_alljoynClient.isConnect()) {
            alljoynUpdateData();
        }
        else {
            xmppUpdateData();
        }
    }
    public void updateDeviceList()
    {
        xmppUpdateList();
        alljoynUpdateList();
    }

    public void alljoynUpdateData()
    {
        if(!m_alljoynClient.isConnect())
        {
            m_alljoynClient.allJoynConnect();
        }
        else
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String deviceStatus = m_alljoynClient.getAirconState(m_deviceID);

                    Log.d(TAG, "=========deviceStatus======"  + deviceStatus);
                    Intent broadIntent = new Intent(ViewBroadcast.UPDATE_AIRCON_STATUS_DATA);
                    Bundle extras = new Bundle();
                    extras.putString(ViewBroadcast.Extras.DEVICE_STATE_DATA, deviceStatus);
                    broadIntent.putExtras(extras);
                    m_context.sendBroadcast(broadIntent);
                }
            }).start();
        }

        m_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(Keys.Actions.ACTION_DEVICE_FOUND.equals(intent.getAction())){
                    Log.d(TAG, "=======before onReceivve==========");
                    String appId;
                    appId = intent.getExtras().getString(Keys.Extras.EXTRA_DEVICE_ID);

                    try {
                        m_alljoynClient.setDevice(appId);
                        m_alljoynClient.initProcyBusObject();
                        String deviceStatus = m_alljoynClient.getAirconState(m_deviceID);

                        Intent broadIntent = new Intent(ViewBroadcast.UPDATE_AIRCON_STATUS_DATA);
                        Bundle extras = new Bundle();
                        extras.putString(ViewBroadcast.Extras.DEVICE_STATE_DATA, deviceStatus);
                        broadIntent.putExtras(extras);
                        m_context.sendBroadcast(broadIntent);
                    } catch (Exception e)
                    {
                        Log.d("ERROR", "=====get device error======");
                    }

                }

                else if(Keys.Actions.ACTION_DEVICE_LOST.equals(intent.getAction())){

                    String busName = intent.getExtras().getString(Keys.Extras.EXTRA_BUS_NAME);
                }
                else if(Keys.Actions.ACTION_CONNECTED_TO_NETWORK.equals(intent.getAction())){

                    String ssid = intent.getStringExtra(Keys.Extras.EXTRA_NETWORK_SSID);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Keys.Actions.ACTION_DEVICE_FOUND);
        filter.addAction(Keys.Actions.ACTION_DEVICE_LOST);
        filter.addAction(Keys.Actions.ACTION_CONNECTED_TO_NETWORK);
        m_application.registerReceiver(m_receiver, filter);


    }

    public void xmppUpdateList()
    {
//        m_deviceStatus = m_alljoynClient.getAirconState();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    for(String tmp : m_xmppClient.getAllAirconIDs())
                    {
                        String   deviceName = m_xmppClient.getAirconName(tmp, "en");
                        Log.d(TAG, "======deviceName ====="+deviceName);
                        m_deviceNameList.add(deviceName);
                        m_deviceMap.put(deviceName, tmp);
                        Log.d(TAG, "==========devicetest id====="+m_deviceMap.get(deviceName));
                    }

                    Intent broadIntent = new Intent(ViewBroadcast.UPDATE_LIST);
                    m_context.sendBroadcast(broadIntent);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void xmppUpdateData()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String   deviceStatus = m_xmppClient.getAirconState(m_deviceID);
                    Log.d(TAG, "======xmpp deviceStatus ====="+deviceStatus);
                    Intent broadIntent = new Intent(ViewBroadcast.UPDATE_AIRCON_STATUS_DATA);
                    Bundle extras = new Bundle();
                    extras.putString(ViewBroadcast.Extras.DEVICE_STATE_DATA, deviceStatus);
                    broadIntent.putExtras(extras);
                    m_context.sendBroadcast(broadIntent);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void listtenToAlljoynList()
    {

        //************** Class receiver ******************
    }

    public List<String> getDeviceNameList()
    {
        Log.d(TAG, "========name list size======"+m_deviceNameList.size());
        return m_deviceNameList;
    }



    public void removeDuplicate(ArrayList<String> duplicateList)
    {
        HashSet h = new HashSet<>(duplicateList);
        duplicateList.clear();
        duplicateList.addAll(h);
    }


    private boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return wifiNetworkInfo.isConnected() ;
    }


    public double getAirconTemperature()
    {
        double   tmp ;
        tmp = m_alljoynClient.getAirconStateField(m_deviceID, "Temperature");

        Log.d(TAG, "====get====temperature======"+tmp);

        return tmp;
    }

    public int setAirconTemperature(int temperature)
    {
        int status = -1;
        m_temperature = (double) temperature;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(m_alljoynClient.isConnect()) {
                    m_alljoynClient.setAirconStateField(m_deviceID, "Temperature", m_temperature);
                }
                else
                    m_xmppClient.setAirconStateField(m_deviceID, "Temperature", m_temperature);
            }
        }).start();

        return  status;
    }

    public void setAirconMode(String mode)
    {
        m_mode = mode;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(m_alljoynClient.isConnect()) {
                    m_alljoynClient.setAirconStateField(m_deviceID, "Mode", m_mode);
                }
                else {
                    m_xmppClient.setAirconStateField(m_deviceID, "Mode", m_mode);
                }
            }
        }).start();
    }

    public void setAirconWindSpeed(int windSpeed)
    {
        m_windSpeed = windSpeed;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(m_alljoynClient.isConnect()) {
                    m_alljoynClient.setAirconStateField(m_deviceID, "FanGear", m_windSpeed);
                }
                else {
                    m_xmppClient.setAirconStateField(m_deviceID, "FanGear", m_windSpeed);
                }
            }
        }).start();
    }

    public void setAirconOnOff(Boolean isOpen)
    {
        m_isOpen = isOpen;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(m_alljoynClient.isConnect()) {
                    m_alljoynClient.setAirconStateField(m_deviceID, "OnOff", m_isOpen);
                }
                else {
                    m_xmppClient.setAirconStateField(m_deviceID, "OnOff", m_isOpen);
                }
            }
        }).start();
    }
    public void destroiy()
    {

        Log.d(TAG, "======on dis connect==========");
        m_alljoynClient.doDisconnect();
        if(m_receiver != null) {
            m_context.unregisterReceiver(m_receiver);
        }

    }

}
