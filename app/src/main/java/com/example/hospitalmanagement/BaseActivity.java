package com.example.hospitalmanagement;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import com.example.hospitalmanagement.utils.NetworkMonitor;

public class BaseActivity extends AppCompatActivity {

    private TextView noInternetBanner;
    private NetworkMonitor networkMonitor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkMonitor = NetworkMonitor.getInstance(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupNoInternetBanner();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setupNoInternetBanner();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setupNoInternetBanner();
    }

    private void setupNoInternetBanner() {
        // Create the banner view pragmatically or inflate it
        View rootView = findViewById(android.R.id.content);
        if (rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;

            // Inflate our custom view
            View bannerView = getLayoutInflater().inflate(R.layout.view_no_internet, viewGroup, false);
            noInternetBanner = bannerView.findViewById(R.id.tv_no_internet);

            // Add it to the root view.
            // We want it to be on top. FrameLayout (which acts as the content root mostly)
            // stacks children.
            // Adding it last usually puts it on top in Z-order.
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.TOP;

            // If the root is not a FrameLayout (unlikely for android.R.id.content), this
            // might need adjustment,
            // but usually it is a FrameLayout or similar.
            viewGroup.addView(bannerView, params);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkMonitor.getIsConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isConnected) {
                if (noInternetBanner != null) {
                    if (isConnected) {
                        noInternetBanner.setVisibility(View.GONE);
                    } else {
                        noInternetBanner.setVisibility(View.VISIBLE);
                        noInternetBanner.bringToFront(); // Ensure it stays on top
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // LiveData observation is lifecycle aware, so we technically don't need to
        // manually remove observers
        // if we use 'this' as the LifecycleOwner.
    }
}
