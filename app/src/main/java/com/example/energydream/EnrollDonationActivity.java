package com.example.energydream;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class EnrollDonationActivity extends Fragment {

    //Donation 생성자 변수
    boolean isDonation;
    String DonationName;
    int DonationGoal;
    String DonationIMG; //image 파일이름 저장
    String DonationContents;
    String Donation_goalDate;


    Uri filepath; //파일 불러와서 주소 저장
    String filename;
    //Donation를 bus로 쓰게씀다
    EditText Donation_text_busName;
    Button Donation_btn_busImg;
    EditText Donation_text_busContents;
    EditText Donation_text_mileage;
    TextView Donation_text_goalDate;
    Button Donation_select_date;
    Button Donation_btn_enroll;
    ImageView Donation_image;

    Calendar Donation_calendar;
    private int Donation_mYear;
    private int Donation_mMonth;
    private int Donation_mDay;
    static final int Donation_DATE_DIALOG_ID = 0;
    public EnrollDonationActivity(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup Donation_rootView = (ViewGroup)inflater.inflate(R.layout.activity_enroll_donation, container, false);
        Donation_initView(Donation_rootView);

        Donation_calendar = Calendar.getInstance();
        Donation_mYear = Donation_calendar.get(Calendar.YEAR);
        Donation_mMonth = Donation_calendar.get(Calendar.MONTH);
        Donation_mDay = Donation_calendar.get(Calendar.DAY_OF_MONTH);

        Donation_select_date.setOnClickListener(new View.OnClickListener() {
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

                        Donation_goalDate = str_year + " 년" + str_month + " 월" + str_day + " 일";
                        Donation_text_goalDate.setText(Donation_goalDate);
                        Toast.makeText(getActivity(),Donation_goalDate, Toast.LENGTH_SHORT).show();
                    }
                }, Donation_calendar.get(Calendar.YEAR), Donation_calendar.get(Calendar.MONTH), Donation_calendar.get(Calendar.DATE));
                dialog.show();


            }
        });

        return Donation_rootView;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == getActivity().RESULT_OK){
            filepath = data.getData();
            try {
                Bitmap Donation_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
                Donation_image.setImageBitmap(Donation_bitmap);
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

    private void Donation_initView(final View view){
        Donation_text_busName = view.findViewById(R.id.enroll_donation_name);
        Donation_btn_busImg = view.findViewById(R.id.enroll_donation_img);
        Donation_btn_busImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGallery();
            }
        });
        Donation_text_busContents = view.findViewById(R.id.enroll_donation_contents);
        Donation_text_mileage = view.findViewById(R.id.enroll_donation_goal_milage);
        Donation_text_goalDate = view.findViewById(R.id.enroll_donation_goal_date);
        Donation_select_date = view.findViewById(R.id.enroll_donation_date);
        Donation_image = view.findViewById(R.id.enroll_donation_img_value);


                Donation_btn_enroll = view.findViewById(R.id.enroll_donation);
        Donation_btn_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Donation_text_busName.getText().toString().length() == 0
                        || Donation_text_busContents.getText().toString().length() == 0
                        || Donation_text_mileage.getText().toString().length() == 0  || filepath==null
                        || Donation_text_goalDate.getText().toString().length() == 0){
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
        isDonation = false;
        DonationIMG = filename;
        DonationName = Donation_text_busName.getText().toString();
        DonationGoal = Integer.parseInt(Donation_text_mileage.getText().toString());
        DonationContents = Donation_text_busContents.getText().toString();
        Donation_goalDate = Donation_text_goalDate.getText().toString();


        Business newBusiness = new Business(isDonation, DonationName, NavActivity.login_comp_user.getCor_name(),
                DonationGoal, DonationIMG, DonationContents, Donation_goalDate);

        NavActivity.login_comp_user.addBusiness(newBusiness);
        NavActivity.mReference
                .child("CompanyMembers")
                .child(NavActivity.login_comp_user.getCor_num())
                .setValue(NavActivity.login_comp_user);
    }

}
