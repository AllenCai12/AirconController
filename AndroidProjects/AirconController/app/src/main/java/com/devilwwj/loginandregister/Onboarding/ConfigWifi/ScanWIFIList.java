package com.devilwwj.loginandregister.Onboarding.ConfigWifi;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.devilwwj.loginandregister.Onboarding.IskWifiManager;
import com.devilwwj.loginandregister.Onboarding.OnboardingApplication;
import com.devilwwj.loginandregister.Onboarding.WifiManagerListener;
import com.devilwwj.loginandregister.R;
import com.devilwwj.loginandregister.login.utils.LogUtils;

import java.util.List;
import java.util.Timer;

public class ScanWIFIList extends ListActivity {
    // ListAdapter
    private ScanWIFIAdapter m_adapter;
    // Lets the user kick off a scan
    private Button m_scanWIFIButton;
    // List of devices
    private ListView m_list;
    // Wi-Fi manager
    private IskWifiManager m_WifiManager;
    // UI feedback for long actions like scanning
    private ProgressDialog m_progressDialog;
    private ProgressDialog m_loadingPopup;
    // Dialog dismiss timer
    private Timer m_timer;
    private BroadcastReceiver m_receiver;

    public static void actionStart(Context context)
    {
        Intent intent = new Intent(context, ScanWIFIList.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan_list);

        m_WifiManager = ((OnboardingApplication) getApplication()).getIskWifiManager();//wif service
        m_list = (ListView) findViewById(android.R.id.list);
        m_adapter = new ScanWIFIAdapter(ScanWIFIList.this, R.id.wifi_name_row_textview);//数据与listview的桥梁
        m_list.setAdapter(m_adapter);
        initWIFIList();
        //m_loadingPopup = new ProgressDialog(this);

    }

    private void initWIFIList() {

        m_WifiManager.scanForWifi(getApplicationContext(), new WifiManagerListener() {
            public void OnScanResultComplete(final List<ScanResult> results) {
                // reset the list to show up to date scan result
//                m_adapter.clear();
                m_adapter.addAll(results);
                m_list.setAdapter(m_adapter);
            }
        }, "");

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final ScanResult scanItem = m_adapter.getItem(position);

        if (scanItem != null){
            LogUtils.d("------------wifiName", scanItem.SSID);
            LogUtils.d("************wifiAuth", scanItem.capabilities);

            ConfigWifi.actionStart(ScanWIFIList.this,scanItem.SSID);
            this.finish();
        }

    }
}
