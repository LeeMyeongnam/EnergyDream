package com.example.energydream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.energydream.Model.Member;
import com.example.energydream.Model.StandyPower;
import com.example.energydream.NavActivity;
import com.example.energydream.R;
import com.example.energydream.RecyclerAdapter_venture;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
     QR코드를 인식해서 콘센트 고유 번호만 저장
*/
public class AddConcentActivity extends Fragment {

    Button btn_bbiyoung;
    Button btn_test;
    Button btn_QR;

    IntentIntegrator qrScanner;
    ///은소로로로로//////

    long savedPower = 0;


    public AddConcentActivity(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_concent, container, false);

        btn_test = (Button)view.findViewById(R.id.btn_test);
        btn_bbiyoung = (Button)view.findViewById(R.id.btn_bbiyong);
        btn_QR = (Button)view.findViewById(R.id.btn_QR);
        qrScanner = new IntentIntegrator(getActivity());

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                energySaving();
            }
        });

        btn_bbiyoung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generStandy();  // 대기전력 발생
            }
        });

        btn_QR.setOnClickListener(addConcent); //콘센트 추가 버튼
        return view;
    }


    //은솔 수정
    View.OnClickListener addConcent = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            getStandyPower();
        }
    };
    //뿌뿌

    private void energySaving(){
        //파이어베이스 값 셋팅
        //isCalc = true시 총 와트를 계산한다.
        NavActivity.mReference.child("StandyPower").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StandyPower standyPower = dataSnapshot.getValue(StandyPower.class);

                if(standyPower!=null) {

                    standyPower.setEnd_time();
                    savedPower = standyPower.calcSavePower();

                    int mileage = (int)(savedPower * 10);

                    if(NavActivity.login_user != null) {
                        NavActivity.login_user.savePower(savedPower);
                        NavActivity.login_user.addMileage(mileage);

                        NavActivity.mReference.child("Members").child(NavActivity.login_user.getName())
                                .setValue(NavActivity.login_user);

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


    private void generStandy(){
        /*
        standyPower.setExist(true);
        int unit_power = 3; // 대기전력 3와트라 가정
        standyPower.setExist(true);
        standyPower.setUnit_power(3);
        standyPower.setStart_time();

        NavActivity.mReference.child("StandyPower").setValue(standyPower);*/
    }

    private void getStandyPower(){

        NavActivity.mReference.child("StandyPower").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NavActivity.standyPower = dataSnapshot.getValue(StandyPower.class);

                // DB에 존재하지 않으면 QR코드 인식기 시행
                if(NavActivity.standyPower == null){

                    qrScanner.forSupportFragment(NavActivity.manager.findFragmentById(R.id.content_fragment_layout))
                            .setOrientationLocked(false)
                            .setPrompt("바코드를 콘센트의 큐알코드에 비춰주세요")
                            .initiateScan();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //콘센트넣기
    private void addStandyPower(String res){
        Log.d("만듬","0000");
        NavActivity.mReference.child("StandyPower").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NavActivity.standyPower = dataSnapshot.getValue(StandyPower.class);
                Log.d("만듬","1111");

                // DB에 존재하지 않으면 새로 객체를 만들어 저장한다.
                if(NavActivity.standyPower == null){
                    NavActivity.standyPower = new StandyPower();
                    NavActivity.standyPower.setId(res);
                    Log.d("만듬",NavActivity.standyPower.getId());
                    NavActivity.mReference.child("StandyPower").setValue(NavActivity.standyPower);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //은솔추가 큐알코드 함수///
    //Getting the scan results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "취소!", Toast.LENGTH_SHORT).show();
            } else {
                //qrcode 결과가 있으면
                Toast.makeText(getContext(), "스캔완료!", Toast.LENGTH_SHORT).show();
                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    //firebase에 넣어줄 string 값

                    if(obj != null) {

                        String res = null;

                        //QR코드로부터 콘센트 id 얻어 옴.
                        try {

                            res = obj.getString("concentID");

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                        //파이어베이스 넣는 작업 시작
                        if (res != null) {

                            addStandyPower(res);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), result.getContents(), Toast.LENGTH_LONG).show();
                    //textViewResult.setText(result.getContents());
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //////여기뿌악끝////////////
}
