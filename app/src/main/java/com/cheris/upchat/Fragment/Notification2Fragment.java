package com.cheris.upchat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cheris.upchat.Adapter.NotificationAdapter;
import com.cheris.upchat.Model.Notification;
import com.cheris.upchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Notification2Fragment extends Fragment {

    RecyclerView notificationRV;
    ArrayList<Notification> list;
    FirebaseDatabase database;

    public Notification2Fragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification2, container, false);

        notificationRV = view.findViewById(R.id.notification2RV);

        list = new ArrayList<>();
//        list.add(new Notification(R.drawable.deaf,"<b>M. AMin</b> Liked your picture","just now"));
//        list.add(new Notification(R.drawable.profile1,"<b>Janeeleona</b> mention you in a comment : try again","just now"));
//        list.add(new Notification(R.drawable.profile,"<b>Katherinn</b> Liked your picture","just now"));
//        list.add(new Notification(R.drawable.deaf,"<b>M. AMin</b> commented on your post","just now"));
//        list.add(new Notification(R.drawable.deaf,"<b>M. AMin</b> mention you in a comment : Nice try","just now"));
//        list.add(new Notification(R.drawable.profile1,"<b>Janeeleona</b> mention you in a comment : try again","just now"));
//        list.add(new Notification(R.drawable.profile,"<b>Katherinn</b> commented on your post","just now"));
//        list.add(new Notification(R.drawable.new_hope,"<b>Alicia Addams</b> mention you in a comment : try again","just now"));
//        list.add(new Notification(R.drawable.new_hope,"<b>Alicia Addams</b> commented on your post","just now"));
//        list.add(new Notification(R.drawable.deaf,"<b>M. AMin</b> mention you in a comment : try again","just now"));

        NotificationAdapter adapter = new NotificationAdapter(list,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        notificationRV.setLayoutManager(layoutManager);
        notificationRV.setNestedScrollingEnabled(false);
        notificationRV.setAdapter(adapter);

        database.getReference()
                .child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            notification.setNotificationID(dataSnapshot.getKey());
                            list.add(notification);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return view;
    }
}