package com.devilwwj.loginandregister.Onboarding.ConfigWifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.widget.ProgressBar;

import com.devilwwj.loginandregister.Onboarding.IskWifiManager;
import com.devilwwj.loginandregister.Onboarding.Jids;
import com.devilwwj.loginandregister.Onboarding.Keys;
import com.devilwwj.loginandregister.Onboarding.OnboardingApplication;
import com.devilwwj.loginandregister.Onboarding.ScanDeviceActivity;
import com.devilwwj.loginandregister.Onboarding.SoftAPDetails;
import com.devilwwj.loginandregister.R;
import com.devilwwj.loginandregister.login.utils.LogUtils;
import com.devilwwj.loginandregister.login.utils.SpUtils;
import com.devilwwj.loginandregister.login.utils.ToastUtils;
import com.umeng.socialize.utils.Log;

import org.alljoyn.onboarding.OnboardingService.AuthType;

public class ProcessOnboarding extends Activity {
    public static final String TAG = "OnboardingClient";
    public static final String TAG_PASSWORD = "OnboardingApplication_password";

    private SoftAPDetails m_device;
    private ProgressBar m_progressBar;
    private IskWifiManager m_WifiManager;
    private OnboardingApplication m_application;
    private BroadcastReceiver m_receiver;
    private String m_networkName;
    private String m_networkPassword;
    private short m_networkAuthType;
    private Intent m_intent;
    private String m_deviceName;
    private boolean isOnboarding = false;
    private boolean m_onceSetConfig = false;
    private boolean isDestroy = false;

    public static void actionStart(Context context, String wifiName, String wifiPwd)
    {
        Intent intent = new Intent(context, ProcessOnboarding.class);
        intent.putExtra("WIFI_NAME", wifiName);
        intent.putExtra("WIFI_PWD", wifiPwd);

        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_process_onboarding);
        m_progressBar = (ProgressBar)findViewById(R.id.probar);
        m_WifiManager = ((OnboardingApplication)getApplication()).getIskWifiManager();//wif service
        m_application = (OnboardingApplication)getApplication();

        m_intent = getIntent();
        m_networkName = m_intent.getStringExtra("WIFI_NAME");
        m_networkPassword = m_intent.getStringExtra("WIFI_PWD");

        Log.d("========", m_networkName);
        Log.d("========", m_networkPassword);
        try {
            connectAp();
            connectAlljoyn();

            listenToAlljoyn();



        } catch (Exception e){
//            e.printStackTrace();
            Log.d("error", "*********************-----------------");
        }

    }


    protected   void connectAp()
    {
        String gateWayAp = SpUtils.getString(this.getApplicationContext(),"GATEWAYAP");
        String gateWayAuth = SpUtils.getString(this.getApplicationContext(),"GATEWAYAUTH");
        String pwd = "";
        if(gateWayAp != null && gateWayAuth != null) {
            Log.d("gateWay*************", gateWayAp);
            Log.d("auth***************", gateWayAuth);
        }

         boolean res =  m_WifiManager.connectToAP(gateWayAp, pwd, gateWayAuth);

        if(res)
        {
            ToastUtils.makeLongText("success to connect", ProcessOnboarding.this);
        }
        else
        {
            ToastUtils.makeLongText("faile to connect",  ProcessOnboarding.this);
        }
        isOnboarding = true;

    }
    private void backToMainActivity()
    {
        ScanDeviceActivity.actionStart(ProcessOnboarding.this);
    }

    private void onBoarding()
    {
        m_application.startSession(m_device);
        configGatway();
        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                backToMainActivity();
                ProcessOnboarding.this.finish();

            }
        }, 10000);
        */
    }

    private  void listenToAlljoyn()
    {
           //************** Class receiver ******************
        m_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(Keys.Actions.ACTION_DEVICE_FOUND.equals(intent.getAction())){

                    String appId = intent.getExtras().getString(Keys.Extras.EXTRA_DEVICE_ID);
                    Log.d(TAG, appId+"====================");
                    m_device = m_application.getDevice(appId);


                    if (m_device == null) {
                        Log.d(TAG, "=============device is null===============");
                  //      closeScreen();
                        return;
                    }
                    else
                    {
                        Log.d(TAG, "=========m_device is not null========= "+m_device.busName);
                        m_deviceName = m_device.deviceFriendlyName;
                    }

                    if(m_deviceName.contains("XMPP"))
                    {
                        //test = false;

                        Jids jids = new Jids(ProcessOnboarding.this, ProcessOnboarding.this);
                        jids.bindingGateWay(appId, m_application);
//                                        jids.sendJidsToGateWay(appId, m_application);

                        Log.d("XMPP", "============"+"xmpp ::"+m_deviceName);
                        m_onceSetConfig = false;
                                    /*
                                    jids.bindingGateWay(appId, m_application);
                                    Log.d("bingGateWay", "==============finished");
                                    */
                    }
                    else
                    {
                        if(isOnboarding) {
                            onBoarding();
                            isOnboarding = false;
                        }
                    }

                }

                else if(Keys.Actions.ACTION_DEVICE_LOST.equals(intent.getAction())){

                }
                else if(Keys.Actions.ACTION_CONNECTED_TO_NETWORK.equals(intent.getAction())){
                    LogUtils.d(TAG, "-----------finished build session-----------------");
                    //LogUtils.d(TAG,"--------------finished build session ---------------");
                }
            }
        };

       IntentFilter filter = new IntentFilter();
       filter.addAction(Keys.Actions.ACTION_DEVICE_FOUND);
       filter.addAction(Keys.Actions.ACTION_DEVICE_LOST);
       filter.addAction(Keys.Actions.ACTION_CONNECTED_TO_NETWORK);
       registerReceiver(m_receiver, filter);
    }

    private  void connectAlljoyn()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String alljoynRealmName = "org.alljoyn.BusNode.OnboardingClient";
                m_application.setRealmName(alljoynRealmName);
                m_application.doConnect();
                m_application.makeToast("AJ_connect done");
            }
        });
    }

    //====================================================================
    private void startOnboardingSession() {

        final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                Log.d(TAG, "startSession: onPreExecute");
            }

            @Override
            protected Void doInBackground(Void... params) {
                m_application.startSession(m_device);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                Log.d(TAG, "startSession: onPostExecute");
            }
        };
        task.execute();
    }
    //====================================================================

    //Let the user know the device not found and we cannot move to this screen
    //Extis this screen after the user pressed OK.
    private void closeScreen() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setMessage("Device was not found");

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                finish();
            }
        });
        alert.show();
    }
    //====================================================================



    private void configGatway()

    {

        String finalPassword;
        //Take the user parameters from the UI:
        LogUtils.d("========", m_networkName);
        LogUtils.d("========", m_networkPassword);

        m_networkAuthType = -3;

        //In case password is WEP and its format is HEX - leave it in HEX format.
        //otherwise convert it from ASCII to HEX
        finalPassword = m_networkPassword;
        if (AuthType.WEP.equals(m_networkAuthType)) {

            Pair<Boolean, Boolean> wepCheckResult = m_application.getIskWifiManager().checkWEPPassword(finalPassword);
            if (!wepCheckResult.first) {//Invalid WEP password
                Log.i(TAG, "Auth type = WEP: password " + finalPassword + " invalid length or charecters");
            } else {
                Log.i(TAG, "configure wifi [WEP] using " + (!wepCheckResult.second ? "ASCII" : "HEX"));
                if (!wepCheckResult.second) {//ASCII. Convert it to HEX
                    finalPassword = m_application.getIskWifiManager().toHexadecimalString(finalPassword);
                }
            }
        } else {//Other auth type than WEP -> convert password to HEX
            finalPassword = m_application.getIskWifiManager().toHexadecimalString(finalPassword);
        }

        try {
            m_application.configureNetwork(m_networkName, finalPassword, m_networkAuthType);
            Log.d(TAG, "finished config===============");
            m_application.connectNetwork();
        }catch (Exception e){
            Log.d(TAG, "***********config and Connect error************");
        }

        m_application.makeToast("Configure network done");
        Log.d(TAG, "configure: onPostExecute");
    }

    /****TO DELETE***/
    private void configGatway_bak()
    {
        final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            private String finalPassword;

            @Override
            protected void onPreExecute() {m_application.connectNetwork();

                //Take the user parameters from the UI:
                m_networkName = m_intent.getStringExtra("WIFINAME");
                m_networkPassword = m_intent.getStringExtra("WIFIPWD");
                m_networkAuthType = -3;

                //In case password is WEP and its format is HEX - leave it in HEX format.
                //otherwise convert it from ASCII to HEX
                finalPassword = m_networkPassword;
                if (AuthType.WEP.equals(m_networkAuthType)) {

                    Pair<Boolean, Boolean> wepCheckResult = m_application.getIskWifiManager().checkWEPPassword(finalPassword);
                    if (!wepCheckResult.first) {//Invalid WEP password
                        Log.i(TAG, "Auth type = WEP: password " + finalPassword + " invalid length or charecters");
                    } else {
                        Log.i(TAG, "configure wifi [WEP] using " + (!wepCheckResult.second ? "ASCII" : "HEX"));
                        if (!wepCheckResult.second) {//ASCII. Convert it to HEX
                            finalPassword = m_application.getIskWifiManager().toHexadecimalString(finalPassword);
                        }
                    }
                } else {//Other auth type than WEP -> convert password to HEX
                    finalPassword = m_application.getIskWifiManager().toHexadecimalString(finalPassword);
                }
                Log.d(TAG, "configure: onPreExecute");
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    m_application.configureNetwork(m_networkName, finalPassword, m_networkAuthType);
                    m_application.connectNetwork();
                }catch (Exception e){
                    Log.d(TAG, "***********config and Connect error************");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                m_application.makeToast("Configure network done");
                Log.d(TAG, "configure: onPostExecute");
            }
        };
        task.execute();
    }


    private void destroy()
    {
        if(isDestroy)
        {
            if(m_receiver != null){
                try{
                    unregisterReceiver(m_receiver);
                    m_application.doDisconnect();
                } catch (Exception e) {
                }
            }

            m_WifiManager.unregisterWifiManager();
            return;
        }

        isDestroy = true;

    }
    @Override
    protected void onPause()
    {
        super.onPause();
        if(isFinishing())
        {
            destroy();
        }
    }

    //=================================================================================
    @Override
    protected void onDestroy() {

        super.onDestroy();
        destroy();

    }
    //=================================================================================

}
