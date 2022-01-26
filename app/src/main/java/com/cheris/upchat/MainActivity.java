package com.cheris.upchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cheris.upchat.Fragment.AddPostFragment;
import com.cheris.upchat.Fragment.ChatFragment;
import com.cheris.upchat.Fragment.HomeFragment;
import com.cheris.upchat.Fragment.NotificationFragment;
import com.cheris.upchat.Fragment.ProfileFragment;
import com.cheris.upchat.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    // BottomNavigation with backstack
    BottomNavigationView bottomNavigationView;
    Deque<Integer> integerDeque = new ArrayDeque<>(5);
    boolean flag = true;

    // prevent recreation of fragment
    final Fragment homeFragment = new HomeFragment();
    final Fragment notificationFragment = new NotificationFragment();
    final Fragment addPostFragment = new AddPostFragment();
    final Fragment chatFragment = new ChatFragment();
    final Fragment profileFragment = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        MainActivity.this.setTitle("My Profile");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        binding.toolbar.setVisibility(View.VISIBLE);
        transaction.replace(R.id.container, homeFragment,"0");
        transaction.commit();

        //Assign variable
        bottomNavigationView = binding.bottomNavigation;

        // Add home fragment in deque list
        integerDeque.push(R.id.bn_home);
        // Load home fragment
        loadFragment(new HomeFragment());
        // Set home as default fragment
        bottomNavigationView.setSelectedItemId(R.id.bn_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //Get selected item id
                        int id = item.getItemId();
                        //Check condition
                        if (integerDeque.contains(id)) {
                            //When deque list contains selected id
                            //Check condition
                            if (id == R.id.bn_home) {
                                //When selected id is equal to home fragment id
                                //Check condition
                                if (integerDeque.size() != 1) {
                                    //When deque list size is not equal to 1
                                    //Check condition
                                    if (flag) {
                                        //When flag value is true
                                        //Add home fragment in deque list
                                        integerDeque.addFirst(R.id.bn_home);
                                        //Set flag is equal to false
                                        flag = false;
                                    }
                                }
                            }
                            //Remove selected id from deque list
                            integerDeque.remove(id);
                        }
                        //Push selected id in deque list
                        integerDeque.push(id);
                        //Load fragment
                        loadFragment(getFragment(item.getItemId()));
                        //return true
                        return true;
                    }
                }
        );

//        binding.readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
//            @Override
//            public void onItemSelected(int i) {
//
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                switch (i) {
//                    case 0:
//                        binding.toolbar.setVisibility(View.VISIBLE);
//                        transaction.replace(R.id.container, new HomeFragment(),"0");
//                        break;
//
//                    case 1:
//                        binding.toolbar.setVisibility(View.GONE);
//                        transaction.replace(R.id.container, new NotificationFragment(),"1");
//                        break;
//
//                    case 2:
//                        binding.toolbar.setVisibility(View.GONE);
//                        transaction.replace(R.id.container, new AddPostFragment(),"2");
//                        break;
//
//                    case 3:
//                        binding.toolbar.setVisibility(View.GONE);
//                        transaction.replace(R.id.container, new ChatFragment(),"3");
//                        break;
//
//                    case 4:
//                        binding.toolbar.setVisibility(View.VISIBLE);
//                        transaction.replace(R.id.container, new ProfileFragment(),"4");
//                        break;
//                }
//                transaction.commit();
//
//            }
//        });

    }

    private Fragment getFragment(int itemId) {
        switch (itemId) {
            case R.id.bn_home:
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                return new HomeFragment();
            case R.id.bn_notification:
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                return new NotificationFragment();
            case R.id.bn_add:
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                binding.toolbar.setVisibility(View.GONE);
                return new AddPostFragment();
            case R.id.bn_chat:
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
                binding.toolbar.setVisibility(View.GONE);
                return new ChatFragment();
            case R.id.bn_profile:
                bottomNavigationView.getMenu().getItem(4).setChecked(true);
                binding.toolbar.setVisibility(View.VISIBLE);
                return new ProfileFragment();
        }
        //Set checked default home fragment
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        //Return Home fragment
        return new HomeFragment();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,fragment,fragment.getClass().getSimpleName())
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();
        //Pop to previous fragment
        if (!integerDeque.isEmpty()) {
            integerDeque.pop();
        }
        //Check condition
        if (!integerDeque.isEmpty()) {
            //When deque list is not empty
            //Load fragment
            loadFragment(getFragment(integerDeque.peek()));
        } else {
            //When deque list is empty
//            finish();
            exitByBackKey();
        }

//        Log.d("BackStackEntryCount ", String.valueOf());
//        FragmentManager.BackStackEntry backEntry = (FragmentManager.BackStackEntry) getSupportFragmentManager().getBackStackEntryAt(index);
//        String tag = backEntry.getName();
//        Log.d("tag", tag);
//        binding.readableBottomBar.selectItem(Integer.parseInt(tag));



    }

    protected void exitByBackKey() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();
                        //close();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }
}