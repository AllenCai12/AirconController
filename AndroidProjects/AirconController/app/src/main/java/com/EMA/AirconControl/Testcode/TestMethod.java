package com.EMA.AirconControl.Testcode;

import android.util.Log;

import com.EMA.AirconControl.AlljoynClient.AlljoynClientImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kylin on 16-9-29.
 */
public class TestMethod {

    private  final static String TAG = "TestMethod";


    public static void alljoynInterseTest(AlljoynClientImpl  alljoynClient)
    {
        try {
            for (String tmp : alljoynClient.getAllAirconIDs())
            {
                Log.d(TAG, "========id========"+tmp);
                for(String language : alljoynClient.getSupportedLanguagees(tmp))
                {
                    Log.d(TAG, "========language========"+language);
                }
//              Log.d(TAG, "=======name======"+m_alljoynClient.getAirconName( "en"));

/*              for(Map.Entry<String, Variant>entry : m_alljoynClient.getAirconState(tmp).entrySet())
              {
                  Log.d(TAG, "======key===="+entry.getKey()+"=========value==="+entry.getValue());
              }*/

//              JSONObject json = JsonUtils.mapToJson(m_alljoynClient.getAirconState(tmp));
//              Log.d(TAG, "jsonstate=========="+json.toString());

                alljoynClient.setAirconStateField(tmp, "OnOff", true);

//              m_alljoynClient.setAirconStateField(tmp, "Temperature", 25.00);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void testList()
    {
        List<String> tmpList = new ArrayList<String>();

        tmpList.add("aaaaa");
        tmpList.add("aaaaa");
        tmpList.add("aaaaa");
        tmpList.add("aaaaa");
        tmpList.add("aaaaa");
        tmpList.add("aaaaa");
        tmpList.add("aaaaa");

        for(String tmp : tmpList)
        {
            Log.d(TAG, "=========testList======"+ tmp);
        }

    }


}
