package com.makaan.fragment.buyerJourney;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewFragment;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.event.content.BlogByTagEvent;
import com.makaan.event.serp.GroupSerpGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.content.BlogItem;
import com.makaan.service.BlogService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.util.AppBus;
import com.makaan.util.AppUtils;
import com.makaan.util.KeyUtil;
import com.squareup.otto.Subscribe;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/28/16.
 */
public class BlogContentFragment extends MakaanBaseFragment {

    @Bind(R.id.fragment_blog_content_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.fragment_blog_content_progress_bar)
    ProgressBar mProgressBar;
    private BlogContentAdapter mAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_blog_content;
    }

    public static String TYPE="type";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null && !TextUtils.isEmpty(bundle.getString(TYPE)))
        ((BlogService)MakaanServiceFactory.getInstance().getService(BlogService.class)).getBlogs(bundle.getString(TYPE));
        else
            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);

        mAdapter = new BlogContentAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Subscribe
    public synchronized void onResults(BlogByTagEvent blogByTagGetEvent) {
        if(null== blogByTagGetEvent || null!=blogByTagGetEvent.error){
            //TODO handle error
            return;
        }

        if(blogByTagGetEvent.blogItems != null && blogByTagGetEvent.blogItems.size() > 0) {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            if(mAdapter != null) {
                mAdapter.setData(blogByTagGetEvent.blogItems);
            }
        }
    }

    class BlogContentAdapter extends RecyclerView.Adapter<BlogContentAdapter.BlogContentHolder> {

        private final Context mContext;
        private List<BlogItem> mBlogItems;

        BlogContentAdapter(Context context) {
            mContext = context;
        }

        @Override
        public BlogContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.card_content, parent, false);

            return new BlogContentHolder(view);
        }

        @Override
        public int getItemCount() {
            if(mBlogItems != null) {
                return mBlogItems.size();
            }
            return 0;
        }

        @Override
        public void onBindViewHolder(BlogContentHolder holder, int position) {
            holder.imageView.setImageUrl(mBlogItems.get(position).primaryImageUrl, MakaanNetworkClient.getInstance().getImageLoader());
            holder.titleTextView.setText(mBlogItems.get(position).postTitle);
            holder.link = mBlogItems.get(position).guid;
        }

        void setData(ArrayList<BlogItem> blogItems) {
            if(mBlogItems == null) {
                mBlogItems = new ArrayList<>();
            } else {
                mBlogItems.clear();
            }

            if(blogItems != null) {
                mBlogItems.addAll(blogItems);
            }
            notifyDataSetChanged();
        }

        class BlogContentHolder extends RecyclerView.ViewHolder {
            FadeInNetworkImageView imageView;
            TextView titleTextView;
            public String link;

            public BlogContentHolder(View itemView) {
                super(itemView);
                imageView = (FadeInNetworkImageView)itemView.findViewById(R.id.iv_content);
                titleTextView = (TextView)itemView.findViewById(R.id.txt_content);

                itemView.setOnClickListener(listener);
            }

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(link != null) {
                        Activity activity = getActivity();
                        if(activity instanceof BuyerDashboardCallbacks) {
                            Bundle bundle = new Bundle();
                            bundle.putString(KeyUtil.KEY_REQUEST_URL, link);
                            ((BuyerDashboardCallbacks) activity).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_WEB_VIEW, true, bundle);
                        }
                    }
                }
            };
        }
    }
}
