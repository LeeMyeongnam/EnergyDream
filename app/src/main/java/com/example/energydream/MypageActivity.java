package com.example.energydream;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MypageActivity extends Fragment {

    TextView text_mileage;
    TextView text_elec;
    TextView text_money;

    public MypageActivity() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_mypage, container, false);
        text_mileage = (TextView)rootView.findViewById(R.id.text_mileage_value);
        text_elec = (TextView)rootView.findViewById(R.id.text_elec_value);
        text_money = (TextView)rootView.findViewById(R.id.text_coin_value);

        text_mileage.setText(NavActivity.login_user.getMileage() + "");
        text_elec.setText(NavActivity.login_user.getPower() + " W");
        text_money.setText(NavActivity.login_user.getMoney() + " 원");

        Toast.makeText(getContext(), "마이페이지입니다." , Toast.LENGTH_LONG).show();

        return rootView;
    }
}
