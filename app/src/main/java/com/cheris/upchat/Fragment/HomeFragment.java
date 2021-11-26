package com.cheris.upchat.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cheris.upchat.Adapter.DashboardAdapter;
import com.cheris.upchat.Adapter.StoryAdapter;
import com.cheris.upchat.Model.DashboardModel;
import com.cheris.upchat.Model.StoryModel;
import com.cheris.upchat.R;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    RecyclerView storyRv, dashboardRV;
    ArrayList<StoryModel> list;
    ArrayList<DashboardModel> dashboardList;


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

        dashboardRV = view.findViewById(R.id.dashboardRv);

        dashboardList = new ArrayList<>();
        dashboardList.add(new DashboardModel(R.drawable.profile,R.drawable.new_hope,R.drawable.saved,"Denis kane","Travler","464","12","15"));
        dashboardList.add(new DashboardModel(R.drawable.profile,R.drawable.new_hope,R.drawable.saved,"Denis kane","Travler","464","12","15"));
        dashboardList.add(new DashboardModel(R.drawable.profile,R.drawable.new_hope,R.drawable.saved,"Denis kane","Travler","4264","112","415"));
        dashboardList.add(new DashboardModel(R.drawable.profile,R.drawable.new_hope,R.drawable.saved,"Denis kane","Travler","44","2","5"));
        dashboardList.add(new DashboardModel(R.drawable.profile,R.drawable.new_hope,R.drawable.saved,"Denis kane","Travler","44","127","115"));
        dashboardList.add(new DashboardModel(R.drawable.profile,R.drawable.new_hope,R.drawable.saved,"Denis kane","Travler","4644","1","55"));

        DashboardAdapter dashboardAdapter = new DashboardAdapter(dashboardList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        dashboardRV.setLayoutManager(layoutManager);
        dashboardRV.setNestedScrollingEnabled(false);
        dashboardRV.setAdapter(dashboardAdapter);
        return view;
    }
}