package com.cheris.upchat;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.cheris.upchat.Fragment.AddFragment;
import com.cheris.upchat.Fragment.HomeFragment;
import com.cheris.upchat.Fragment.NotificationFragment;
import com.cheris.upchat.Fragment.ProfileFragment;
import com.cheris.upchat.Fragment.SearchFragment;
import com.cheris.upchat.databinding.ActivityMainBinding;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new HomeFragment());
        transaction.commit();

        binding.readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (i) {
                    case 0:
                        transaction.replace(R.id.container, new HomeFragment());
                        break;

                    case 1:
                        transaction.replace(R.id.container, new NotificationFragment());
                        break;

                    case 2:
                        transaction.replace(R.id.container, new AddFragment());
                        break;

                    case 3:
                        transaction.replace(R.id.container, new SearchFragment());
                        break;

                    case 4:
                        transaction.replace(R.id.container, new ProfileFragment());
                        break;
                }
                transaction.commit();

            }
        });

    }
}
