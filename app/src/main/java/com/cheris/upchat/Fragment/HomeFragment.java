package com.cheris.upchat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cheris.upchat.Adapter.PostAdapter;
import com.cheris.upchat.Adapter.StoryAdapter;
import com.cheris.upchat.Model.Post;
import com.cheris.upchat.Model.StoryModel;
import com.cheris.upchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    RecyclerView storyRv, dashboardRV;
    ArrayList<StoryModel> list;
    ArrayList<Post> postList;
//    ImageView addstory;
    FirebaseDatabase database;
    FirebaseAuth auth;


    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        // Story Recycler View
        storyRv = view.findViewById(R.id.storyRV);
        list = new ArrayList<>();
        list.add(new StoryModel(R.drawable.dennis,R.drawable.ic_video_camera,R.drawable.deaf,"Darshi"));
        list.add(new StoryModel(R.drawable.dennis,R.drawable.ic_video_camera,R.drawable.deaf,"Darshi"));
        list.add(new StoryModel(R.drawable.dennis,R.drawable.ic_video_camera,R.drawable.deaf,"Darshi"));
        list.add(new StoryModel(R.drawable.dennis,R.drawable.ic_video_camera,R.drawable.deaf,"Darshi"));

        list.add(new StoryModel(R.drawable.dennis,R.drawable.ic_video_camera,R.drawable.deaf,"Darshi"));
        list.add(new StoryModel(R.drawable.dennis,R.drawable.ic_video_camera,R.drawable.deaf,"Darshi"));

        StoryAdapter adapter = new StoryAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRv.setLayoutManager(linearLayoutManager);;
        storyRv.setNestedScrollingEnabled(false);
        storyRv.setAdapter(adapter);


        // Dashboard Recycler View
        dashboardRV = view.findViewById(R.id.dashboardRv);
        postList = new ArrayList<>();

        PostAdapter postAdapter = new PostAdapter(postList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dashboardRV.setLayoutManager(layoutManager);
        dashboardRV.addItemDecoration(new DividerItemDecoration(dashboardRV.getContext(),DividerItemDecoration.HORIZONTAL));
        dashboardRV.setNestedScrollingEnabled(false);
        dashboardRV.setAdapter(postAdapter);


        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        addStory = view.findViewById(R.id.addS)


        return view;
    }
}