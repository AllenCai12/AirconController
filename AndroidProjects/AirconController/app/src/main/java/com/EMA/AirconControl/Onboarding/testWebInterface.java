package com.EMA.AirconControl.Onboarding;

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
import com.EMA.AirconControl.R;
import com.EMA.AirconControl.login.utils.LogUtils;
import com.EMA.AirconControl.login.utils.SpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kylin on 16-9-19.
 */
public class testWebInterface {
    private RequestQueue mQueue;
    private Context m_context;
    private final String TAG = "testWebInterface";

    public testWebInterface(Context context)
    {
       m_context  = context;
    }

    private RequestQueue getRequestQueue() {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(m_context);
        }
        return mQueue;
    }

    //public void getJidsFromService()
    public void bindingGateWay()
    {
        getRequestQueue();
        String url ="http://172.26.1.240:8080/lc-user/auth/gateway/binding";
        String url1 = "http://172.26.1.240:8080/lc-user/auth/accountManagement/getDetailInfo";

        String findPassword = "http://172.26.1.240:8080/lc-user/mail/sendResetPasswordMail";//服务有错
        String updateUserInfo ="http://172.26.1.240:8080/lc-user/auth/accountManagement/updateDetailInfo";
        String upResult ="http://172.26.1.240:8080/lc-user/auth/gateway/bindingResult";//服务有错
        String UpdatePass = "http://172.26.1.240:8080/lc-user/auth/accountManagement/updatePassword";//服务错误

        Log.d("TAG", "======requst start from hrer======");
        StringRequest stringRequest = new StringRequest(UpdatePass,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "========"+response);
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
                String cookies = SpUtils.getString(m_context, "LOGIN_COOKIES");//TODO 目前保存在本地，可以保存在内存

                if(cookies != null && cookies.length() > 0)
                {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Cookie", cookies);

                    return headers;
                }
                return super.getHeaders();
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("password", "abcd1212");
                map.put("newPassword", "abcd121212");
/*                map.put("gatewayJid", "1232342342342321");
                map.put("status", "0");*/

/*                 map.put("nickname","Allen");
                map.put("sex","man");
                map.put("birthday", "2016-01-01");*/

//                map.put("email", "1103959541@qq.com");
                return map;
            }

        };
        mQueue.add(stringRequest);
    }
}
