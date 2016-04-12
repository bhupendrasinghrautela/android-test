package com.makaan.fragment.buyerJourney;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.buyerJourney.BuyerDashboardActivity;
import com.makaan.activity.buyerJourney.BuyerDashboardCallbacks;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.content.BlogByTagEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.content.BlogItem;
import com.makaan.service.BlogService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.ui.CustomNetworkImageView;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/28/16.
 */
public class BlogContentFragment extends MakaanBaseFragment {

    @Bind(R.id.fragment_blog_content_recycler_view)
    RecyclerView mRecyclerView;

    private BlogContentAdapter mAdapter;

    public static final String SEARCH = "search";
    public static final String SHORTLIST = "shortlist";
    public static final String SITE_VISIT = "sitevisit";
    public static final String HOME_LOAN = "homeloan";
    public static final String UNIT_BOOK = "unit booking";
    public static final String POSSESSION = "possession";
    public static final String REGISTRATION = "registration";
    private String mType;

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
        if(bundle!=null && !TextUtils.isEmpty(bundle.getString(TYPE))) {
            String type = bundle.getString(TYPE);
            ((BlogService) MakaanServiceFactory.getInstance().getService(BlogService.class)).getBlogs(type);
            showProgress();
            mType = type;

        }
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);

        mAdapter = new BlogContentAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void initHeaderUi(View view) {
        TextView titleTextView = (TextView)view.findViewById(R.id.fragment_blog_title_text_view);
        TextView subTitleTextView = (TextView)view.findViewById(R.id.fragment_blog_subtitle_text_view);
        TextView actionTextView = (TextView)view.findViewById(R.id.fragment_blog_action_text_view);

        switch (mType) {
            case SEARCH:
                titleTextView.setVisibility(View.VISIBLE);
                subTitleTextView.setVisibility(View.VISIBLE);
                actionTextView.setVisibility(View.VISIBLE);

                titleTextView.setText(getResources().getString(R.string.blog_content_search_title));
                subTitleTextView.setText(getResources().getString(R.string.blog_content_search_sub_title));
                actionTextView.setText(getResources().getString(R.string.blog_content_search_action));

                if(getActivity() != null) {
                    getActivity().setTitle("save searches");
                }
                break;
            case SHORTLIST:
                titleTextView.setVisibility(View.VISIBLE);
                subTitleTextView.setVisibility(View.VISIBLE);
                actionTextView.setVisibility(View.VISIBLE);

                titleTextView.setText(getResources().getString(R.string.blog_content_shortlist_title));
                String subtitle = getResources().getString(R.string.blog_content_shortlist_sub_title);
                if(subtitle.indexOf("{img=") >= 0) {
                    int index = subtitle.indexOf("{img=");
                    subtitle = subtitle.replace("{img=", "");
                    int lastIndex = subtitle.indexOf("}", index);
                    String imageName = subtitle.substring(index, lastIndex);
                    subtitle = subtitle.replace(imageName + "}", "");
                    int id = this.getResources().getIdentifier(imageName, "drawable", "com.makaan");

                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    builder.append(subtitle.substring(0, index)).append(" ");
                    builder.setSpan(new ImageSpan(getActivity(), id), builder.length() - 1, builder.length(), 0);
                    builder.append(subtitle.substring(index, subtitle.length()));

                    subTitleTextView.setText(builder);
                } else {
                    subTitleTextView.setText(getResources().getString(R.string.blog_content_shortlist_sub_title));
                }
                actionTextView.setText(getResources().getString(R.string.blog_content_shortlist_action));

                if(getActivity() != null) {
                    getActivity().setTitle("shortlist");
                }
                break;
            case SITE_VISIT:
                titleTextView.setVisibility(View.VISIBLE);
                subTitleTextView.setVisibility(View.VISIBLE);
                actionTextView.setVisibility(View.VISIBLE);

                titleTextView.setText(getResources().getString(R.string.blog_content_site_visit_title));
                subTitleTextView.setText(getResources().getString(R.string.blog_content_site_visit_sub_title));
                actionTextView.setText(getResources().getString(R.string.blog_content_site_visit_action));

                if(getActivity() != null) {
                    getActivity().setTitle("site visits");
                }
                break;
            case HOME_LOAN:
                titleTextView.setVisibility(View.GONE);
                subTitleTextView.setVisibility(View.GONE);
                actionTextView.setVisibility(View.VISIBLE);

                actionTextView.setText(getResources().getString(R.string.blog_content_home_loan_action));

                if(getActivity() != null) {
                    getActivity().setTitle("home loan");
                }
                break;
            case UNIT_BOOK:
                titleTextView.setVisibility(View.GONE);
                subTitleTextView.setVisibility(View.GONE);
                actionTextView.setVisibility(View.VISIBLE);

                actionTextView.setText(getResources().getString(R.string.blog_content_home_booking_action));

                if(getActivity() != null) {
                    getActivity().setTitle("unit booking");
                }
                break;
            case POSSESSION:
                titleTextView.setVisibility(View.GONE);
                subTitleTextView.setVisibility(View.GONE);
                actionTextView.setVisibility(View.VISIBLE);

                actionTextView.setText(getResources().getString(R.string.blog_content_home_possession_action));

                if(getActivity() != null) {
                    getActivity().setTitle("possession");
                }
                break;
            case REGISTRATION:
                titleTextView.setVisibility(View.GONE);
                subTitleTextView.setVisibility(View.GONE);
                actionTextView.setVisibility(View.VISIBLE);

                actionTextView.setText(getResources().getString(R.string.blog_content_home_registration_action));

                if(getActivity() != null) {
                    getActivity().setTitle("registration");
                }
                break;
        }
    }

    @Subscribe
    public synchronized void onResults(BlogByTagEvent blogByTagGetEvent) {
        if(!isVisible()) {
            return;
        }
        if(null== blogByTagGetEvent || null!=blogByTagGetEvent.error) {
            showNoResults();
            return;
        }

        if(blogByTagGetEvent.blogItems != null) {
            showContent();
            mRecyclerView.setVisibility(View.VISIBLE);

            if (mAdapter != null) {
                mAdapter.setData(blogByTagGetEvent.blogItems);
            }
        }
    }

    class BlogContentAdapter extends RecyclerView.Adapter<BlogContentAdapter.BlogContentHolder> {

        private final Context mContext;
        private List<BlogItem> mBlogItems;

        static final int TYPE_HEADER = 1;
        static final int TYPE_ITEM = 2;

        BlogContentAdapter(Context context) {
            mContext = context;
        }

        @Override
        public BlogContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.card_content, parent, false);

                return new BlogContentHolder(view, viewType);
            } else if(viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.blog_content_header_layout, parent, false);

                return new BlogContentHolder(view, viewType);
            }
            return null;
        }

        @Override
        public int getItemCount() {
            if(mBlogItems != null) {
                return mBlogItems.size() + 1;
            }
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            if(mBlogItems != null) {
                if(position == 0) {
                    return TYPE_HEADER;
                } else {
                    return TYPE_ITEM;
                }
            }
            return TYPE_HEADER;
        }

        @Override
        public void onBindViewHolder(BlogContentHolder holder, int position) {
            if(holder.viewType == TYPE_ITEM) {
                int itemPosition = position-1;
                int height = (int) Math.ceil(mContext.getResources().getDimension(R.dimen.buyer_content_image_height));
                int width = (int) (mContext.getResources().getConfiguration().screenWidthDp * Resources.getSystem().getDisplayMetrics().density);
                if(TextUtils.isEmpty(mBlogItems.get(itemPosition).primaryImageUrl)) {
                    Bitmap bitmap = MakaanBuyerApplication.bitmapCache.getBitmap("blog_placeholder");
                    if (bitmap == null) {
                        int id = R.drawable.blog_placeholder;
                        bitmap = BitmapFactory.decodeResource(getResources(), id);
                        MakaanBuyerApplication.bitmapCache.putBitmap("blog_placeholder", bitmap);
                    }
                    holder.imageView.setLocalImageBitmap(bitmap);
                } else {
                    holder.imageView.setDefaultImageResId(R.drawable.blog_placeholder);
                    holder.imageView.setImageUrl(ImageUtils.getImageRequestUrl(mBlogItems.get(itemPosition).primaryImageUrl, width, height, false),
                            MakaanNetworkClient.getInstance().getImageLoader());
                }
                if(mBlogItems.get(itemPosition).postTitle != null) {
                    holder.titleTextView.setText(mBlogItems.get(itemPosition).postTitle.toLowerCase());
                }
                holder.link = mBlogItems.get(itemPosition).guid;
                if(mBlogItems.get(itemPosition).id!=null){
                    holder.articleId=mBlogItems.get(itemPosition).id;
                }
            } else {
                initHeaderUi(holder.view);
            }
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
            private final int viewType;
            private final View view;
            CustomNetworkImageView imageView;
            TextView titleTextView;
            public String link;
            private Long articleId;

            public BlogContentHolder(View itemView, int viewType) {
                super(itemView);
                this.viewType = viewType;
                this.view = itemView;
                if(viewType == TYPE_ITEM) {
                    imageView = (CustomNetworkImageView) itemView.findViewById(R.id.iv_content);
                    titleTextView = (TextView) itemView.findViewById(R.id.txt_content);

                    itemView.setOnClickListener(listener);
                }
            }

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("article ","clicked");
                    if(articleId!=null) {
                        createDashBoardEvent(articleId);
                    }
                    if(link != null) {
                        Activity activity = getActivity();
                        if(activity instanceof BuyerDashboardCallbacks) {
                            Bundle bundle = new Bundle();
                            bundle.putString(KeyUtil.KEY_REQUEST_URL, link);
                            ((BuyerDashboardCallbacks) activity).loadFragment(BuyerDashboardActivity.LOAD_FRAGMENT_WEB_VIEW, true, bundle, null, null);
                        }
                    }
                }
            };
        }
    }

    public void createDashBoardEvent(Long articleId){
        Properties properties= MakaanEventPayload.beginBatch();
        switch (mType) {
            case SEARCH:
                properties.put(MakaanEventPayload.CATEGORY , MakaanTrackerConstants.Category.buyerDashboardSavedSearches);
                properties.put(MakaanEventPayload.LABEL, articleId);
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.click);
                break;
            case SHORTLIST:
                properties.put(MakaanEventPayload.CATEGORY , MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, articleId);
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.shortList);
                break;
            case SITE_VISIT:
                properties.put(MakaanEventPayload.CATEGORY , MakaanTrackerConstants.Category.buyerDashboardSiteVisits);
                properties.put(MakaanEventPayload.LABEL, articleId);
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.click);
                break;
            case HOME_LOAN:
                properties.put(MakaanEventPayload.CATEGORY , MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s,",articleId,MakaanTrackerConstants.Label.homeLoan.toString()));
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.click);
                break;
            case UNIT_BOOK:
                properties.put(MakaanEventPayload.CATEGORY , MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s,",articleId,MakaanTrackerConstants.Label.unitBook.toString()));
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.click);
                break;
            case POSSESSION:
                properties.put(MakaanEventPayload.CATEGORY , MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL,  String.format("%s_%s,",articleId,MakaanTrackerConstants.Label.possesion.toString()));
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.click);
                break;
            case REGISTRATION:
                properties.put(MakaanEventPayload.CATEGORY , MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL,  String.format("%s_%s,",articleId,MakaanTrackerConstants.Label.registration.toString()));
                MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.click);
                break;
        }
    }
}
