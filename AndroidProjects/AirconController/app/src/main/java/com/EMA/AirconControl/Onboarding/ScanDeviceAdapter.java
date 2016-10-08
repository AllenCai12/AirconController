package com.EMA.AirconControl.Onboarding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.EMA.AirconControl.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by kylin on 16-9-17.
 */
public class ScanDeviceAdapter extends ArrayAdapter<String> {
    private LayoutInflater m_layoutInflater;
    private ArrayList<String> m_properties;

   public ScanDeviceAdapter(Context context, int textViewResourceId,
                            List<String> objects)
   {
     super(context, textViewResourceId, objects);


   }


    // ====================================================================
    /**
     * @param layoutInflater
     *            Set a layout inflater. Will be used to inflate the views to
     *            display.
     */
    public void setLayoutInflator(LayoutInflater layoutInflater) {
        m_layoutInflater = layoutInflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;

        if (convertView == null)
            row = m_layoutInflater.inflate(R.layout.device_name, parent, false);
        else
            row = convertView;

        final String property = m_properties.get(position);
        if (property != null) {
            // Property name
            TextView propertyName = (TextView) row.findViewById(R.id.propertyName);

        }
        return row;

    }


}
