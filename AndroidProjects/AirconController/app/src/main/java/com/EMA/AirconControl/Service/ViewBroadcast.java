package com.EMA.AirconControl.Service;

/*
 * Created by kylin on 16-9-22.
 */
public class ViewBroadcast {
    public static String UPDATE_LIST = "view_update_list";
    public static String UPDATE_AIRCON_STATUS_DATA = "view_update_aircon_status_data";


    public static String SERVICE_CONNECT_XMPP = "service_connect_to_xmpp";
    public static String SERVICE_CONNECT_ALLJOYON = "service_connect_to_alljoyn";
    public static String SERVICE_DISCONNECT_XMPP = "service_disconnect_to_xmpp";
    public static String SERVICE_DISCONNECT_ALLJOYON = "service_disconnect_to_alljoyn";

    public  class Extras {
        public static final String DEVICE_STATE_DATA = "extra_device_state";
    }


}
