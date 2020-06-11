package com.jung.safedrive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        boolean isFirst = intent.getBooleanExtra("isFirst",true);

        ViewPager vp=findViewById(R.id.viewpager);
        TabLayout tl=findViewById(R.id.tab_container);

        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
        tl.setupWithViewPager(vp);
        if(!isFirst)
            vp.setCurrentItem(1);
    }
}
