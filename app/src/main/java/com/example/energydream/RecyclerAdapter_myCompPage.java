package com.example.energydream;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/* 사업자 마이페이지 */
public class RecyclerAdapter_myCompPage extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FirebaseStorage storage = FirebaseStorage.getInstance();

    // recyclerView 의 각 아이템을 표시하는 클래스

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView busi_name;     // 사업명
        TextView cur_mileage;   // 현재 모금 마일리지
        TextView goal_mileage;  // 목표 마일리지
        TextView termi_day;     // 모금 마감 날짜
        ImageView imageView;    // 사업 이미지

        public MyViewHolder(View itemView) {
            super(itemView);

            /* 레이아웃 객체화 */
            itemView.setOnClickListener(this);
            context = itemView.getContext();
            busi_name = (TextView)itemView.findViewById(R.id.text_busi_name);     // 사업명
            cur_mileage = (TextView)itemView.findViewById(R.id.text_cur_mileage);
            goal_mileage = (TextView)itemView.findViewById(R.id.text_goal_mileage);
            termi_day = (TextView)itemView.findViewById(R.id.end_date_value);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }

        @Override
        public void onClick(View view) {
            /* 클릭이벤트 */
            /* 클릭시 Detail 페이지로 이동 */
        }

    }


    private ArrayList<Business> mList;
    Context context;

    // 각 아이템에 표시할 데이터를 가지는 ArrayList를 생성자에서 받아서 저장
    public RecyclerAdapter_myCompPage(boolean isVenture) {
        // 선택된 종류의 사업(venture/donation)만 리스트에 저장
        mList = new ArrayList<>();
        for(int i=0; i<NavActivity.myCompBusi_list.size(); i++)
           mList.add(NavActivity.myCompBusi_list.get(i));

    }

    @NonNull
    @Override   // RecyclerView의 각 행을 표시하는데 사용되는 레이아웃 객체화 (Holder생성)
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_comp_page, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);   // 내부 클래스(ViewHolder 객체화)

        return viewHolder;
    }


    @Override // 각 아이템이 RecyclerView에서 보여질 때의 값 설정
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder myHolder = (MyViewHolder)holder;

        myHolder.busi_name.setText(NavActivity.myCompBusi_list.get(position).getBusinessName());
        myHolder.termi_day.setText(NavActivity.myCompBusi_list.get(position).getGoalDate());
        myHolder.cur_mileage.setText(NavActivity.myCompBusi_list.get(position).getMileage() + "");
        myHolder.goal_mileage.setText(NavActivity.myCompBusi_list.get(position).getBusinessGoal() + "");

        StorageReference sReference = storage.getReference().child("businessIMG/")
                .child(NavActivity.myCompBusi_list.get(position).getBusinessIMG());
        sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri.toString()).into(myHolder.imageView);
            }
        });

    }



    @Override
    public int getItemCount() {
        return mList.size();
    }



}
