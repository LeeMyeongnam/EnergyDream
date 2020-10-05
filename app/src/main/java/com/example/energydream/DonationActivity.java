package com.example.energydream;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;

/* 에너지기부/소셜벤처사업에 기부할 마일리지 입력 페이지 */

public class DonationActivity extends Fragment {

    TextView text_type;
    TextView text_name;
    TextView text_hold_mileage;
    EditText edit_mileage;
    Button btn_donation;

    Business cur_business;
    int index;
    //int fragment_id;
    boolean isVenture;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_donation, container, false);
        text_type = (TextView)rootView.findViewById(R.id.text_type);
        text_name = (TextView)rootView.findViewById(R.id.text_name);
        text_hold_mileage = (TextView)rootView.findViewById(R.id.hold_mileage_value);
        edit_mileage = (EditText)rootView.findViewById(R.id.edit_mileage);
        btn_donation = (Button)rootView.findViewById(R.id.btn_donation);

        Bundle bundle = getArguments();

        // 넘어온 프래그먼트에 따라서 가져올 데이터 리스트 구분(ventrue/donation)
        index = Integer.parseInt(bundle.getString("index"));
        isVenture = bundle.getBoolean("isVenture");
        cur_business = (isVenture) ? NavActivity.venture_list.get(index) : NavActivity.donation_list.get(index);

        setData(cur_business);


        // 기부하기 버튼 누를 시 '기부하시겠습니까?' Dialog 띄워주기
        btn_donation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_mileage.getText().toString().length() == 0){
                    Toast.makeText(getContext(), "기부 마일리지를 입력해주세요", Toast.LENGTH_LONG).show();
                }else{
                    int dona_mile = Integer.parseInt(edit_mileage.getText().toString());
                    if(dona_mile > NavActivity.login_user.getMileage()){
                        Toast.makeText(getContext(), "마일리지가 부족합니다.", Toast.LENGTH_LONG).show();
                    }else{
                        makeDialog();
                    }
                }
            }
        });


        return rootView;
    }

    private void setData(Business business){

        if(isVenture)
            text_type.setText("벤처기업 후원하기");
        else
            text_type.setText("에너지 기부하기");

        text_name.setText(business.getBusinessName());
        text_hold_mileage.setText(NavActivity.login_user.getMileage() + "");

    }


    private void makeDialog(){

        DonationActivityDialog donationDialog = new DonationActivityDialog();
        Bundle bundle = new Bundle();
        bundle.putString("index", index+"");
        bundle.putString("mileage", edit_mileage.getText().toString());
        bundle.putBoolean("isVenture", isVenture);
        donationDialog.setArguments(bundle);
        donationDialog.show(getActivity().getFragmentManager(), "registration dial");

    }
}
