package com.example.energydream;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;

public class EnrollBusinessActivity extends Fragment{
    //Business 생성자 변수
    boolean isBusiness;
    String businessName;
    int businessGoal;
    String businessIMG; //image 파일이름 저장
    String businessContents;
    String goalDate;


    Uri filepath; //파일 불러와서 주소 저장
    String filename;
    //business를 bus로 쓰게씀다
    EditText text_busName;
    Button btn_busImg;
    EditText text_busContents;
    EditText text_mileage;
    TextView text_goalDate;
    Button select_date;
    Button btn_enroll;
    ImageView img_business;

    Calendar calendar;
    private int mYear;
    private int mMonth;
    private int mDay;
    static final int DATE_DIALOG_ID = 0;
    public EnrollBusinessActivity(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_enroll_business, container, false);
        initView(view);
        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        String str_year = year + "";
                        String str_month = (month+1) + "";
                        if(str_month.length() == 1) str_month = "0" + str_month;
                        String str_day = date + "";
                        if(str_day.length() == 1) str_day = "0" + str_day;

                        goalDate = str_year + " 년" + str_month + " 월" + str_day + " 일";
                        text_goalDate.setText(goalDate);
                        Toast.makeText(getActivity(),goalDate, Toast.LENGTH_SHORT).show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.show();


            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == getActivity().RESULT_OK){
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                img_business.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void selectGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }
    private void uploadFile(){
        if(filepath!= null){
            filename = (int)(Math.random()*1000+1)+".png";
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("업로드중");
            progressDialog.show();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            NavActivity.sReference = storage.getReferenceFromUrl("gs://e-tmi-43dd5.appspot.com").child("businessIMG/"+filename);
            NavActivity.sReference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                }
            })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {

        }
    }

    private void initView(final View view){
        text_busName = view.findViewById(R.id.business_name);
        btn_busImg = view.findViewById(R.id.business_img);
        btn_busImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGallery();
            }
        });
        text_busContents = view.findViewById(R.id.business_contents);
        text_mileage = view.findViewById(R.id.goal_milage);
        text_goalDate = view.findViewById(R.id.goal_date);
        select_date = view.findViewById(R.id.business_date);
        img_business = view.findViewById(R.id.business_img_value);


        btn_enroll = view.findViewById(R.id.enroll);
        btn_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text_busName.getText().toString().length() == 0
                        || text_busContents.getText().toString().length() == 0
                        || text_mileage.getText().toString().length() == 0  || filepath==null
                        || text_goalDate.getText().toString().length() == 0){
                    Toast.makeText(getContext(), "정보를 모두 기입해주세요.", Toast.LENGTH_LONG).show();
                } else {
                    uploadFile();
                    enroll();
                    Toast.makeText(getContext(), "등록되었습니다.", Toast.LENGTH_LONG).show();
                    NavActivity.changeFragment(NavActivity.FRAGMENT_MAIN);
                }

            }
        });


    }

    private void enroll(){

        /* 소셜/일반기부 구분하는 switch 만들기 */
        isBusiness = true;
        businessIMG = filename;
        businessName = text_busName.getText().toString();
        businessGoal = Integer.parseInt(text_mileage.getText().toString());
        businessContents = text_busContents.getText().toString();
        goalDate = text_goalDate.getText().toString();


        Business newBusiness = new Business(isBusiness, businessName, NavActivity.login_comp_user.getCor_name(),
                businessGoal, businessIMG, businessContents, goalDate);

        NavActivity.login_comp_user.addBusiness(newBusiness);
        NavActivity.mReference
                .child("CompanyMembers")
                .child(NavActivity.login_comp_user.getCor_num())
                .setValue(NavActivity.login_comp_user);
    }

}