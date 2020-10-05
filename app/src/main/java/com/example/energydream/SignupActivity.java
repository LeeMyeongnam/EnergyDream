package com.example.energydream;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.energydream.Model.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends Fragment implements AdapterView.OnItemSelectedListener{

    String[] city;
    Spinner spinner;
    String email;
    String pw;
    String pw_check;
    String name;
    String region;

    EditText text_email;
    EditText text_pw;
    EditText text_pw_check;
    EditText text_name;
    Button btn_signup;

    FirebaseAuth mAuth;

    public SignupActivity(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_signup, container, false);

        initView(view);

        mAuth = FirebaseAuth.getInstance();

        spinner.setPrompt("시도를 선택하세요");
        spinner.setOnItemSelectedListener(this);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, city);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        return view;
    }

    private void initView(View view){
        spinner = view.findViewById(R.id.join_city);
        text_email = view.findViewById(R.id.join_email);
        text_pw = view.findViewById(R.id.join_pw);
        text_pw_check = view.findViewById(R.id.join_pw_check);
        text_name = view.findViewById(R.id.join_name);
        btn_signup = view.findViewById(R.id.btn_join_inJoin);

        city = new String[]{"서울", "부산", "인천","대구", "광주", "대전", "울산", "세종", "경기",
                "강원", "경북", "경남", "충북", "충남", "전북", "전남", "제주"};
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void signUp(){
        email = text_email.getText().toString();
        pw = text_pw.getText().toString();
        pw_check = text_pw_check.getText().toString();
        name = text_name.getText().toString();
        region = spinner.getSelectedItem().toString();

        if(!pw.equals(pw_check)){
            Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
        }else {

            mAuth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // 성공적으로 회원가입이 되었을 때
                            if (task.isSuccessful()) {
                                //mUser = mAuth.getCurrentUser();
                                Member signupMember = new Member(email, pw, name, region);
                                NavActivity.mReference.child("Members").child(name).setValue(signupMember);
                                Toast.makeText(getContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();

                                NavActivity.changeFragment(NavActivity.FRAGMENT_LOGIN);

                            } else {  // 회원가입에 실패했을 경우
                                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

}