package com.makaan.activity.buyerJourney;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makaan.R;
import com.makaan.database.DataProvider;
import com.makaan.database.DatabaseHelper;
import com.makaan.database.NotificationDbHelper;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.notification.NotificationAttributes;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by sunil on 18/12/15.
 */

public class NotificationsFragment extends MakaanBaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private NotificationsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final int NOTIFICATIONS_LOADER = 0;

    public List<NotificationAttributes> list;

    @Bind(R.id.notification_recycler_view)
    RecyclerView mRecyclerView;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_notifications;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super .onCreateView(inflater, container, savedInstanceState);

        mLayoutManager = new LinearLayoutManager(getActivity());
        list=new ArrayList<>();

        if (mLayoutManager != null) {
            mRecyclerView.setLayoutManager(mLayoutManager);
        }

        mAdapter = new NotificationsAdapter(getActivity(), list);

        mRecyclerView.setAdapter(mAdapter);

        getActivity().getSupportLoaderManager().initLoader(NOTIFICATIONS_LOADER, null, this);
        showProgress();

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
                DatabaseHelper.NotificationAttributeColumns.TIMESTAMP + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        if(getActivity()==null || getActivity().isFinishing()){
            return;
        }

        List<NotificationAttributes> notifications = NotificationDbHelper.getNotificationList(data);
        if(null==notifications || notifications.isEmpty()){
            showNoResults(R.string.no_notifications);
        } else {
            showContent();
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.setData(notifications);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
