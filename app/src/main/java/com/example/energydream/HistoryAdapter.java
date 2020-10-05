package com.example.energydream;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 이명남 on 2018-11-09.
 */

public class HistoryAdapter extends BaseAdapter {
    LayoutInflater inflater = null;

    ArrayList<ItemData> m_data;

    public HistoryAdapter(ArrayList<ItemData> m_data){
        this.m_data = m_data;
    }
    @Override
    public int getCount() {
        return m_data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();

            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.history_item, parent, false);
        }

        TextView busi_name = (TextView) convertView.findViewById(R.id.history_busi_name);
        TextView comp_name = (TextView) convertView.findViewById(R.id.history_comp_name);
        TextView coin = (TextView) convertView.findViewById(R.id.history_coin);
        TextView date = (TextView) convertView.findViewById(R.id.history_date);

        busi_name.setText(m_data.get(position).busi_name);
        comp_name.setText(m_data.get(position).comp_name);
        coin.setText(m_data.get(position).coin + "");
        date.setText(m_data.get(position).date);

        return convertView;
    }

}

class ItemData{

    public String comp_name; // 기업명
    public String busi_name; // 사업명
    public String date;     // 기부날짜
    public int coin;        // 기부금액

    public ItemData(String c_name, String b_date, String date, int coin){
        this.comp_name = c_name;
        this.busi_name = b_date;
        this.date = date;
        this.coin = coin;
    }
}