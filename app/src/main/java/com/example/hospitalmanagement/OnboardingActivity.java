package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.example.hospitalmanagement.adapter.OnboardingAdapter;
import com.example.hospitalmanagement.model.ScreenItem;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends BaseActivity {

    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;
    private MaterialButton actionButton;
    private TextView skipText;
    private List<ScreenItem> screenItems;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initializeViews();
        setupOnboardingData();
        setupViewPager();
        setupButtonClickListener();
        setupSkipClickListener();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPager);
        actionButton = findViewById(R.id.actionButton);
        skipText = findViewById(R.id.skipText);
    }

    private void setupOnboardingData() {
        screenItems = new ArrayList<>();

        // Screen 1
        screenItems.add(new ScreenItem(
                R.drawable.welcome1, // Replace with your image resource
                "Find Trusted Doctors",
                "Contrary to popular belief, Pules360 is not simply random text. It has roots in a piece of it over 2000 years old.",
                "Next",
                false));

        // Screen 2
        screenItems.add(new ScreenItem(
                R.drawable.welcome2, // Replace with your image resource
                "Choose Best Doctors",
                "Contrary to popular belief, Pulse360  is not simply random text. It has roots in a piece of it over 2000 years old.",
                "Next",
                false));

        // Screen 3 (Last screen)
        screenItems.add(new ScreenItem(
                R.drawable.welcome3, // Replace with your image resource
                "Easy Appointments",
                "Contrary to popular belief, Pulse360 is not simply random text. It has roots in a piece of it over 2000 years old.",
                "Get Started",
                true));
    }

    private void setupViewPager() {
        adapter = new OnboardingAdapter(screenItems, this);
        viewPager.setAdapter(adapter);

        // Add page change callback
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateButtonForPage(position);

                // Hide skip button on last screen
                if (position == screenItems.size() - 1) {
                    skipText.setVisibility(View.GONE);
                } else {
                    skipText.setVisibility(View.VISIBLE);
                }
            }
        });

        // Add dots indicator (optional)
        // setupDotsIndicator();
    }

    private void updateButtonForPage(int position) {
        ScreenItem currentItem = screenItems.get(position);
        actionButton.setText(currentItem.getButtonText());
    }

    private void setupButtonClickListener() {
        actionButton.setOnClickListener(v -> {
            if (currentPage < screenItems.size() - 1) {
                // Go to next page
                viewPager.setCurrentItem(currentPage + 1, true);
            } else {
                // Last screen - navigate to main activity or home
                navigateToLogin();
            }
        });
    }

    private void setupSkipClickListener() {
        skipText.setOnClickListener(v -> {
            // Skip to login directly
            navigateToLogin();
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Optional: Add login text click listener if you have it in layout
    public void onLoginTextClick(View view) {
        navigateToLogin();
    }
}