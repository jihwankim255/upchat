package com.cheris.upchat.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cheris.upchat.Adapter.FriendAdapter;
import com.cheris.upchat.Model.FriendModel;
import com.cheris.upchat.R;
import com.cheris.upchat.Model.User;
import com.cheris.upchat.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {


//    RecyclerView recyclerView;
    ArrayList<FriendModel> list;
    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    int request_code;

    ActivityResultLauncher<String> galleryLauncher;

    public ProfileFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
//        recyclerView = view.findViewById(R.id.friendRV);

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user = snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getCoverPhoto())
                            .placeholder(R.drawable.placeholder)
                            .into(binding.coverPhoto);
                    Picasso.get()
                            .load(user.getProfile())
                            .placeholder(R.drawable.placeholder)
                            .into(binding.profileImage);
                    binding.userName.setText(user.getName());
                    binding.profession.setText(user.getProfession());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        list = new ArrayList<>();

        list.add(new FriendModel(R.drawable.profile));
        list.add(new FriendModel(R.drawable.profile));
        list.add(new FriendModel(R.drawable.cover));
        list.add(new FriendModel(R.drawable.profile));
        list.add(new FriendModel(R.drawable.profile1));

        FriendAdapter adapter = new FriendAdapter(list,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        binding.friendRV.setLayoutManager(layoutManager);
        binding.friendRV.setAdapter(adapter);
//        recyclerView.setLayoutManager(linearLayoutManag
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (request_code == 11){
                    if (uri != null) {
                        binding.coverPhoto.setImageURI(uri);

                        final StorageReference reference = storage.getReference().child("cover_photo").child(FirebaseAuth.getInstance().getUid());
                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Cover photo saved", Toast.LENGTH_SHORT).show();
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        database.getReference().child("Users").child(auth.getUid()).child("coverPhoto").setValue(uri.toString());
                                    }
                                });
                            }
                        });
                    }
                } else if (request_code == 22){
                    if (uri != null) {
                        binding.profileImage.setImageURI(uri);

                        final StorageReference reference = storage.getReference().child("profile_image").child(FirebaseAuth.getInstance().getUid());
                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Profile photo saved", Toast.LENGTH_SHORT).show();
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        database.getReference().child("Users").child(auth.getUid()).child("profile").setValue(uri.toString());
                                    }
                                });
                            }
                        });
                    }

                }

            }
        });

        binding.changeCoverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request_code = 11;
                galleryLauncher.launch("image/*");


//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, 11);

            }
        });
        binding.verifiedAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request_code = 22;
                galleryLauncher.launch("image/*");
            }
        });

        return binding.getRoot();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data.getData() != null) {
//            Uri uri = data.getData();
//            binding.coverPhoto.setImageURI(uri);
//        }
//    }
}