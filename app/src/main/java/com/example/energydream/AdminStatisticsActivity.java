package com.example.energydream;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.energydream.Model.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AdminStatisticsActivity extends Fragment {
    int totalMileage=0;
    int presentMileage=0;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    TextView avaliable;
    TextView total;
    public AdminStatisticsActivity() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.activity_admin_statistics, container, false);
        Context context = view.getContext();


        init(view);
        availableDonation();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.admin_list_view);
        mRecyclerView.setHasFixedSize(true);    //옵션

        mAdapter = new RecyclerAdapter_statistic(NavActivity.company_list); //스트링 배열 데이터 인자로
        mRecyclerView.setAdapter(mAdapter);

        //Linear layout manager 사용
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.notifyDataSetChanged();



        return view;
    }
    public void init(View view){
        avaliable = view.findViewById(R.id.availableMileage);
        total = view.findViewById(R.id.totalMielage);

    }
    private void availableDonation(){

        NavActivity.mReference.child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 기업의 사업자 아이디(business name) 탐색
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Member member = snapshot.getValue(Member.class);
                    Log.v("확인해주세영", "member name" + member.getName());
                    Log.v("확인해주세영", "member mileage" + member.getMileage());
                    totalMileage+= member.getTotal_mileage();
                    presentMileage+=member.getMileage();
                    Log.v("확인해주세영", "total mileage = "+totalMileage +"available mileage = "+ presentMileage);
                }
                total.setText(totalMileage+"");
                avaliable.setText(presentMileage+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
