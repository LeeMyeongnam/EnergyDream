package com.example.energydream;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.StandyPower;
import com.example.energydream.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Fragment {


    Button btn_StandyPower;
    Button btn_ConcentOn;

    private boolean standy_check; //대기전력 발생 여부
    private DatabaseReference mainReference;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_main, container, false);
        btn_StandyPower = (Button)rootView.findViewById(R.id.btn_checkisExist);
        btn_ConcentOn = (Button)rootView.findViewById(R.id.btn_elecOn);

        mainReference = FirebaseDatabase.getInstance().getReference();
        checkStandyPower_isExist();
        checkConcentUsage();


        standy_check = false;
        Log.d("띠용 시작","ㅇ");

        //대기전력 차단 버튼
        btn_StandyPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startStandyBtnAction();
            }
        });

        //콘센트 On 버튼
        btn_ConcentOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startConcentBtnAction();
            }
        });

        return rootView;
    }

    public void startConcentBtnAction(){

        mainReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int op = dataSnapshot.child("operation").getValue(Integer.class);
                boolean usage =dataSnapshot.child("usage").getValue(Boolean.class);
                StandyPower standyPower = dataSnapshot.child("StandyPower").getValue(StandyPower.class);

                if( standyPower != null) {
                    //사용중이 아닐때만 킨다.
                    if (usage == false) {
                        boolean isCalc = standyPower.isCalc();

                        if (op == 0) {
                            //계산함수 돌리기
                            if (isCalc) {
                                Log.d("계산할꿰!", "뀨");
                                energySaving();
                                Toast.makeText(getContext(), "나 꼐산했오오옹", Toast.LENGTH_LONG).show();
                            }

                            mainReference.child("operation").setValue(1);

                        } else {
                            Toast.makeText(getContext(), "다른 작업을 처리하고 있습니다.", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        //사용중이 아닐 때

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void startStandyBtnAction(){

        mainReference.child("operation").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                int op = dataSnapshot.getValue(Integer.class);

                if(standy_check==true){

                    if(op == 0){
                        //대기전력 지금 차단하러 가겠습니다!!!!!!! 가즈아!!!!!!!!1
                        Intent gointent = new Intent(getContext(),ArActionActivity.class);
                        startActivityForResult(gointent,3000);

                    }else if(op == 2){
                        Toast.makeText(getContext(), "작업을 처리하고 있습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void checkConcentUsage(){

        mainReference.child("usage").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean usage = dataSnapshot.getValue(boolean.class);

                if(usage){
                    ConcentBtnNotActive concentBtnNotActive = new ConcentBtnNotActive();
                    concentBtnNotActive.start();
                }else{
                    ConcentBtnActive concentBtnActive = new ConcentBtnActive();
                    concentBtnActive.start();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //은솔은솔 -> 대기전력 차단 버튼 활성화/비활성화
    public void checkStandyPower_isExist(){

        mainReference.child("StandyPower").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StandyPower check_standyPower = dataSnapshot.getValue(StandyPower.class);

                if(check_standyPower!=null) {
                    //대기전력 발생여부
                    boolean check_IsStandyPower = check_standyPower.isExist();

                    //True이면 버튼 울고있는모양
                    if(check_IsStandyPower){
                        //UI 활성화 -> 울고있는 버튼으로

                        Log.d(".....","띠용1");
                        standy_check = true;
                        StandyBtnActive standyBtnActive = new StandyBtnActive();
                        standyBtnActive.start();


                    }else {
                        //False면 버튼 웃고있는모양으로
                        Log.d(".....","띠용2");

                        standy_check = false;
                        ChangeOP(); //operation 값 변경
                        StandyBtnNotActive standyBtnNotActive = new StandyBtnNotActive();
                        standyBtnNotActive.start();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void energySaving(){
        //파이어베이스 값 셋팅
        //isCalc = true시 총 와트를 계산한다.
        NavActivity.mReference.child("StandyPower").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StandyPower standyPower = dataSnapshot.getValue(StandyPower.class);

                if(standyPower!=null) {

                    standyPower.setEnd_time();
                    long savedPower = standyPower.calcSavePower();

                    int mileage = (int)(savedPower * 10);

                    if(NavActivity.login_user != null) {
                        Log.d("세이브에너지 체크","1");
                        NavActivity.login_user.savePower(savedPower);
                        NavActivity.login_user.addMileage(mileage);

                        Log.d("세이브에너지 체크","2");
                        NavActivity.mReference.child("Members").child(NavActivity.login_user.getName())
                                .setValue(NavActivity.login_user);
                        Log.d("세이브에너지 체크","3");

                        Toast.makeText(getContext(), savedPower + "W 절약!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "기업회원은 사업등록이나 하세요", Toast.LENGTH_SHORT).show();
                    }
                    //Standy Power 초기화
                    NavActivity.mReference.child("StandyPower").child("isCalc").setValue(false);
                    NavActivity.mReference.child("StandyPower").child("start_time").setValue(0);
                    NavActivity.mReference.child("StandyPower").child("end_time").setValue(0);
                    NavActivity.mReference.child("StandyPower").child("unit_power").setValue(0);
                    NavActivity.mReference.child("StandyPower").child("save_power").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void ChangeOP(){


        mainReference.child("operation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int op = dataSnapshot.getValue(Integer.class);

                //차단이벤트 완료 됬으니 연산값 변경
                if(op == 2)
                    mainReference.child("operation").setValue(0);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //대기전력차단버튼 Active
    private class StandyBtnActive extends Thread{
        @Override
        public void run(){

            if(getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_StandyPower.setText("대기전력 걱정좀하시죠?");
                    }
                });
            }
        }
    }

    //대기전력차단버튼 비활성화
    private class StandyBtnNotActive extends Thread{
        @Override
        public void run() {

            if(getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_StandyPower.setText("대기전력 걱정 니니해");
                    }
                });
            }
        }
    }

    //콘센트 ON 버튼 활성화
    private class ConcentBtnActive extends Thread{
        @Override
        public void run() {

            if(getActivity()!=null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_ConcentOn.setText("콘센트를 켤까요?");
                    }
                });
            }
        }
    }
    //콘센트ON 버튼 비활성화
    private class ConcentBtnNotActive extends Thread{
        @Override
        public void run() {

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_ConcentOn.setText("콘센트가 이미 켜져있네욤.");
                    }
                });
            }
        }
    }

}
