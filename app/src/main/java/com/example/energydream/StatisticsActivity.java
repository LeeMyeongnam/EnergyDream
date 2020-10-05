package com.example.energydream;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.energydream.Model.Member;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
    우리집 <-> 모든사람
    기부랭킹
    막대 그래프로 지역별 랭킹
*/
public class StatisticsActivity extends Fragment {

    ArrayList<BarEntry> entries;    // 데이터값 리스트
    ArrayList<String> labels;       // 지역 이름
    String city[];
    int donation_value[];   // 지역별 donation 총 합
    int venture_value[];    // 지역별 venture 총 합
    boolean isLoadDB; // DB 로드 여부
    boolean isVenture;// 벤처 / 에너지기부 구분 변수

    BarChart chart;     // 차트 layout
    BarDataSet dataset; //
    Button btn_venture_chart;
    Button btn_donation_chart;


    public StatisticsActivity(){}
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_statistics, container, false);
        chart = view.findViewById(R.id.barchart);
        btn_venture_chart = (Button)view.findViewById(R.id.btn_venture_chart);
        btn_donation_chart = (Button)view.findViewById(R.id.btn_donation_chart);

        entries = new ArrayList<>();
        city = new String[]{"서울", "부산", "인천","대구", "광주", "대전", "울산", "세종", "경기","강원", "경북", "경남", "충북", "충남", "전북", "전남", "제주"};
        donation_value = new int[17];  // 지역별 에너지 기부 통계
        venture_value = new int[17];   // 지역별 소셜벤처기업 응원 통계
        isLoadDB = false;
        isVenture = true;

        labels = new ArrayList<String>();
        for(int i=0; i<17; i++)     // 라벨(지역이름) 설정
            labels.add(city[i]);


        // 지역별 기부내역 데이터 가져오기
        getRegionData();


        btn_venture_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLoadDB && !isVenture){
                    isVenture = true;
                    setVentureChart();

                    btn_venture_chart.setTextColor(Color.WHITE);
                    btn_venture_chart.setBackgroundColor(Color.BLACK);
                    btn_donation_chart.setTextColor(Color.BLACK);
                    btn_donation_chart.setBackgroundColor(Color.WHITE);
                }
            }
        });

        btn_donation_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(isLoadDB && isVenture) {
                    isVenture = false;
                    setDonationChart();

                    btn_venture_chart.setTextColor(Color.BLACK);
                    btn_venture_chart.setBackgroundColor(Color.WHITE);
                    btn_donation_chart.setTextColor(Color.WHITE);
                    btn_donation_chart.setBackgroundColor(Color.BLACK);
                }
            }
        });

        return view;
    }

    private void getRegionData(){
        NavActivity.mReference.child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // DB 값 불러오기
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Member member = snapshot.getValue(Member.class);
                    getDataFromMember(member);
                }
                isLoadDB = true;
                // DB 데이터 저장 후 차트 설정하는 함수 호출
                setVentureChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getDataFromMember(Member member){
        // 지역 index 번호 가져오기
        int region = getRegionNum(member.getRegion());

        if(region == -1){
            Toast.makeText(getContext(), "잘못된 지역값이 들어가 있습니다." ,Toast.LENGTH_LONG).show();
        }else{
            for(int i=0; i<member.getDonationList().size(); i++){
                // 벤처응원/에너지기부 구분해서 통계 값 저장
                if(member.getDonationList().get(i).getIs_venture())
                    venture_value[region] += member.getDonationList().get(i).get_mileage();
                else
                    donation_value[region] += member.getDonationList().get(i).get_mileage();
            }
        }
    }


    private void setVentureChart(){
        entries.clear();
        for(int i=0; i<17; i++)
            entries.add(new BarEntry(venture_value[i], i));

        BarDataSet dataset = new BarDataSet(entries, "마일리지");
        BarData data = new BarData(labels, dataset);
        chart.setData(data);
        chart.setDescription("전국 소셜벤처 기부 마일리지 순위");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.invalidate();

    }

    private void setDonationChart(){
        entries.clear();
        for(int i=0; i<17; i++)
            entries.add(new BarEntry(donation_value[i], i));

        BarDataSet dataset = new BarDataSet(entries, "마일리지");
        BarData data = new BarData(labels, dataset);
        chart.setData(data);
        chart.setDescription("전국 에너지 기부 마일리지 순위");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.invalidate();

    }



    // 지역 String 값으로 지역 고유 번호(배열 인덱스) 얻어오는 함수
    private int getRegionNum(String str_region){
        if(str_region.equals("서울")) return 0;
        else if(str_region.equals("부산")) return 1;
        else if(str_region.equals("인천")) return 2;
        else if(str_region.equals("대구")) return 3;
        else if(str_region.equals("광주")) return 4;
        else if(str_region.equals("대전")) return 5;
        else if(str_region.equals("울산")) return 6;
        else if(str_region.equals("세종")) return 7;
        else if(str_region.equals("경기")) return 8;
        else if(str_region.equals("강원")) return 9;
        else if(str_region.equals("경북")) return 10;
        else if(str_region.equals("경남")) return 11;
        else if(str_region.equals("충북")) return 12;
        else if(str_region.equals("충남")) return 13;
        else if(str_region.equals("전북")) return 14;
        else if(str_region.equals("전남")) return 15;
        else if(str_region.equals("제주")) return 16;
        else return -1; // 잘못된 지역값이 들어가 있는 경우
    }

}