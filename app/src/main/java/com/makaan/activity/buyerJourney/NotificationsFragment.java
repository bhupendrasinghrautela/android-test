package com.makaan.activity.buyerJourney;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.database.DataProvider;
import com.makaan.database.NotificationDbHelper;
import com.makaan.notification.NotificationAttributes;

/**
 * Created by sunil on 18/12/15.
 */
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private NotificationsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final int NOTIFICATIONS_LOADER = 0;

    public List<NotificationAttributes> list;

    @Bind(R.id.notification_recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_no_notifications)
    TextView mZeroNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager((Context)getActivity());
        list=new ArrayList<NotificationAttributes>();


        if(list==null||list.size()==0){
            mZeroNotifications.setVisibility(View.VISIBLE);
        }

        if(mLayoutManager != null)
            mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NotificationsAdapter(getActivity(), list);

        if(mAdapter!= null) {
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setData(list);
        }

        getActivity().getSupportLoaderManager().initLoader(NOTIFICATIONS_LOADER, null, this);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),   // Parent activity context
                DataProvider.SAVED_NOTIFICATIONS_URI,        // Table to query
                null,     // Projection to return
                null,     // No selection clause
                null,     // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        if(getActivity()==null || getActivity().isFinishing()){
            return;
        }

        List<NotificationAttributes> notifications = NotificationDbHelper.getNotificationList(data);
        if(notifications.isEmpty()){
            mZeroNotifications.setVisibility(View.VISIBLE);
        } else {
            mZeroNotifications.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.setData(notifications);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
