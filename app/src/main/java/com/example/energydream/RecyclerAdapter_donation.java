package com.example.energydream;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by 이명남 on 2018-11-06.
 */

public class RecyclerAdapter_donation extends RecyclerView.Adapter<RecyclerAdapter_donation.ViewHolder>{

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<Business> mList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mimageView;
        TextView titletext;
        TextView hosttext;
        ProgressBar progress;
        TextView percent;
        TextView fund;
        Button btn_donation;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mimageView = itemView.findViewById(R.id.image);
            titletext = itemView.findViewById(R.id.title);
            hosttext=itemView.findViewById(R.id.host);
            progress = itemView.findViewById(R.id.progress);
            percent = itemView.findViewById(R.id.percent);
            fund = itemView.findViewById(R.id.fund);
            btn_donation = itemView.findViewById(R.id.donation);
            context = itemView.getContext();
        }

        public void onClick(View v){
            Bundle args = new Bundle();
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                // 현재 프래그먼트 스택에 저장
                NavActivity.stack_fragment.push(NavActivity.manager.findFragmentById(R.id.content_fragment_layout));

                args.putString("index", position+"");
                args.putString("fragment", NavActivity.FRAGMENT_DONATION_LIST + "");

                NavActivity.getDetailFragment().setArguments(args);
                NavActivity.changeFragment(NavActivity.FRAGMENT_DETAIL);
            }
        }
    }

    public RecyclerAdapter_donation(ArrayList<Business> items){
        mList = items;
    }

    @Override
    public RecyclerAdapter_donation.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        StorageReference sReference = storage.getReference().child("businessIMG/").child(mList.get(position).getBusinessIMG());
        sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri.toString()).into(holder.mimageView);
            }
        });

        holder.titletext.setText(mList.get(position).getBusinessName());
        holder.hosttext.setText(mList.get(position).getCompanyName());
        holder.progress.setProgress((mList.get(position).getMileage()*100/mList.get(position).getBusinessGoal()));
        holder.percent.setText(mList.get(position).getMileage()*100/mList.get(position).getBusinessGoal()+ "%");
        holder.fund.setText(mList.get(position).getMileage()+"개");

        holder.btn_donation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String business_id = mList.get(position).getBusinessID();

                if(NavActivity.login_user == null){
                    Toast.makeText(context, "로그인 시 이용할 수 있습니다", Toast.LENGTH_LONG ).show();
                }else {
                    // 현재 프래그먼트 스택에 저장
                    NavActivity.stack_fragment.push(NavActivity.manager.findFragmentById(R.id.content_fragment_layout));

                    // Bundle에 사업id 저장 후 프래그먼트 전환
                    Bundle bundle = new Bundle();
                    bundle.putString("id", business_id);
                    bundle.putString("index", position + "");
                    bundle.putBoolean("isVenture", false);

                    NavActivity.getDonationFragment().setArguments(bundle);
                    NavActivity.changeFragment(NavActivity.FRAGMENT_DONATION);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
}