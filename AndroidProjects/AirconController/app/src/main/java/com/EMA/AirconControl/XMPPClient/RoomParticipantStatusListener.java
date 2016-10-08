package com.EMA.AirconControl.XMPPClient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.EMA.AirconControl.Service.ViewBroadcast;

import org.jivesoftware.smackx.muc.ParticipantStatusListener;

/**
 * 
 * @author Allen
 * @version 1.0 Create at 2016年8月31日 下午2:19:07
 */
public class RoomParticipantStatusListener implements ParticipantStatusListener {

    private Context m_context;

    public RoomParticipantStatusListener(Context context)
    {
        m_context = context;
    }


    @Override
    public void joined(String participant) {
        Log.d("room","========== joined =========="+participant);
        Intent intent = new Intent(ViewBroadcast.SERVICE_CONNECT_XMPP);
        m_context.sendBroadcast(intent);
    }

    @Override
    public void left(String participant) {
        Log.d("room","========== left =========="+participant);
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

}
