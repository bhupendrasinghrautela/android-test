package com.makaan.activity.buyerJourney;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.notification.NotificationAttributes;

/**
 * Created by sunil on 18/12/15.
 */
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationsFragment extends Fragment {

    private NotificationsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public List<NotificationAttributes> list;

    @Bind(R.id.notification_recycler_view)
    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, view);
        mLayoutManager = new LinearLayoutManager((Context) getActivity());
        list = new ArrayList<NotificationAttributes>();
/*        int i;
        for (i = 0; i < 5; i++) {
            NotificationAttributes notificationAttributes = new NotificationAttributes();
            notificationAttributes.setMessage("title");
            notificationAttributes.setTitle("sub title ");
            list.add(notificationAttributes);
        }*/

        if (mLayoutManager != null)
            mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NotificationsAdapter(getActivity(), list);

        if (mAdapter != null)
            mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(list);

        return view;
    }
}
