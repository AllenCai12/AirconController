package com.EMA.AirconControl.Onboarding.ConfigWifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.EMA.AirconControl.R;

public class ConfigWifi extends Activity implements View.OnClickListener {
    private EditText m_wifiText;
    private EditText m_passwordText;
    private Intent  m_configWifiIntent;

    public static void actionStart(Context context,  String wifiSsid)
    {
        Intent intent = new Intent(context, ConfigWifi.class);
        intent.putExtra("SSID", wifiSsid);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_wfifi);
        initViews();

    }

    @Override
    public void onClick(View v)
    {

        String wifiName = m_wifiText.getText().toString();
        String wifiPwd = m_passwordText.getText().toString();
        Log.d("==========", wifiName );
        Log.d("==========", wifiPwd);
        ProcessOnboarding.actionStart(ConfigWifi.this, wifiName, wifiPwd);

    }

    private void initViews()
    {
         m_configWifiIntent = getIntent();

         m_wifiText = (EditText)this.findViewById(R.id.select_wifi);
         m_wifiText.setText(m_configWifiIntent.getStringExtra("SSID"));

         m_wifiText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getX() >= (m_wifiText.getWidth() - m_wifiText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                    {
                        ScanWIFIList.actionStart(ConfigWifi.this);
                        return true;
                    }

                }
                return false;
            }
        });
         m_passwordText = (EditText)this.findViewById(R.id.pass_input);
         m_passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());

         m_passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
             @Override
             public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                 if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_DONE)//如果输入完成自动登录
                 {

                 }
                 return false;
             }
         });
    }
}
