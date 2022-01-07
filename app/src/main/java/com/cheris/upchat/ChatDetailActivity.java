package com.cheris.upchat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.cheris.upchat.databinding.ActivityChatDetailBinding;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}