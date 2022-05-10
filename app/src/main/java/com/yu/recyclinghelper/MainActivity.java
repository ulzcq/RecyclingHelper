package com.yu.recyclinghelper;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.kakao.sdk.common.KakaoSdk;
import com.yu.recyclinghelper.Adapter.FragmentCallback;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback {

    Home_fragment homefragment;
    Challenge_fragment challengefragment;
    Campaign_fragment campaignfragment;
    DrawerLayout drawer;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KakaoSdk.init(this, "key");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        homefragment = new Home_fragment();
        challengefragment = new Challenge_fragment();
        campaignfragment = new Campaign_fragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container, homefragment).commit();
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }

        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            super.onBackPressed();
        }else{
            getSupportFragmentManager().popBackStack();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id ==R.id.menu_home){
            onFragmentSelected(0,null);
        }
        else if(id==R.id.menu_challenge){
            onFragmentSelected(1,null);
        }
        else if(id==R.id.menu_campaign){
            onFragmentSelected(2,null);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onFragmentSelected(int position, Bundle bundle){
        Fragment curFragment = null;

        if(position ==0){
            curFragment = homefragment;
            toolbar.setTitle("리싸이클링헬퍼");
        }
        else if(position ==1){
            curFragment = challengefragment;
            toolbar.setTitle("챌린지");
        }
        else if(position ==2){
            curFragment = campaignfragment;
            toolbar.setTitle("캠페인");
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFragment).commit();
    }

    public interface onKeyBackPressedListener{
        void onBackKey();
    }

}
