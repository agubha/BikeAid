package com.example.bikeaid.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.bikeaid.Fragments.HistoryFragment;
import com.example.bikeaid.Fragments.PurchaseFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return PurchaseFragment.newInstance(1, "1");
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return HistoryFragment.newInstance(0, "2");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
