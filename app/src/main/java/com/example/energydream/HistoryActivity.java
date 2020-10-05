package com.example.energydream;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.energydream.HistoryAdapter;
import com.example.energydream.ItemData;
import com.example.energydream.Model.Business;
import com.example.energydream.Model.Member;
import com.example.energydream.NavActivity;
import com.example.energydream.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends Fragment {

    TextView userName;
    TextView userMileage;
    ListView listview = null;

    public HistoryActivity(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);
        userName = (TextView)view.findViewById(R.id.userName);
        userMileage = (TextView)view.findViewById(R.id.userBal);

        userName.setText(NavActivity.login_user.getName());
        userMileage.setText(NavActivity.login_user.getMileage() + "");

        SimpleDateFormat Dateformat = new SimpleDateFormat("MM-dd HH:mm");
        long time = System.currentTimeMillis();
        String timeStr = Dateformat.format(new Date(time));

        // 기부내역 객체 생성
        ArrayList<ItemData> dona_hist = new ArrayList<>();
        for(int i = 0; i<NavActivity.login_user.getDonationList().size(); i++){
            Member.Donation_user dona_list = NavActivity.login_user.getDonationList().get(i);
            dona_hist.add(new ItemData(dona_list.get_company_name(), dona_list.get_business_name(),
                                        timeStr, dona_list.get_mileage()));
        }


        listview = (ListView)view.findViewById(R.id.history_list);
        HistoryAdapter adapter = new HistoryAdapter(dona_hist);
        listview.setAdapter(adapter);


        return view;
    }
}
