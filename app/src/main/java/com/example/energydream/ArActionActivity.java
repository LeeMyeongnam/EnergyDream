package com.example.energydream;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.energydream.ARUtils.Coin;
import com.example.energydream.ARUtils.Demoutils;
import com.example.energydream.Model.StandyPower;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class ArActionActivity extends AppCompatActivity implements Scene.OnUpdateListener{
    private static final String TAG = ArActionActivity.class.getSimpleName();

    private Snackbar loadingMessageSnackbar = null;
    private boolean installRequested;
    private boolean makeCheck = false;

    private ArFragment arFragment;
    private ArSceneView arSceneView;

    private Button Backbtn;
    private Coin coinNode;

    private boolean pauseCheck = false;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Demoutils.checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        //초기에 들어갈 때 대기전력이 존재하지 않으면 아예 화면 꺼버림
        NavActivity.mReference.child("StandyPower").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    StandyPower standyPower = dataSnapshot.getValue(StandyPower.class);

                    // DB에 존재하지 않으면 NavActivity로 돌아간다.
                    if(standyPower == null){
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK,resultIntent);
                        finish();
                        return;
                    }

                    if(!standyPower.isExist()){
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK,resultIntent);;
                        finish();
                        return;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        setContentView(R.layout.activity_ar);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        arSceneView = arFragment.getArSceneView();
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);


        Backbtn = (Button)findViewById(R.id.btn_arback);  //뒤로가기 버튼 등록

        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // When yu build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        /* ModelRenderable.builder()
        .setSource(this, R.raw.andy)
        .build()
        .thenAccept(renderable -> andyRenderable = renderable)
        .exceptionally(
            throwable -> {
              Toast toast =
                  Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
              toast.setGravity(Gravity.CENTER, 0, 0);
              toast.show();
              return null;
            });*/


        arSceneView.getScene().addOnUpdateListener(this);

    }

    private void addObject(Frame frame) {

        Point point = getScreenCenter();

        if(frame == null)
            Log.d("프레임 없다","결과");

        if (frame != null) {

            List<HitResult> hits = frame.hitTest((float) point.x, (float) point.y);
            Log.d(Integer.toString(hits.size()),"   결과");
            for (int i = 0; i < hits.size(); i++) {
                Trackable trackable = hits.get(i).getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hits.get(i).getHitPose())) {
                    placeObject(hits.get(i).createAnchor());
                }
            }
        }
    }

    //객체를 만든다.
    void placeObject(Anchor anchor){


        Log.d("만들어쪄","만들어쨔");
        // Create the Anchor.
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        arFragment.getArSceneView().getScene().addChild(anchorNode);

        //탐지된 곳에 Anchor 만든 후 coinNode 합성
        Node node = CreateCoin();
        node.setParent(anchorNode);

        //coin은 하나만 만들겠다.
        makeCheck = true;
    }


    private Node CreateCoin(){
        Node base = new Node();
        Coin coin = new Coin(arFragment,this);
        coinNode = coin;
        coin.setParent(base);
        return base;
    }





    @Override
    public void onUpdate(FrameTime frameTime) {

        Frame arframe = arFragment.getArSceneView().getArFrame();

        if (arframe.getCamera().getTrackingState() != TrackingState.TRACKING) {

            return;
        }

        for (Plane plane : arframe.getUpdatedTrackables(Plane.class)) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {
                Log.e("바닥걸림222","ㅇㅇ");
                hideLoadingMessage();

                if(!makeCheck)
                    addObject(arframe);

                //바닥 Tracking 한번만 실시 ---이거 하면 오류 ----
                // if(makeCheck) {
                //     Config config = arSceneView.getSession().getConfig();
                //     config.setPlaneFindingMode(Config.PlaneFindingMode.DISABLED);
                //     arSceneView.getSession().configure(config);
                // }


            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ARonResume","크크크");
        if(arSceneView == null)
            return;

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = Demoutils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = Demoutils.hasCameraPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                    Log.d("세션설정완료","ㅇ");
                }
            } catch (UnavailableException e) {
                Demoutils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            Demoutils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }

        if(coinNode != null)
            coinNode.resumeEvent();


        if(pauseCheck){

            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED,resultIntent);
            finish();
            return;

        }

    }


    @Override
    public void onPause() {
        super.onPause();

        //홈키 누르면 Pause 상태로 빠짐
        Log.d("지금은","Pause");

        if (arSceneView != null) {
            arSceneView.pause();
        }

        if(coinNode != null)
            coinNode.pauseEvent();


        pauseCheck = true;

    }

    @Override
    public void onDestroy() {

        Log.d("지금은","Destory");
        super.onDestroy();

    }


    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ArActionActivity.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }

    private Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }
}
