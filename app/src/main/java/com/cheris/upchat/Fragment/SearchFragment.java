package com.cheris.upchat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cheris.upchat.Adapter.UserAdapter;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    ArrayList<User> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    public SearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        UserAdapter adapter = new UserAdapter(getContext(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.usersRV.setLayoutManager(layoutManager);
        binding.usersRV.setAdapter(adapter);

        // Search 목록을 뿌려주는 부분
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    user.setUserID(dataSnapshot.getKey());
                    // 현재유저를 Search목록에서 제거
                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(user);

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}