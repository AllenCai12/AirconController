package com.EMA.AirconControl.XMPPClient;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.EMA.AirconControl.login.utils.ParseJson;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * Created by kylin on 16-9-14.
 */


public class AirconControlByXmppInterface implements ParticipantStatusListener, ConnectionListener{

    private  final String TAG = "AirconCtlByXmpp";
    private  final String fileName = "AirconContrlByXmpp.json";
    private  final  String roomJId = "fe1da687-7a74-41aa-9cbb-d6329ab24446@conference.localxmpp.leads-cloud.com";
    private Context m_context;
    private String m_jsonFileText;
    private String m_request;
    private XmppClient m_xmppClient;

    private boolean m_isConnectXmpp = false;


   public AirconControlByXmppInterface(Context context)
   {

       m_context = context;
       m_jsonFileText = getJson();
       Log.d(TAG, "======new xmppclient=========");
       m_xmppClient = new XmppClient(context, this);

   }

    public void connectXmppServer()
    {
        m_xmppClient.connectXMPPServer();
    }

    private String getJson()
    {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = m_context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public String[] getAllAirconIDs()
    {
        String[] stringArrayTmp = null;

        try {
            String tmpString = replyMessage("GetAllAirconIDs", "", "");

            Log.d(TAG, "========"+tmpString+"===-=======");

            if(0 == ParseJson.getIntFromJson(tmpString, "responseCode")) {

                stringArrayTmp = ParseJson.getStringArrayFromJson(tmpString, "airconIDs");
            }
            else
            {
                Log.d(TAG, "=========response error from gateWAy======");
//                return null;
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "=======ERROR===getAllAirconIDs=========");

        }
        return  stringArrayTmp;

    }

    /*parse the request and send the request to xmpps server ,than get the reply String*/
    public String replyMessage(String requestMethod, String airconID, String language) throws Exception
    {
        String getRequestMessage = "";
        try {
            JSONObject object = new JSONObject(m_jsonFileText);
            getRequestMessage =  object.getJSONObject(requestMethod).toString();
            if(!airconID.equals("") ) {
                getRequestMessage = ParseJson.setStringToJson(getRequestMessage, "airconIDs", airconID).toString();
            }
            if(!language.equals("")){
                getRequestMessage = ParseJson.setStringToJson(getRequestMessage, "language", language).toString();
            }

            Log.d(TAG, getRequestMessage);

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return m_xmppClient.sendMessage(roomJId,getRequestMessage);
    }


    public String[] getAirconSupportedLanguages(String airconID)
    {


        String tmpString = " ";
        String[] languages = null;
        try {

            tmpString = replyMessage("GetAirconSupportedLanguages", airconID, "");

            if (0 == ParseJson.getIntFromJson(tmpString, "responseCode")) {
                languages =  ParseJson.getStringArrayFromJson(tmpString, "supportedLanguages");
            } else {
                Log.d(TAG, "=========response error from gateWAy======");
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "========Error getAirconLanguage ==========");
        }

        return  languages;
    }


    public String getAirconName(String airconID, String language)
    {
        String airconNameString = null;
        try {
            String tmpString = replyMessage("GetAirconName", airconID, language);

            if(0 == ParseJson.getIntFromJson(tmpString, "responseCode")) {
                airconNameString =  ParseJson.getStringFromJson(tmpString, "airconName");
            }
            else
            {
                Log.d(TAG, "=========response error from gateWAy======");
            }

        }catch (Exception e)
        {
           e.printStackTrace();
            Log.d(TAG, "============Error in getAirconName =============");
        }
        return airconNameString;
    }

    public String getAirconState(String airconID)
    {
        String airconStateJson = null;

        try {

            String tmpString = replyMessage("GetAirconState", airconID, "");
            Log.d(TAG, "====start get Aircon State======");

            if(0 == ParseJson.getIntFromJson(tmpString, "responseCode")) {
//            return ParseJson.getStringFromJson(tmpString, "airconName");
                 airconStateJson = ParseJson.getJsonStringFromJson(tmpString, "airconState").toString();
                Log.d(TAG, airconStateJson);
            }
            else
            {
                Log.d(TAG, "=========response error from gateWAy======");
                return null;
            }

        }catch (Exception e)
        {
          e.printStackTrace();
          Log.d(TAG, "===Error in getAirconState==========");
        }

        return airconStateJson;
    }

    public void setAirconStateField(String airconID, String stateField, Object object )
    {
        String getRequestMessage = "";
        String tmpString;

        try {
            JSONObject jsonObject = new JSONObject(m_jsonFileText);
            getRequestMessage = jsonObject.getJSONObject("SetAirconOnOffState").toString();
            getRequestMessage = ParseJson.setStringToJson(getRequestMessage, "airconIDs", airconID).toString();
            getRequestMessage = ParseJson.setStringToJson(getRequestMessage, "airconStateFieldName", stateField).toString();

            if(stateField.equals("OnOff")) {
                getRequestMessage = ParseJson.setBooleanToJson(getRequestMessage, "airconStateFieldValue", (Boolean)object).toString();
            }
            if(stateField.equals("Mode")) {
                getRequestMessage = ParseJson.setStringToJson(getRequestMessage, "airconStateFieldValue", object.toString()).toString();
            }
            if(stateField.equals("Temperature")) {
                getRequestMessage = ParseJson.setIntToJson(getRequestMessage, "airconStateFieldValue", (int)object).toString();
            }
            if(stateField.equals("FanGear")) {
                getRequestMessage = ParseJson.setIntToJson(getRequestMessage, "airconStateFieldValue", (int)object).toString();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.d(TAG, "====="+getRequestMessage);

        tmpString =  m_xmppClient.sendMessage(roomJId,getRequestMessage);

        if(0 == ParseJson.getIntFromJson(tmpString, "responseCode")) {
            Log.d(TAG, "=========response Success from gateWAy======");

        }
        else
        {
            Log.d(TAG, "=========response error from gateWAy======");
        }
    }

    @Override
    public void joined(String participant) {
        Log.d("room","========== joined =========="+participant);
        m_isConnectXmpp = true;
    }

    @Override
    public void left(String participant) {
        Log.d("room","========== left =========="+participant);
//        m_isConnectXmpp = false;
    }

    @Override
    public void kicked(String participant, String actor, String reason) {
        Log.d("room","========== kicked by someone =========="+participant);
    }

    @Override
    public void voiceGranted(String participant) {
        // TODO Auto-generated method stub

    }

    @Override
    public void voiceRevoked(String participant) {
        // TODO Auto-generated method stub

    }

    @Override
    public void banned(String participant, String actor, String reason) {
        // TODO Auto-generated method stub

    }

    @Override
    public void membershipGranted(String participant) {
        // TODO Auto-generated method stub

    }

    @Override
    public void membershipRevoked(String participant) {
        // TODO Auto-generated method stub

    }

    @Override
    public void moderatorGranted(String participant) {
        // TODO Auto-generated method stub

    }

    @Override
    public void moderatorRevoked(String participant) {
        // TODO Auto-generated method stub

    }

    @Override
    public void ownershipGranted(String participant) {
        // TODO Auto-generated method stub

    }

    @Override
    public void ownershipRevoked(String participant) {
        // TODO Auto-generated method stub

    }

    @Override
    public void adminGranted(String participant) {
        // TODO Auto-generated method stub

    }

    @Override
    public void adminRevoked(String participant) {
        // TODO Auto-generated method stub

    }

    @Override
    public void nicknameChanged(String participant, String newNickname) {
        // TODO Auto-generated method stub

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean auth) {
        Log.d("ConnectListener","==========authenticated========");
    }

    @Override
    public void connected(XMPPConnection connection) {
        Log.d("ConnectListener","============connected==========");
    }

    @Override
    public void connectionClosed() {
        Log.d("ConnectListener","============connectionClosed==========");
    }

    @Override
    public void connectionClosedOnError(Exception arg0) {
        Log.d("ConnectListener","============connectionClosedOnError==========");

/*        xmppService.cleanConnection();
        timer = new Timer();
        timer.schedule(new MyTimeTask(), interval);*/
    }

    @Override
    public void reconnectingIn(int arg0) {
        Log.d("ConnectListener","============reconnectingIn==========");
    }

    @Override
    public void reconnectionFailed(Exception arg0) {
        Log.d("ConnectListener","============reconnectionFailed==========");
    }

    @Override
    public void reconnectionSuccessful() {
        Log.d("ConnectListener","============reconnectionSuccessful==========");
    }

}
