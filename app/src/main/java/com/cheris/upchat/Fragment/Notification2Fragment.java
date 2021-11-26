package com.cheris.upchat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cheris.upchat.Adapter.NotificationAdapter;
import com.cheris.upchat.Model.NotificationModel;
import com.cheris.upchat.R;

import java.util.ArrayList;


public class Notification2Fragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<NotificationModel> list;

    public Notification2Fragment() {
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
        View view = inflater.inflate(R.layout.fragment_notification2, container, false);

        recyclerView = view.findViewById(R.id.notification2RV);

        list = new ArrayList<>();
        list.add(new NotificationModel(R.drawable.deaf,"<b>M. AMin</b> Liked your picture","just now"));
        list.add(new NotificationModel(R.drawable.profile1,"<b>Janeeleona</b> mention you in a comment : try again","just now"));
        list.add(new NotificationModel(R.drawable.profile,"<b>Katherinn</b> Liked your picture","just now"));
        list.add(new NotificationModel(R.drawable.deaf,"<b>M. AMin</b> commented on your post","just now"));
        list.add(new NotificationModel(R.drawable.deaf,"<b>M. AMin</b> mention you in a comment : Nice try","just now"));
        list.add(new NotificationModel(R.drawable.profile1,"<b>Janeeleona</b> mention you in a comment : try again","just now"));
        list.add(new NotificationModel(R.drawable.profile,"<b>Katherinn</b> commented on your post","just now"));
        list.add(new NotificationModel(R.drawable.new_hope,"<b>Alicia Addams</b> mention you in a comment : try again","just now"));
        list.add(new NotificationModel(R.drawable.new_hope,"<b>Alicia Addams</b> commented on your post","just now"));
        list.add(new NotificationModel(R.drawable.deaf,"<b>M. AMin</b> mention you in a comment : try again","just now"));

        NotificationAdapter adapter = new NotificationAdapter(list,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }
}