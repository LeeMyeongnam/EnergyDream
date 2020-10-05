package com.example.energydream.ARUtils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;
import com.example.energydream.CustomDialog.StandyPowerBlock;
import com.example.energydream.Model.StandyPower;
import com.example.energydream.NavActivity;
import com.example.energydream.R;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Coin extends Node implements Node.OnTapListener, MediaPlayer.OnCompletionListener {


    private final Context context;
    private Node coinNode;
    private ArFragment arFragment;
    private ModelRenderable coinRenderable;
    private MediaPlayer mediaPlayer; //동영상재생
    private MediaPlayer soundPlayer; //음악재생
    private int soundPlayPosition = 0; //중지될 위치
    private ExternalTexture texture;


    // The color to filter out of the video.
    private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);

    // Controls the height of the video in world space.
    private static final float VIDEO_HEIGHT_METERS = 0.4f;

    private boolean coinCheck = true;

    public Coin (ArFragment fragment, Context context){
        this.arFragment = fragment;
        this.context = context;
    }

    //노드 초기화 (내가 핸드폰 놓고 다시 키면 한번 더 호출 됨)
    @Override
    public void onActivate() {

        if(getScene() == null){
            throw  new IllegalStateException("Scene is null!");

        }

        if(mediaPlayer == null){

            createMediaPlayer(coinCheck);
        }

        if(coinRenderable == null){

            CreateModelRenderable();
        }

        if(coinNode == null){

            createCoinNode();
        }


        Log.d("노드초기화","노드초기화");
    }

    //노드 클릭이벤트
    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        Log.d("눌러쪄","눌러쪙ㅇㅇㅇ");

        //누르면 노드가 변해야 해서 미디어 플레이어에 다른 mp4파일 삽입할 꺼다.

        if(coinCheck) {

            coinCheck = false;


            if (coinNode != null) {
                this.removeChild(coinNode);
                coinNode = null;

            }

            if (coinRenderable != null) {
                coinRenderable = null;
            }

            //새로운 효과 넣어줄 거라서 기존의 노드와 효과들 모두 없앰
            //새로운 효과 생성 (coinCheck = false) 이기 때문에 코인 클릭시의 효과가 들어 감.

            createMediaPlayer(coinCheck);


            if (coinRenderable == null) {

                CreateModelRenderable();
            }

            if (coinNode == null) {

                createCoinNode();
            }


            //파이어베이스 값 셋팅
            //초기에 들어갈 때 대기전력이 존재하지 않으면 아예 화면 꺼버림
            NavActivity.mReference.child("StandyPower").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    StandyPower standyPower = dataSnapshot.getValue(StandyPower.class);


                    if(standyPower!=null) {


                        if(NavActivity.standyPower!=null){
                            NavActivity.standyPower.setStart_time();
                            NavActivity.standyPower.setisCalc(true);
                        }

                        standyPower.setStart_time();
                        standyPower.setisCalc(true);

                        //결과 디비에 반영
                        NavActivity.mReference.child("StandyPower").setValue(standyPower);
                        NavActivity.mReference.child("operation").setValue(2);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

    }

    //각각의 미디어들이 끝날 때 나타남. (동전이벤트인지 아닌지 구분해줘야 함)
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        //끝난게 동전을 클릭했을 때 이벤트일 때 (노드를 모두 삭제해 줘야 되용)
        if(!coinCheck){

            if(this.mediaPlayer != null){
                this.mediaPlayer.stop();
                this.mediaPlayer.release();
                this.mediaPlayer = null;
            }

            if(coinNode != null){
                this.removeChild(coinNode);
                coinNode = null;

            }

            if(coinRenderable != null){
                coinRenderable = null;
            }

            StandyPowerBlock standyPowerBlock = new StandyPowerBlock(context);
            standyPowerBlock.show();
        }

        //동전일 때는 동영상이 무한루프로 재생되는데 안건들여야 겠쥬?
    }

    private void CreateModelRenderable(){

        ModelRenderable.builder()
                .setSource(context, R.raw.chroma_key_video)
                .build()
                .thenAccept(
                        renderable -> {
                            coinRenderable = renderable;
                            renderable.getMaterial().setExternalTexture("videoTexture", texture);
                            renderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
                        })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(context, "Unable to load video renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

    }


    //coinCheck == True 면 코인 false면 코인 클릭 이벤트를 넣는다.
    private void createMediaPlayer(boolean coinCheck){

        if(coinCheck) {
            // Create an ExternalTexture for displaying the contents of the video.
            texture = new ExternalTexture();
            // Create an Android MediaPlayer to capture the video on the external texture's surface.

            mediaPlayer = MediaPlayer.create(context, R.raw.coinn);
            mediaPlayer.setSurface(texture.getSurface());
            mediaPlayer.setLooping(true);

            soundPlayer = MediaPlayer.create(context, R.raw.coinsound);
            soundPlayer.setLooping(true);


        }else{
            // Create an ExternalTexture for displaying the contents of the video.
            //texture = new ExternalTexture();
            // Create an Android MediaPlayer to capture the video on the external texture's surface.
            mediaPlayer.stop();
            mediaPlayer.release();
            soundPlayer.stop();
            soundPlayer = null;
            mediaPlayer = MediaPlayer.create(context, R.raw.heart);
            mediaPlayer.setSurface(texture.getSurface());
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(this);
        }

    }

    private void createCoinNode(){

        float videoWidth = mediaPlayer.getVideoWidth();
        float videoHeight = mediaPlayer.getVideoHeight();

        coinNode = new Node();
        coinNode.setLocalScale(
                new Vector3(
                        VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), VIDEO_HEIGHT_METERS, 1.0f));

        coinNode.setParent(this);
        coinNode.setRenderable(coinRenderable);
        coinNode.setOnTapListener(this);

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();

            if(coinCheck)
                soundPlayer.start();
            // Wait to set the renderable until the first frame of the  video becomes available.
            // This prevents the renderable from briefly appearing as a black quad before the video
            // plays.
            texture
                    .getSurfaceTexture()
                    .setOnFrameAvailableListener(
                            (SurfaceTexture surfaceTexture) -> {
                                coinNode.setRenderable(coinRenderable);
                                texture.getSurfaceTexture().setOnFrameAvailableListener(null);
                            });
        } else {
            coinNode.setRenderable(coinRenderable);
        }

        Log.d("노드초기화","코인 만든당.");
    }

    public void pauseEvent(){

        if(soundPlayer != null) {
            soundPlayPosition = soundPlayer.getCurrentPosition();
            soundPlayer.pause();
        }


    }

    public void resumeEvent(){

        if(soundPlayer != null && !soundPlayer.isPlaying()) {
            soundPlayer.start();
            soundPlayer.seekTo(soundPlayPosition);
        }
    }

}