package com.example.energydream;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.example.energydream.Model.CompanyMember;
import com.example.energydream.Model.Member;
import com.example.energydream.Service.MyService;
import com.example.energydream.Service.ServiceConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends Fragment {

    FirebaseAuth mAuth;
    String input_email, input_pw;
    boolean isCompany;

    EditText emailText;
    EditText pwText;
    Button btn_user;
    Button btn_companyUser;
    Button btn_login;
    TextView text_signup;
    TextView text_signup_company;

    ServiceConfig serviceConfig;    // 서비스 활성화 확인


    public LoginActivity(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_login, container, false);

        emailText = (EditText)rootView.findViewById(R.id.insert_id);
        pwText = (EditText)rootView.findViewById(R.id.insert_passwd);
        btn_login = (Button)rootView.findViewById(R.id.sign_in);
        text_signup = (TextView)rootView.findViewById(R.id.sign_up);
        text_signup_company = (TextView)rootView.findViewById(R.id.sign_up_company);
        btn_user = (Button)rootView.findViewById(R.id.btn_user);
        btn_companyUser = (Button)rootView.findViewById(R.id.btn_company_user);

        serviceConfig = new ServiceConfig(getActivity());

        mAuth = FirebaseAuth.getInstance();
        isCompany = false;


        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCompany){
                    isCompany = false;
                    emailText.setHint("이메일");
                    emailText.setText("");
                    pwText.setText("");
                    // 회원가입 텍스트 변경
                    text_signup.setVisibility(View.VISIBLE);
                    text_signup_company.setVisibility(View.INVISIBLE);
                    // 개인회원/기업회원 버튼 설정
                    btn_user.setTextColor(Color.WHITE);
                    btn_user.setBackgroundResource(R.drawable.design_main_background);
                    btn_companyUser.setTextColor(Color.BLACK);
                    btn_companyUser.setBackgroundResource(R.drawable.design_main_border);
                }
            }
        });
        btn_companyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isCompany){
                    isCompany = true;
                    emailText.setHint("사업자번호");
                    emailText.setText("");
                    pwText.setText("");
                    text_signup.setVisibility(View.INVISIBLE);
                    text_signup_company.setVisibility(View.VISIBLE);
                    btn_companyUser.setTextColor(Color.WHITE);
                    btn_companyUser.setBackgroundResource(R.drawable.design_main_background);
                    btn_user.setTextColor(Color.BLACK);
                    btn_user.setBackgroundResource(R.drawable.design_main_border);
                }
            }
        });


        text_signup.setOnClickListener(new View.OnClickListener() {
            Fragment fragment = new SignupActivity();
            @Override
            public void onClick(View v) {
                // 현재 프래그먼트(LoginActivity) 프래그먼트 스택에 저장 후 프래그먼트 전환
                Fragment cur_fragment = NavActivity.manager.findFragmentById(R.id.content_fragment_layout);
                NavActivity.stack_fragment.push(cur_fragment);

                NavActivity.changeFragment(NavActivity.FRAGMENT_SIGNUP);
            }
        });
        text_signup_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 프래그먼트(LoginActivity) 프래그먼트 스택에 저장 후 프래그먼트 전환
                Fragment cur_fragment = NavActivity.manager.findFragmentById(R.id.content_fragment_layout);
                NavActivity.stack_fragment.push(cur_fragment);

                NavActivity.changeFragment(NavActivity.FRAGMENT_SIGNUP_COMPANY);
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCompany){
                    login_company();    // 기업 로그인
                }else {
                    login_personal();   // 개인 로그인
                }
            }
        });


        return rootView;
    }

    private void login_company(){
        // 기업 로그인 이메일 : 사업자번호@company.com
        input_email = emailText.getText().toString() + "@company.com";
        input_pw = pwText.getText().toString();

        // 이메일, 비밀번호 중 빈칸이 있는 경우
        if(input_email.length() <= 0 || input_pw.length() <= 0) {
            Toast.makeText(getContext(), "로그인 정보를 입력해주세요", Toast.LENGTH_LONG).show();
        }// 제대로 입력된 경우
        else{
            mAuth.signInWithEmailAndPassword(input_email, input_pw)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // email-pw 가 일치할 경우 로그인 성공
                                NavActivity.mUser = mAuth.getCurrentUser();

                                // 로그인 유저 객체 생성
                                NavActivity.mReference.child("CompanyMembers").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            CompanyMember compMember = snapshot.getValue(CompanyMember.class);


                                            String company_email = compMember.getCor_num() + "@company.com";
                                            if (company_email.equals(NavActivity.mUser.getEmail())) {
                                                NavActivity.login_comp_user = compMember;
                                                // 사업리스트가 없다면 ArrayList 초기화
                                                if(NavActivity.login_comp_user.getBusinessList() == null){
                                                    NavActivity.login_comp_user.setBusinessList(new ArrayList<Business>());
                                                }
                                                NavActivity.updateUI(NavActivity.mUser, isCompany);

                                                addCompBusiList(); // 해당 사업자의 사업리스트 저장

                                                input_email = input_pw = "";
                                                pwText.setText("");
                                                emailText.setText("");

                                                //Toast.makeText(getContext(), "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                                                NavActivity.changeFragment(NavActivity.FRAGMENT_MAIN);
                                            } else {
                                                // Firebase에 데이터에 문제가 생긴 경우

                                                Log.d("[TAG]", "Database Problem occur in LoginActivity");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                            } else {
                                Toast.makeText(getContext(), "로그인 정보가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void login_personal(){

        input_email = emailText.getText().toString();
        input_pw = pwText.getText().toString();

        // 이메일, 비밀번호 중 빈칸이 있는 경우
        if(input_email.length() == 0 || input_pw.length() == 0) {
            Toast.makeText(getContext(), "로그인 정보를 입력해주세요", Toast.LENGTH_LONG).show();
        }// 제대로 입력된 경우
        else{
            mAuth.signInWithEmailAndPassword(input_email, input_pw)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // email-pw 가 일치할 경우 로그인 성공
                                NavActivity.mUser = mAuth.getCurrentUser();

                                // 로그인 유저 객체 생성 (수정)
                                NavActivity.mReference.child("Members").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            Member member = snapshot.getValue(Member.class);

                                            if (member.getEmail().equals( NavActivity.mUser.getEmail()) ) {
                                                NavActivity.login_user = member;
                                                NavActivity.updateUI( NavActivity.mUser, isCompany);

                                                // 현재 띄워진 프래그먼트가 LoginActivity인 경우
                                                Fragment cur_fragment = NavActivity.manager.findFragmentById(R.id.content_fragment_layout);
                                                if(cur_fragment == NavActivity.getLoginFragment()) {

                                                    //만약 서비스가 돌아가고 있지않은 상태라면
                                                    if (!serviceConfig.getServiceState()) {
                                                        //Toast.makeText(getContext(), NavActivity.login_user.getEmail() + "호오오잇 서비스돌려요~.", Toast.LENGTH_LONG).show();
                                                        getActivity().startService(new Intent(getActivity(), MyService.class));
                                                    }

                                                    input_email = input_pw = "";
                                                    pwText.setText("");
                                                    emailText.setText("");

                                                    Toast.makeText(getContext(), NavActivity.login_user.getEmail() + "로그인 되었습니다.", Toast.LENGTH_LONG).show();
                                                    NavActivity.changeFragment(NavActivity.FRAGMENT_MAIN);
                                                }else{
                                                    // 로그인중이 아닌데 중간데 데이터가 변경되어 onDataChange() 메소드가 호출된 경우
                                                    // -> login_user 객체만 변경해주고 다른 작업은 하지 않는다.
                                                }
                                            } else {
                                                // Firebase에 데이터에 문제가 생긴 경우
                                                Log.d("[TAG]", "Database Problem occur in LoginActivity");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                Toast.makeText(getContext(), "로그인 정보가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }


    // 해당 사업자의 사업리스트 저장
    private void addCompBusiList(){

        NavActivity.myCompBusi_list = new ArrayList<>();
        String com_user_id = NavActivity.login_comp_user.getCor_num();

        NavActivity.mReference.child("CompanyMembers").child(com_user_id).child("businessList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // 데이터 변경 시,
                        // 변경된 데이터를 새롭게 리스트에 저장
                        if(NavActivity.myCompBusi_list != null) {
                            NavActivity.myCompBusi_list.clear();
                            ArrayList<Business> cur_busiList = (ArrayList<Business>) dataSnapshot.getValue();
                            if(cur_busiList != null) {
                                for (int i = 0; i < cur_busiList.size(); i++) {
                                    Business cur_busi = dataSnapshot.child(i + "").getValue(Business.class);
                                    NavActivity.myCompBusi_list.add(cur_busi);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
}
