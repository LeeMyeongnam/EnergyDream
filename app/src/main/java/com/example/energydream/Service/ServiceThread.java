package com.example.energydream.Service;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class ServiceThread extends Thread{

    Handler myhandler;
    boolean isRun = true;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    public ServiceThread(Handler handler) {

        this.myhandler = handler;

        ref.child("StandyPower").child("isExist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("발생2","발생2");

                if (dataSnapshot.getValue() != null
                        && dataSnapshot.getValue().toString().equals("true")) {  // 대기전력이 발생할 경우에만 알림 발생
                    Log.d("발생","발생");
                    myhandler.sendEmptyMessage(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        //반복적으로 수행할 작업을 한다.
        while(isRun){
            // myhandler.sendEmptyMessage(0);
            Log.d("발생3","발생3");

            try{
                Thread.sleep(10000); //10초씩 쉰다.
            }catch (Exception e) {}
        }
    }
}