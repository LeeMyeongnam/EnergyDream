package com.example.energydream;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.CompanyMember;
import com.example.energydream.Model.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupCompanyActivity extends Fragment {


    FirebaseAuth mAuth;

    CompanyMember corMember;
    String cor_num;
    String pw;
    String name;

    EditText text_corNum;
    EditText text_pw;
    EditText text_pw_check;
    EditText text_name;
    Button btn_signup;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.activity_signup_company, container, false);

        text_corNum = (EditText)rootView.findViewById(R.id.join_company_id);
        text_pw = (EditText)rootView.findViewById(R.id.join_company_pw);
        text_pw_check = (EditText)rootView.findViewById(R.id.join_company_pw_check);
        text_name = (EditText)rootView.findViewById(R.id.join_company_name);
        btn_signup = (Button)rootView.findViewById(R.id.btn_join_company);

        mAuth = FirebaseAuth.getInstance();

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 비어있는 칸이 있다면 가입되지 않음
                if(text_corNum.getText().length() == 0  || text_pw.getText().length() == 0
                        || text_pw_check.getText().length() == 0  || text_name.getText().length() == 0){
                    Toast.makeText(getContext(), "정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(!text_pw.getText().toString().equals(text_pw_check.getText().toString())){
                    Toast.makeText(getContext(), "비밀번호가 동일하지 않습니다..", Toast.LENGTH_SHORT).show();
                }else {
                    signup();
                }
            }
        });


        return rootView;
    }

    private void signup(){
        cor_num = text_corNum.getText().toString();
        pw = text_pw.getText().toString();
        name = text_name.getText().toString();

        String cor_email = cor_num + "@company.com";    // 기업 이메일 : 사업자번호@company.com
        mAuth.createUserWithEmailAndPassword(cor_email, pw)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // 성공적으로 회원가입이 되었을 때
                            if (task.isSuccessful()) {
                                //mUser = mAuth.getCurrentUser();
                                corMember = new CompanyMember(cor_num, name, pw, null);
                                NavActivity.mReference.child("CompanyMembers").child(cor_num).setValue(corMember);
                                Toast.makeText(getContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();

                                NavActivity.changeFragment(NavActivity.FRAGMENT_LOGIN);

                            } else {  // 회원가입에 실패했을 경우
                                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

}
