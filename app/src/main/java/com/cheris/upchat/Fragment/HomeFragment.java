package com.cheris.upchat.Fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cheris.upchat.Adapter.PostAdapter;
import com.cheris.upchat.Adapter.StoryAdapter;
import com.cheris.upchat.Model.Post;
import com.cheris.upchat.Model.Story;
import com.cheris.upchat.Model.UserStories;
import com.cheris.upchat.R;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Date;


public class HomeFragment extends Fragment {

    RecyclerView storyRV;
    ShimmerRecyclerView dashboardRV;
    ArrayList<Story> storyList;
    ArrayList<Post> postList;
    //    ImageView addstory;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    RoundedImageView addStoryImage;
    ActivityResultLauncher<String> galleryLauncher;
    ProgressDialog dialog;

// swipeRefreshLayout
    private SwipeRefreshLayout swipeRefreshLayout;


    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        dialog = new ProgressDialog(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_home, container, false);



        //swiperefreshlayout
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);

        dashboardRV = view.findViewById(R.id.dashboardRV);
        dashboardRV.showShimmerAdapter();


        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);

        // Story Recycler View
        storyRV = view.findViewById(R.id.storyRV);
        storyList = new ArrayList<>();

//        list.add(new Story(R.drawable.dennis,R.drawable.ic_video_camera,R.drawable.deaf,"Darshi"));

        StoryAdapter adapter = new StoryAdapter(storyList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        storyRV.setLayoutManager(layoutManager);
        storyRV.setNestedScrollingEnabled(false);
        storyRV.setAdapter(adapter);


        database.getReference()
                .child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    storyList.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()){
                        Story story = new Story();
                        story.setStoryBy(storySnapshot.getKey());
                        story.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));




                        ArrayList<UserStories> stories = new ArrayList<>();
                        for (DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()){
                            UserStories userStories = snapshot1.getValue(UserStories.class);
                            stories.add(userStories);
                        }
                        story.setStories(stories);
                        storyList.add(story);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Dashboard Recycler View

        postList = new ArrayList<>();

        PostAdapter postAdapter = new PostAdapter(postList,getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        dashboardRV.setLayoutManager(linearLayoutManager);
        dashboardRV.addItemDecoration(new DividerItemDecoration(dashboardRV.getContext(),DividerItemDecoration.HORIZONTAL));
        dashboardRV.setNestedScrollingEnabled(false);

        database.getReference().child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
//        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());  //may produce NullPointerException
                    postList.add(post);
                }
                dashboardRV.setAdapter(postAdapter);

                dashboardRV.hideShimmerAdapter();
                postAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                database.getReference().child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    postList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Post post = dataSnapshot.getValue(Post.class);
                        post.setPostId(dataSnapshot.getKey());  //may produce NullPointerException
                        postList.add(post);
                    }
                    dashboardRV.setAdapter(postAdapter);

                    dashboardRV.hideShimmerAdapter();
                    postAdapter.notifyDataSetChanged();
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

                swipeRefreshLayout.setRefreshing(false);
            }
        });


//        addStory = view.findViewById(R.id.addS)
        addStoryImage = view.findViewById(R.id.storyImg);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    addStoryImage.setImageURI(result);

                    dialog.show();

                    final StorageReference reference = storage.getReference()
                            .child("stories")
                            .child(FirebaseAuth.getInstance().getUid())  //might be null
                            .child(new Date().getTime()+"");
                    reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Story story = new Story();
                                    story.setStoryAt(new Date().getTime());

                                    database.getReference()
                                            .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("postedBy")
                                            .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            UserStories stories = new UserStories(uri.toString(), story.getStoryAt());

                                            database.getReference()
                                                    .child("stories")
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .child("userStories")
                                                    .push()
                                                    .setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

            }
        });


        return view;
    }


}