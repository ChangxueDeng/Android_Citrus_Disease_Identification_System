package com.example.shiju;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;



public class HelpFragment extends Fragment {
    private final String[] titleNames = {"黑斑病", "溃疡病", "黄龙病"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tl = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = view.findViewById(R.id.vp2);
        viewPager2.setAdapter(new FragmentAdapter(this));
        TabLayoutMediator tab = new TabLayoutMediator(tl, viewPager2, new TabLayoutMediator.TabConfigurationStrategy(){
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                Log.d("onConfigureTab: ", String.valueOf(position));
                switch (position) {
                    case 0:
                        tab.setText(titleNames[0]);
                        break;
                    case 1:
                        tab.setText(titleNames[1]);
                        break;
                    case 2:
                        tab.setText(titleNames[2]);
                        break;
                }
            }
        });
        tab.attach();
    }
}

