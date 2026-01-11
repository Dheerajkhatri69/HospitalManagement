package com.example.hospitalmanagement.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hospitalmanagement.fragment.DoctorAppointmentsFragment;
import com.example.hospitalmanagement.fragment.DoctorHomeFragment;
import com.example.hospitalmanagement.fragment.DoctorLabFragment;
import com.example.hospitalmanagement.fragment.DoctorProfileFragment;

public class DoctorViewPagerAdapter extends FragmentStateAdapter {

    public DoctorViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DoctorHomeFragment();
            case 1:
                return new DoctorAppointmentsFragment();
            case 2:
                return new DoctorLabFragment();
            case 3:
                return new DoctorProfileFragment();
            default:
                return new DoctorHomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
