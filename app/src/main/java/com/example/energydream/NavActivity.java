package com.example.energydream;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.example.energydream.Model.CompanyMember;
import com.example.energydream.Model.Member;
import com.example.energydream.Model.StandyPower;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Stack;

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    // fragment number
    public final static int FRAGMENT_MAIN = 1000;
    public final static int FRAGMENT_LOGIN = 1001;
    public final static int FRAGMENT_SIGNUP = 1002;
    public final static int FRAGMENT_SIGNUP_COMPANY = 1003;
    public final static int FRAGMENT_ADD_CONCENT = 1004;
    public final static int FRAGMENT_STATISTICS = 1005;
    public final static int FRAGMENT_DONATION_LIST = 1006;
    public final static int FRAGMENT_VENTURE_LIST = 1007;
    public final static int FRAGMENT_DONATION = 1008;
    public final static int FRAGMENT_MYPAGE = 1009;
    public final static int FRAGMENT_MYCOMPPAGE = 1010;
    public final static int FRAGMENT_DETAIL = 1011;
    public final static int FRAGMENT_APPROVE = 1012;
    public final static int FRAGMENT_APPROVE_DETAIL = 1013;


    // fragment
    private static MainActivity mainActivity;
    private static LoginActivity loginFragment;
    private static SignupActivity signupFragment;
    private static SignupCompanyActivity signupCompanyFragment;
    private static DonationActivity donationFragment;
    private static DonationListActivity donationListFragment;
    private static VentureListActivity ventureListFragment;
    private static DetailActivity detailFragment;
    private static AddConcentActivity addConcentFragment;
    private static StatisticsActivity statisticsFragment;
    private static MypageActivity mypageFragment;
    private static MyCompPageActivity myCompPageFragment;
    private static ApproveBusinessActivity approveBusinessFragment;
    private static ApproveDetailActivity approveDetailFragment;


    public static Member login_user;
    public static CompanyMember login_comp_user;
    public static ArrayList<Business> venture_list;
    public static ArrayList<Business> donation_list;
    public static ArrayList<Business> myCompBusi_list; // 현재 로그인한 사업자의 사업리스트
    public static ArrayList<Business> notApprove_list;
    public static ArrayList<CompanyMember> company_list;
    public static StandyPower standyPower;
    public static Stack<Fragment> stack_fragment;

    public static DatabaseReference mReference;
    public static StorageReference sReference;
    public static FirebaseUser mUser;       // 로그인 사용자 정보 저장 변수
    public static FragmentManager manager;
    public static FragmentTransaction transaction;


    static TextView nav_login;
    static TextView nav_logout;
    static TextView nav_email;
    static MenuItem nav_add_concent;        // 콘센트 연결 관리
    static MenuItem nav_donation_list;      // 에너지 기부하기
    static MenuItem nav_venture_list;       // 벤처기업 응원하기
    static MenuItem nav_history;            // 기부 내역
    static MenuItem nav_statics;            // 통계
    static MenuItem nav_add_business;       // 사업 추가하기
    static MenuItem nav_approve_business;   // 사업 승인하기
    static MenuItem nav_mypage;             // 마이페이지
    static MenuItem nav_add_donation;
    static ImageView nav_setting;

    NavigationView navigationView;
    DrawerLayout drawer;
    View headerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        mainActivity = new MainActivity();

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content_fragment_layout, mainActivity).commit();
        stack_fragment = new Stack<>();
        stack_fragment.push(mainActivity);

        mReference = FirebaseDatabase.getInstance().getReference();
        sReference = FirebaseStorage.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        loginFragment = new LoginActivity();
        signupFragment = new SignupActivity();
        signupCompanyFragment = new SignupCompanyActivity();
        donationFragment = new DonationActivity();
        donationListFragment = new DonationListActivity();
        ventureListFragment = new VentureListActivity();
        detailFragment = new DetailActivity();
        addConcentFragment = new AddConcentActivity();
        statisticsFragment = new StatisticsActivity();
        mypageFragment = new MypageActivity();
        myCompPageFragment = new MyCompPageActivity();
        approveBusinessFragment = new ApproveBusinessActivity();
        approveDetailFragment = new ApproveDetailActivity();

        venture_list = new ArrayList<>();
        donation_list = new ArrayList<>();
        myCompBusi_list = new ArrayList<>();
        notApprove_list = new ArrayList<>();
        company_list = new ArrayList<>();

        initView();
        getBusinessList();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Log.d("랄랄라ㅏㄹ","라라랄");
        /////은솔씨 추가//////
        onNewIntent(getIntent());
        ////////////////

        // 로그인 되지 않은 상태일 경우 대부분의 메뉴가 보이지 않도록 설정
        if(login_user == null){

        }

        // 로그인 / 로그아웃
        nav_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stack_fragment.push(mainActivity);
                changeFragment(FRAGMENT_LOGIN);
                drawer.closeDrawers();
            }
        });
        nav_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login_user != null || login_comp_user != null) {
                    logout();
                }else {  //testing (로그인시 객체설정 제대로 되지 않은 경우)
                    Toast.makeText(getApplicationContext(), "롸?", Toast.LENGTH_LONG).show();
                }
            }
        });
        nav_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login_user != null || login_comp_user != null){
                    if(login_user != null)
                        changeFragment(FRAGMENT_MYPAGE);
                    else
                        changeFragment(FRAGMENT_MYCOMPPAGE);

                    drawer.closeDrawers();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            changeFragment(FRAGMENT_LOGIN);
            drawer.closeDrawers();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        String title = getString(R.string.app_name);
        if (id == R.id.nav_camera) {
            fragment = new AddConcentActivity();
            title = "콘센트 연결 관리";
        } else if (id == R.id.nav_gallery) {
            fragment = new HistoryActivity();
            title = "기부 내역";
        } else if (id == R.id.nav_slideshow) {
            fragment = new StatisticsActivity();
            title ="통계";
        } else if (id == R.id.nav_share) {
            fragment = new AdminStatisticsActivity();
            title = "에너지 기부하기";
        } else if (id == R.id.nav_send) {
            fragment = new VentureListActivity();
            title = "벤처기업 응원하기";
        }else if(id == R.id.nav_mypage){
            if(login_user != null)
                fragment = new MypageActivity();
            else if(login_comp_user != null)
                fragment = new MyCompPageActivity();
            title = "마이페이지";
        }else if(id == R.id.nav_add_business){
            fragment = new EnrollBusinessActivity();
            title = "사업추가";
        }else if(id == R.id.nav_approve_business){
            fragment = new ApproveBusinessActivity();
            title = "사업승인";
        }else if(id == R.id.nav_enroll_donation){
            fragment = new EnrollDonationActivity();
            title = "에너지기부 등록";
        }

        if (fragment != null) {
            // 이동하기 전에 현재 프래그먼트 저장
            stack_fragment.push(mainActivity);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_fragment_layout, fragment);
            ft.commit();
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // 스택에 직전 프래그먼트가 남아있다면 그 프래그먼트로 이동
            if(!stack_fragment.isEmpty()){
                Fragment next_fragment = stack_fragment.pop();
                manager.beginTransaction().replace(R.id.content_fragment_layout, next_fragment).commit();
            }else {
                super.onBackPressed();
            }
        }
    }


    private void initView(){
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerview = navigationView.getHeaderView(0);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nav_login = (TextView)headerview.findViewById(R.id.nav_login);
        nav_logout = (TextView)headerview.findViewById(R.id.nav_logout);
        nav_email = (TextView)headerview.findViewById(R.id.nav_email);
        nav_setting = (ImageView)headerview.findViewById(R.id.image_setting);

        nav_add_concent = navigationView.getMenu().getItem(0);
        nav_donation_list = navigationView.getMenu().getItem(1);
        nav_venture_list = navigationView.getMenu().getItem(2);
        nav_history = navigationView.getMenu().getItem(3);
        nav_statics = navigationView.getMenu().getItem(4);
        nav_add_business = navigationView.getMenu().getItem(5);
        nav_approve_business = navigationView.getMenu().getItem(6);
        nav_mypage = navigationView.getMenu().getItem(7);
        nav_add_donation = navigationView.getMenu().getItem(8);



        nav_email.setVisibility(View.INVISIBLE);
        nav_add_concent.setVisible(false);
        nav_history.setVisible(false);
        nav_statics.setVisible(false);
        nav_add_business.setVisible(false);
        nav_approve_business.setVisible(true); //testing
        nav_add_donation.setVisible(true);  // testing
        //nav_mypage.setVisible(true);    //testing
        nav_setting.setVisibility(View.INVISIBLE);
    }


    public void getBusinessList(){
        mReference.child("CompanyMembers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                donation_list.clear();
                venture_list.clear();
                notApprove_list.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CompanyMember companyMember = snapshot.getValue(CompanyMember.class);
                    company_list.add(companyMember);
                    ArrayList<Business> tmp_list = companyMember.getBusinessList();
                    if(tmp_list != null){
                        for(int i=0; i<tmp_list.size(); i++){
                            // 벤처사업, 에너지기부사업 나눠서 리스트에 저장
                            if(!tmp_list.get(i).getIsVenture())
                                donation_list.add(tmp_list.get(i));

                            else if(tmp_list.get(i).getState()==1)
                                venture_list.add(tmp_list.get(i));
                            else
                                notApprove_list.add(tmp_list.get(i));
                        }
                    }
                }
                Toast.makeText(getApplicationContext(), venture_list.size() + "개 추가완료", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void updateUser(){
        // 개인사용자 로그인인 경우
        if(login_user != null)
            mReference.child("Members").child(login_user.getName()).setValue(login_user);
    }

    public static void updateUI(FirebaseUser user, boolean isCompany){

        // 로그인
        if(user != null){
            if(isCompany){
                String email[] = user.getEmail().split("@");
                // 1) 관리자 로그인
                if(email[0].equals("admin")){
                    nav_login.setText("관리자");
                    nav_approve_business.setVisible(true);      // 사업 승인하기
                    nav_mypage.setVisible(false);               // 사업자 마이페이지
                }
                else{ // 2) 기업 로그인
                    nav_login.setText(login_comp_user.getCor_name());    // 사업명
                    nav_add_business.setVisible(true);      // 사업추가하기
                    nav_mypage.setVisible(true);            // 사업자 마이페이지
                }
                nav_email.setText(email[0]);            // 사업자번호
                nav_history.setVisible(false);
            }else{
                // 개인 로그인
                nav_login.setText(login_user.getName());
                nav_email.setText(login_user.getEmail());
                nav_approve_business.setVisible(false);
                nav_add_concent.setVisible(true);
                nav_history.setVisible(true);
                nav_mypage.setVisible(true);
            }

            nav_setting.setVisibility(View.VISIBLE);
            nav_logout.setVisibility(View.VISIBLE);
            nav_email.setVisibility(View.VISIBLE);

        }else{
            nav_login.setText("로그인해주세요.");
            nav_email.setVisibility(View.INVISIBLE);
            nav_logout.setVisibility(View.INVISIBLE);
            nav_add_concent.setVisible(false);
            nav_history.setVisible(false);
            nav_statics.setVisible(false);
            nav_add_business.setVisible(false);
            nav_approve_business.setVisible(false);
            nav_mypage.setVisible(false);
            nav_setting.setVisibility(View.INVISIBLE);
        }
    }

    public void logout(){
        mUser = null;
        login_user = null;
        login_comp_user = null;
        myCompBusi_list = null;

        // 로그아웃 시 updateUI는 isCompnay가 의미 없음
        updateUI(null, false);
        changeFragment(FRAGMENT_MAIN);
        Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();

        drawer.closeDrawers();
    }

    public static void changeFragment(int index){
        switch(index){
            case FRAGMENT_MAIN:
                manager.beginTransaction().replace(R.id.content_fragment_layout, mainActivity).commit();
                break;
            case FRAGMENT_LOGIN:
                manager.beginTransaction().replace(R.id.content_fragment_layout, loginFragment).commit();
                break;
            case FRAGMENT_SIGNUP:
                manager.beginTransaction().replace(R.id.content_fragment_layout, signupFragment).commit();
                break;
            case FRAGMENT_SIGNUP_COMPANY :
                manager.beginTransaction().replace(R.id.content_fragment_layout, signupCompanyFragment).commit();
                break;
            case FRAGMENT_ADD_CONCENT:
                manager.beginTransaction().replace(R.id.content_fragment_layout, addConcentFragment).commit();
                break;
            case FRAGMENT_STATISTICS:
                manager.beginTransaction().replace(R.id.content_fragment_layout, statisticsFragment).commit();
                break;
            case FRAGMENT_DONATION_LIST:
                manager.beginTransaction().replace(R.id.content_fragment_layout, donationListFragment).commit();
                break;
            case FRAGMENT_VENTURE_LIST:
                manager.beginTransaction().replace(R.id.content_fragment_layout, ventureListFragment).commit();
                break;
            case FRAGMENT_DONATION:
                manager.beginTransaction().replace(R.id.content_fragment_layout, donationFragment).commit();
                break;
            case FRAGMENT_MYPAGE:
                manager.beginTransaction().replace(R.id.content_fragment_layout, mypageFragment).commit();
                break;
            case FRAGMENT_MYCOMPPAGE:
                manager.beginTransaction().replace(R.id.content_fragment_layout, myCompPageFragment).commit();
                break;
            case FRAGMENT_DETAIL:
                manager.beginTransaction().replace(R.id.content_fragment_layout, detailFragment).commit();
                break;
            case FRAGMENT_APPROVE:
                manager.beginTransaction().replace(R.id.content_fragment_layout, approveBusinessFragment).commit();
                break;
            case FRAGMENT_APPROVE_DETAIL:
                manager.beginTransaction().replace(R.id.content_fragment_layout, approveDetailFragment).commit();
                break;
        }
    }

    public static LoginActivity getLoginFragment(){
        return loginFragment;
    }
    public static DonationActivity getDonationFragment(){
        return donationFragment;
    }
    public static DetailActivity getDetailFragment(){
        return detailFragment;
    }
    public static ApproveDetailActivity getApproveDtailFragment(){
        return approveDetailFragment;
    }

    //은솔///
    //알림창을 누를때만 AR화면으로 이동
    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if(extras != null){

            if(extras.containsKey("StandyPowerMessage"))
            {
                String msg = extras.getString("StandyPowerMessage");

                Intent gointent = new Intent(NavActivity.this,ArActionActivity.class);
                startActivityForResult(gointent,3000);

            }

        }
    }



    /////은솔
    //대기전력이 없을 때만 없는메시지창
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("풍",Integer.toString(requestCode));
        Log.d("풍풍",Integer.toString(resultCode));

        if (resultCode == RESULT_OK){

            switch (requestCode) {
                case 3000:
                    StandyPowerNoneDialog standyPowerNoneDialog = new StandyPowerNoneDialog(this);
                    standyPowerNoneDialog.show();
                    break;
            }
        }
    }

    */
    //은솔//
    

}
