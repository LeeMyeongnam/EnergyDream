package com.example.energydream.Service;

import android.app.NativeActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.energydream.ArActionActivity;
import com.example.energydream.NavActivity;
import com.example.energydream.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MyService extends Service {

    ServiceThread thread;


    //Service와 Activity 통신 데이터 주고받을 때 사용 (없으면 null)
    @Override
    public IBinder onBind(Intent intent) {

        return  null;
    }

    //서비스 주기 onCreate() - onStartCommand()
    //프로그램이 종료되도 다시 살아난다. 살아있을 때 또 부르면 onStartCommand()로 바로 감
    @Override
    public void onCreate() {
        super.onCreate();
        //서비스에서 최초의 한번만 호출 됨
        Toast.makeText(MyService.this,"랄라라",Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //서비스가 호출 될때마다 시행
        myServiceHandler handler = new myServiceHandler();
        if(thread == null){
            thread = new ServiceThread(handler);
            thread.start();
        }

        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업

    public void onDestroy() {

        //서비스 종료
        //서비스 종료
        if(thread!=null){
            thread.stopForever();
            thread = null;
        }
    }


    class myServiceHandler extends Handler{

        @Override
        public void handleMessage(android.os.Message msg) {

            //토스트 띄우기
            // Toast.makeText(MyService.this, "뜸?", Toast.LENGTH_LONG).show();
            sendNotification();
        }

        // 푸시알림
        public void sendNotification(){

            String channelId = "channel";
            String channelName = "Channel Name";

            NotificationManager notifManager

                    = (NotificationManager) getSystemService  (Context.NOTIFICATION_SERVICE);


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);

                notifManager.createNotificationChannel(mChannel);

            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), channelId);

            Intent intent = new Intent(getApplicationContext(), NavActivity.class);
            intent.putExtra("StandyPowerMessage","GoAR");    //알림클릭시 NavActivity에 메시지 전송
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);


            builder.setContentTitle("대기전력 발생!") // required
                    .setContentText("지금 바로 대기전력을 차단해주세요.")  // required
                    .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                    .setAutoCancel(true) // 알림 터치시 반응 후 삭제

                    .setSound(RingtoneManager

                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                    .setSmallIcon(android.R.drawable.btn_star)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources()

                            , R.drawable.icon))
                    .setBadgeIconType(R.drawable.icon)

                    .setContentIntent(pendingIntent);


            notifManager.notify(0, builder.build());

            //sendNotification("아안녕");
        }



    }

}