package com.cheris.upchat.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cheris.upchat.MainActivity;
import com.cheris.upchat.Model.Post;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.R;
import com.cheris.upchat.databinding.FragmentAddPostBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;


public class AddPostFragment extends Fragment {
    Context context;
    FragmentAddPostBinding binding;
    Uri uri;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;

    public AddPostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater, container, false);

        String wait = getResources().getString(R.string.wait);
        String postUploading = getResources().getString(R.string.postUploading);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle(postUploading);
        dialog.setMessage(wait);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // post할때 쓰는사람 이름과 프사를 가져옴
        database.getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getActivity() == null) {
                    return;
                }
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
//                    Picasso.get()
                    Glide.with(getActivity())
                            .load(user.getProfile())
                            .placeholder(R.drawable.placeholder)
                            .into(binding.notificationProfile);
                    binding.name.setText(user.getName());
                    binding.profession.setText(user.getProfession());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.postDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String description = binding.postDescription.getText().toString();
                // post가 비지않았을때 버튼을 활성화
                if (!(description.trim().length()<5)) {
                    binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btn_bg));
                    binding.postBtn.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postBtn.setEnabled(true);
                } else {
                    binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_active_btn));
                    binding.postBtn.setTextColor(getContext().getResources().getColor(R.color.grey));
                    binding.postBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                final long postDate = new Date().getTime();
                final StorageReference reference = storage.getReference().child("posts")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(postDate+"");
                if (uri == null) { // 사진 업로드를 안했을 때
//                    binding.postDescription.setVisibility(View.GONE);
                    Post post = new Post();
                    post.setPostedBy(FirebaseAuth.getInstance().getUid());
                    post.setPostDescription(binding.postDescription.getText().toString());
                    post.setPostedAt(postDate);
                    database.getReference().child("posts")
                            .push()
                            .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                            post.setPostedBy(null);
                            post.setPostDescription("");
                            post.setPostedAt(0);
                        }
                    });
                } else {  // 사진 업로드를 했을 때
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Post post = new Post();
                                    post.setPostImage(uri.toString());
                                    post.setPostedBy(FirebaseAuth.getInstance().getUid());
                                    post.setPostDescription(binding.postDescription.getText().toString());
                                    post.setPostedAt(postDate);

                                    database.getReference().child("posts")
                                            .push()
                                            .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            Toast.makeText(getContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                                            post.setPostImage(null);
                                            post.setPostedBy(null);
                                            post.setPostDescription("");
                                            post.setPostedAt(0);
                                            binding.postBtn.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                binding.postDescription.setText(""); // 공백으로
                ((MainActivity)getActivity()).afterAddPostEvent();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            if (data.getData() != null){
                uri = data.getData();
                binding.postImage.setImageURI(uri);
                binding.postImage.setVisibility(View.VISIBLE);

                binding.postBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.follow_btn_bg));
                binding.postBtn.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.postBtn.setEnabled(true);

            }
        }

    }
}