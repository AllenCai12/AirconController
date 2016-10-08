package com.EMA.AirconControl.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.EMA.AirconControl.XMPPClient.AirconControlByXmppInterface;

public class AirconConnectService extends Service {

    private static final String TAG = "AirconConnectServcie";

    private AirconControlByXmppInterface m_airconXmppClient;

    private AirconConnectBinder airconConnectBinder = new AirconConnectBinder();
    private String[] m_airconIds;


    class AirconConnectBinder extends Binder{

        public AirconControlByXmppInterface getXmppClient()
        {
            return m_airconXmppClient;
        }
        public String[] getAirconIDs()
        {
            return m_airconIds;
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "=============onCreate excuted===================");
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                startConnectXmpp();
            }
        }).start();*/
    }

    private void startConnectXmpp()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                m_airconXmppClient = new AirconControlByXmppInterface(AirconConnectService.this.getApplicationContext());
                m_airconIds = m_airconXmppClient.getAllAirconIDs();
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "==========onStartCommand excuted=======");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {

        return airconConnectBinder;

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
