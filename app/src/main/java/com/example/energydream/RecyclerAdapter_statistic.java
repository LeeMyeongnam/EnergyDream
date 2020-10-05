package com.example.energydream;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.energydream.Model.Business;
import com.example.energydream.Model.CompanyMember;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by 이명남 on 2018-11-12.
 */

public class RecyclerAdapter_statistic extends RecyclerView.Adapter<RecyclerAdapter_statistic.ViewHolder>{

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private ArrayList<CompanyMember> mList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image_img;
        TextView text_name;
        TextView text_id;
        TextView text_count;
        TextView text_watt;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            image_img  = itemView.findViewById(R.id.bus_img);
            text_name  = itemView.findViewById(R.id.bus_name);
            text_id    = itemView.findViewById(R.id.bus_id);
            text_count = itemView.findViewById(R.id.bus_count);
            text_watt  = itemView.findViewById(R.id.bus_watt);
        }

    }

    public RecyclerAdapter_statistic(ArrayList<CompanyMember> items){
        mList = items;
    }

    @Override
    public RecyclerAdapter_statistic.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_admin_statistics_page, parent, false);
        RecyclerAdapter_statistic.ViewHolder vh = new RecyclerAdapter_statistic.ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final RecyclerAdapter_statistic.ViewHolder holder, final int position) {
        Log.v("추가 완료료1", "vv");
        StorageReference sReference = storage.getReference().child("userIMG/").child("user (1).png");
        sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri.toString()).into(holder.image_img);
            }
        });
        Log.v("추가 완료료2", "vv");
        holder.text_name.setText(mList.get(position).getCor_name());
        Log.v("추가 완료료3", "vv");
        holder.text_id.setText(mList.get(position).getCor_num());
        Log.v("추가 완료료4", "vv");
        if(mList.get(position).getBusinessList() == null) holder.text_count.setText(0+"");
        else holder.text_count.setText(mList.get(position).getBusinessList().size()+"");
        Log.v("추가 완료료5", "vv");
        holder.text_watt.setText(getMileageCount(mList.get(position))+"");
        Log.v("추가 완료료6", "vv");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public int getMileageCount(CompanyMember cm){
        int totalMileage=0;
        ArrayList<Business> cmBusiness = cm.getBusinessList();
        if(cmBusiness!=null)
             for(int i=0; i<cmBusiness.size(); i++){
                totalMileage+=cmBusiness.get(i).getMileage();
             }
        return  totalMileage;
    }

}