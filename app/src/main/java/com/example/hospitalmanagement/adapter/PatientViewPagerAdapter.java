package com.example.hospitalmanagement.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hospitalmanagement.BookAppointmentFragment;
import com.example.hospitalmanagement.fragment.PatientAppointmentsFragment;
import com.example.hospitalmanagement.fragment.PatientHomeFragment;
import com.example.hospitalmanagement.fragment.PatientProfileFragment;

public class PatientViewPagerAdapter extends FragmentStateAdapter {

    public PatientViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PatientHomeFragment();
            case 1:
                return BookAppointmentFragment.newInstance();
            case 2:
                return new PatientAppointmentsFragment();
            case 3:
                return new PatientProfileFragment();
            default:
                return new PatientHomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
