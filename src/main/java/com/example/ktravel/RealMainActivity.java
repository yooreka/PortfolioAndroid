package com.example.ktravel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class RealMainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    GridAdapter adapter;
    ViewPagerAdapter vadapter;
    GridLayoutManager layoutManager;
    ViewPager2 viewPager2;

    ArrayList<Item> list = new ArrayList<Item>() {{
        add(new Item("산",R.drawable.image1));
        add(new Item("시장",R.drawable.image2));
        add(new Item("축제",R.drawable.image3));
        add(new Item("캠핑",R.drawable.image4));
        add(new Item("테마파크",R.drawable.image5));
        add(new Item("해변",R.drawable.image6));
        add(new Item("박물관",R.drawable.image7));
        add(new Item("항구",R.drawable.image8));

    }};
    ArrayList<BannerItem> blist  = new ArrayList<BannerItem>() {{
        add(new BannerItem(R.drawable.door, "광화문"));
        add(new BannerItem(R.drawable.camp,"강동그린웨이 가족캠핑장"));
        add(new BannerItem(R.drawable.garak,"가락시장"));
        add(new BannerItem(R.drawable.tower,"남삼타워"));
        add(new BannerItem(R.drawable.goong,"경복궁"));

    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);

        recyclerView = (RecyclerView)findViewById(R.id.gridRecyclerView);
        adapter = new GridAdapter(getApplicationContext(), list);

        layoutManager = new GridLayoutManager(getApplicationContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        viewPager2 = (ViewPager2)findViewById(R.id.viewPager2);
        vadapter = new ViewPagerAdapter(getApplicationContext(), blist);
        viewPager2.setAdapter(vadapter);
    }
}


