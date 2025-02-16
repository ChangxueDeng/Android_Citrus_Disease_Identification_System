package com.example.shiju;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {


    public FragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment =  new HelpBlackSpotFragment();
                break;
            case 1:
                fragment =  new HelpCankerFragment();
                break;
            default:
                fragment =  new HelpGreeningFragment();
                break;
        }
        Log.d("FragmentAdapter", "Created fragment at position " + position + ": " + fragment.getClass().getSimpleName());
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
