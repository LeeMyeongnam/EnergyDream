package com.example.energydream;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DetailActivity extends Fragment {

    Business detail_business;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    TextView detail_title;
    TextView detail_company;
    ImageView detail_img;
    TextView detail_mileage;
    TextView detail_percent;
    ProgressBar detail_progress;
    TextView detail_goalDate;
    TextView detail_goal;
    TextView detail_contents;
    Button detail_godonation;

    int position;
    int before_fragment_id; // DetailFragment로 넘어오기 전의 프래그먼트 id

    /* 기부리스트 상세 페이지 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_detail, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            position = Integer.parseInt(bundle.getString("index"));
            before_fragment_id = Integer.parseInt(bundle.getString("fragment"));

            if(before_fragment_id == NavActivity.FRAGMENT_DONATION_LIST) {
                if (NavActivity.donation_list.size() > 0)
                    detail_business = NavActivity.donation_list.get(position);
            }else{
                if (NavActivity.venture_list.size() > 0)
                    detail_business = NavActivity.venture_list.get(position);
            }
        }

        init(view);
        setDetail(detail_business);

        return view;
    }
    void init(final View view){
        detail_title = view.findViewById(R.id.detail_title);
        detail_company = view.findViewById(R.id.detail_company);
        detail_img = view.findViewById(R.id.detail_img);
        detail_mileage = view.findViewById(R.id.detail_mileage);
        detail_percent = view.findViewById(R.id.detail_percent);
        detail_progress=view.findViewById(R.id.detail_progress);
        detail_goalDate = view.findViewById(R.id.detail_goalDate);
        detail_goal = view.findViewById(R.id.detail_goal);
        detail_contents = view.findViewById(R.id.detail_contents);
        detail_godonation = view.findViewById(R.id.detail_godonation);
    }
    void setDetail(Business det_business){
        detail_title.setText(det_business.getBusinessName());
        detail_company.setText(det_business.getCompanyName());
        StorageReference sReference = storage.getReference().child("businessIMG/").child(det_business.getBusinessIMG());
        sReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri.toString()).into(detail_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                detail_img.setImageResource(R.drawable.ic_launcher_background);
            }
        });

        detail_mileage.setText(det_business.getMileage()+"");
        detail_percent.setText((det_business.getMileage()*100)/(det_business.getBusinessGoal()) + " %");
        detail_progress.setProgress((det_business.getMileage()*100/det_business.getBusinessGoal()));
        detail_goalDate.setText(det_business.getGoalDate());
        detail_goal.setText(det_business.getBusinessGoal()+"");
        detail_contents.setText(det_business.getBusinessContents());

        detail_godonation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String business_id = detail_business.getBusinessID();
                if(NavActivity.login_user == null){
                    /*Dialog로 띄우주고, 확인버튼 누를 시 로그인페이지로 이동*/
                    Toast.makeText(getContext(), "로그인 시 이용할 수 있습니다", Toast.LENGTH_LONG ).show();
                }else {
                    // Bundle에 사업id 저장 후 프래그먼트 전환
                    Bundle bundle = new Bundle();
                    bundle.putString("id", business_id);
                    bundle.putString("index", position + "");   // testing

                    NavActivity.getDonationFragment().setArguments(bundle);
                    NavActivity.changeFragment(NavActivity.FRAGMENT_DONATION);
                }
            }
        });
    }
}