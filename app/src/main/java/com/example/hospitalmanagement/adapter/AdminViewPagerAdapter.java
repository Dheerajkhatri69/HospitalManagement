package com.example.hospitalmanagement.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hospitalmanagement.fragment.AdminDoctorsFragment;
import com.example.hospitalmanagement.fragment.AdminHomeFragment;
import com.example.hospitalmanagement.fragment.AdminProfileFragment;
import com.example.hospitalmanagement.fragment.AdminRequestsFragment;

public class AdminViewPagerAdapter extends FragmentStateAdapter {

    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminHomeFragment();
            case 1:
                return new AdminRequestsFragment();
            case 2:
                return new AdminDoctorsFragment();
            case 3:
                return new AdminProfileFragment();
            default:
                return new AdminHomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
