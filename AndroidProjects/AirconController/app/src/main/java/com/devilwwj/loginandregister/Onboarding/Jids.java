package com.devilwwj.loginandregister.Onboarding;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.devilwwj.loginandregister.R;
import com.devilwwj.loginandregister.login.utils.LogUtils;
import com.devilwwj.loginandregister.login.utils.ParseJson;
import com.devilwwj.loginandregister.login.utils.SpUtils;
import com.devilwwj.loginandregister.login.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kylin on 16-8-27.
 */
public class Jids {

    private String roomJid;
    private String mobilePhoneJid;
    private String gateWayJid;
    private RequestQueue mQueue;
    private Context m_context;
    private Activity m_activity;

    public Jids(Context context, Activity activity)
    {
        this.m_context = context;
        this.m_activity = activity;


    }

    private RequestQueue getRequestQueue() {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(m_context);
        }
        return mQueue;
    }

    //public void getJidsFromService()
    public void bindingGateWay(final String appId, final OnboardingApplication application)
    {

        getRequestQueue();
        String url ="http://172.26.1.240:8080/lc-user/auth/gateway/binding";
        Log.d("TAG", "======requst start from hrer======");
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(ParseJson.getBooleanFromJson(response, "success")) {
                            gateWayJid = ParseJson.getStringFromJson(response, "gatewayJid");
                            roomJid = ParseJson.getStringFromJson(response, "roomJid");
                            mobilePhoneJid = ParseJson.getStringFromJson(response, "mobilePhoneJid");
                            sendJidsToGateWay(appId, application);
                        }
                        else
                        {
                            ToastUtils.makeLongText("Faile to get JIds", m_context);
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("getJids", "***********************here");
                    if(error instanceof NoConnectionError){

                        Toast.makeText(m_context,
                                m_context.getString(R.string.error_network_noconnect),
                                Toast.LENGTH_LONG).show();

                    }else if (error instanceof TimeoutError) {
                        LogUtils.d("timeout", "===================");

                        Toast.makeText(m_context,
                                m_context.getString(R.string.error_network_timeout),
                                Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        LogUtils.d("AuthFailure", "===================");

                        Toast.makeText(m_context,
                                m_context.getString(R.string.error_network_Auth),
                                Toast.LENGTH_LONG).show();

                    } else if (error instanceof ServerError) {
                        LogUtils.d("ServerError", "===================");
                        Toast.makeText(m_context,
                                m_context.getString(R.string.error_network_server),
                                Toast.LENGTH_LONG).show();
                    } else if (error instanceof NetworkError) {
                        LogUtils.d("NetworkError", "===================");
                        Toast.makeText(m_context,
                                m_context.getString(R.string.error_network),
                                Toast.LENGTH_LONG).show();
                    }

                }
         }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                String cookies = SpUtils.getString(m_context, "LOGIN_COOKIES");

                if(cookies != null && cookies.length() > 0)
                {
                    HashMap<String, String>headers = new HashMap<String, String>();
                    headers.put("Cookie", cookies);

                    return headers;
                }
                return super.getHeaders();
            }

        };
        mQueue.add(stringRequest);
    }


   public void sendJidsToGateWay(String appId, OnboardingApplication application)
   {
       Map<String, Object> configMap = new HashMap<String, Object>();

       configMap.put("RoomJID",  roomJid);
       configMap.put("UserJID", gateWayJid);
       configMap.put("Roster", mobilePhoneJid);

       ((OnboardingApplication)m_activity.getApplication()).setConfig(configMap, appId, "en");
       Log.d("setconfig", "========finished set config");
   }

    public String getRommJid()
    {
        return roomJid;
    }
    public String getMobilePhoneJid()
    {
        return mobilePhoneJid;
    }
    public String getGateWayJid()
    {
        return gateWayJid;
    }

}
