package com.makaan.fragment.property;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.crashlytics.android.Crashlytics;
import com.makaan.R;
import com.makaan.activity.lead.LeadFormActivity;
import com.makaan.activity.listing.PropertyDetailFragment;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.constants.ScreenNameConstants;
import com.makaan.fragment.project.ProjectFragment;
import com.makaan.jarvis.BaseJarvisActivity;
import com.makaan.network.CustomImageLoaderListener;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.pojo.SellerCard;
import com.makaan.ui.view.CustomRatingBar;
import com.makaan.util.AppBus;
import com.makaan.util.ImageUtils;
import com.makaan.util.KeyUtil;
import com.makaan.util.PermissionManager;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by aishwarya on 03/02/16.
 */
public class ViewSellersDialogFragment extends DialogFragment {

    @Bind(R.id.fragment_dialog_contact_sellers_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.fragment_dialog_contact_sellers_select_all_checkbox)
    CheckBox selectSeller;
    @Bind(R.id.fragment_dialog_contact_sellers_submit_button)
    Button mSubmitButton;
    private boolean mAlreadyLoaded=false;


    @OnCheckedChanged(R.id.fragment_dialog_contact_sellers_select_all_checkbox)
    public void checkChanged(){
        if(mSellerCards ==null){
            dismiss();
            return;
        }
        if(selectSeller.isChecked()){
            Bundle bundle=this.getArguments();
            for(SellerCard sellerCard : mSellerCards){
                if (ScreenNameConstants.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity)getActivity()).getScreenName())) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                    properties.put(MakaanEventPayload.LABEL, bundle.get(MakaanEventPayload.PROJECT_ID) + "_" + sellerCard.sellerId + "_all");
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectViewOtherSellers);
                }
                else if (ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity)getActivity()).getScreenName())) {
                    Properties properties = MakaanEventPayload.beginBatch();
                    properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                    properties.put(MakaanEventPayload.LABEL, bundle.get(MakaanEventPayload.PROJECT_ID) + "_" + sellerCard.sellerId + "_all");
                    MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyViewOtherSellers);
                }
                sellerCard.isChecked = true;
            }
            mChecked = mSellerCards.size();
        }
        else{
            for(SellerCard sellerCard : mSellerCards){
                sellerCard.isChecked = false;
            }
            mChecked = 0;
        }
        if(mAdapter!=null) {
            mAdapter.updateData();
        }
        setButtonText();
    }

    @OnClick(R.id.fragment_dialog_contact_sellers_back_button)
    public void onBackClicked(View view) {
        dismiss();
    }

    @OnClick(R.id.fragment_dialog_contact_sellers_submit_button)
    public void openLeadForm(){
        Intent intent = new Intent(getActivity(), LeadFormActivity.class);
        boolean assist=false;
        ArrayList<Integer> ids = new ArrayList<>();
        if(mSellerCards != null) {
            for (SellerCard sellerCard : mSellerCards) {
                if (sellerCard.isChecked) {
                    ids.add(sellerCard.sellerId.intValue());
                }
                if (sellerCard.assist) {
                    assist = true;
                }
            }
        }
        try {
            //Todo add new multi lead fragment
            Bundle bundle=getArguments();
            //SellerCard sellerCard = mSellerCards.get(3);
            /*intent.putExtra("name", sellerCard.name);
            if(sellerCard.rating != null) {
                intent.putExtra("score", sellerCard.rating.toString());
            }
            else{
                intent.putExtra("score","0");
            }*/
            //intent.putExtra("phone",sellerCard.contactNo);//todo: not available in pojo
            //intent.putExtra("id", sellerCard.sellerId.toString());

            /*------------lead form ---------open ---track-----event*/
            Properties properties = MakaanEventPayload.beginBatch();
            if(bundle!=null) {


                if(!TextUtils.isEmpty(bundle.getString(KeyUtil.SOURCE_LEAD_FORM)))
                {
                    if(PropertyDetailFragment.class.getName().equalsIgnoreCase(bundle.getString(KeyUtil.SOURCE_LEAD_FORM))){
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                        if (!TextUtils.isEmpty(bundle.getString(KeyUtil.SALE_TYPE_LEAD_FORM)) &&
                                "buy".equalsIgnoreCase(bundle.getString(KeyUtil.SALE_TYPE_LEAD_FORM))) {
                            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY,
                                    bundle.getLong(KeyUtil.PROPERTY_Id_LEAD_FORM)));
                        } else if(!TextUtils.isEmpty(bundle.getString(KeyUtil.SALE_TYPE_LEAD_FORM)) &&
                                "rent".equalsIgnoreCase(bundle.getString(KeyUtil.SALE_TYPE_LEAD_FORM))){
                            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.RENT,
                                    bundle.getLong(KeyUtil.PROPERTY_Id_LEAD_FORM)));
                        }
                        else {
                            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", "",""));
                        }

                    }
                    else if(ProjectFragment.class.getName().equalsIgnoreCase(bundle.getString(KeyUtil.SOURCE_LEAD_FORM))){
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.project);
                        if (!TextUtils.isEmpty(bundle.getString(KeyUtil.SALE_TYPE_LEAD_FORM)) &&
                                "buy".equalsIgnoreCase(bundle.getString(KeyUtil.SALE_TYPE_LEAD_FORM))) {
                            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.BUY,
                                    bundle.getLong(KeyUtil.PROJECT_ID_LEAD_FORM)));
                        } else if(!TextUtils.isEmpty(bundle.getString(KeyUtil.SALE_TYPE_LEAD_FORM)) &&
                                "rent".equalsIgnoreCase(bundle.getString(KeyUtil.SALE_TYPE_LEAD_FORM))){
                            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", ScreenNameConstants.RENT,
                                    bundle.getLong(KeyUtil.PROJECT_ID_LEAD_FORM)));
                        }
                        else {
                            properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", "",""));
                        }
                    }
                }
            }
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.leadFormOpen);
            /*--------------------------------------------------------------event*/

            intent.putIntegerArrayListExtra("multipleSellerIds", ids);
            if(bundle!=null){
                intent.putExtra(KeyUtil.CITY_NAME_LEAD_FORM,bundle.getString(KeyUtil.CITY_NAME_LEAD_FORM));
                intent.putExtra(KeyUtil.CITY_ID_LEAD_FORM, bundle.getLong(KeyUtil.CITY_ID_LEAD_FORM));
                intent.putExtra(KeyUtil.LISTING_ID, bundle.getLong(KeyUtil.LISTING_ID));
                intent.putExtra(KeyUtil.LOCALITY_LEAD_FORM, bundle.getString(KeyUtil.LOCALITY_LEAD_FORM));
                intent.putExtra(KeyUtil.AREA_LEAD_FORM, bundle.getString(KeyUtil.AREA_LEAD_FORM));
                intent.putExtra(KeyUtil.BHK_UNIT_TYPE, bundle.getString(KeyUtil.BHK_UNIT_TYPE));
                intent.putExtra(KeyUtil.LOCALITY_ID_LEAD_FORM, bundle.getLong(KeyUtil.LOCALITY_ID_LEAD_FORM));
                intent.putExtra(KeyUtil.SOURCE_LEAD_FORM, bundle.getString(KeyUtil.SOURCE_LEAD_FORM));
                intent.putExtra(KeyUtil.PROJECT_LEAD_FORM, bundle.getString(KeyUtil.PROJECT_LEAD_FORM));
                intent.putExtra(KeyUtil.PROJECT_ID_LEAD_FORM, bundle.getLong(KeyUtil.PROJECT_ID_LEAD_FORM));
                intent.putExtra(KeyUtil.PROJECT_NAME_LEAD_FORM, bundle.getString(KeyUtil.PROJECT_NAME_LEAD_FORM));
                intent.putExtra(KeyUtil.SALE_TYPE_LEAD_FORM, bundle.getString(KeyUtil.SALE_TYPE_LEAD_FORM));
                intent.putExtra("builder", bundle.getString("builder"));
                //intent.putExtra("sellerImageUrl", bundle.getString("sellerImageUrl"));

                if(assist){
                    intent.putExtra("assist", assist);
                }
            }
            getActivity().startActivity(intent);
        }
        catch (NullPointerException e){
            Crashlytics.logException(e);
        }
    }

    private ArrayList<SellerCard> mSellerCards;
    private AllSellersAdapter mAdapter;
    private int mChecked = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_contact_sellers, container, false);

        AppBus.getInstance().register(this); //TODO: move to base fragment
        ButterKnife.bind(this, view);
        if(mSellerCards !=null && mSellerCards.size()!=0) {
            init();
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!mAlreadyLoaded){
             /*--------------------track--------------code-------------*/
            Properties properties1 = MakaanEventPayload.beginBatch();
            properties1.put(MakaanEventPayload.CATEGORY, ScreenNameConstants.SCREEN_NAME_CHOOSE_MULTIPLE_SELLERS_LEAD_FORM);
            properties1.put(MakaanEventPayload.LABEL, ScreenNameConstants.SCREEN_NAME_CHOOSE_MULTIPLE_SELLERS_LEAD_FORM);
            MakaanEventPayload.endBatch(getContext(), MakaanTrackerConstants.Action.screenName);
            /*--------------------------------------------------------*/
            mAlreadyLoaded=true;
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullscreen_dialog_fragment_theme);
    }

    private void init() {
        LayoutManager layoutManager= new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AllSellersAdapter(mSellerCards);
        mRecyclerView.setAdapter(mAdapter);
        mSubmitButton.setText(getString(R.string.contact_top_sellers));
        mSubmitButton.setEnabled(true);
        if(mSellerCards.size()<=3){
            selectSeller.setChecked(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppBus.getInstance().unregister(this);
    }

    public void bindView(ArrayList<SellerCard> sellerCards) {
        Collections.sort(sellerCards);
        mSellerCards = sellerCards;
        int count = 0;
        for(SellerCard sellerCard:mSellerCards){
            if(count <3){
                sellerCard.isChecked = true;
            }
            count++;
        }
        mChecked = 3;
    }

    void onItemClicked(boolean isClicked){
        if(isClicked){
            mChecked ++;
        }
        else{
            mChecked --;
        }
        setButtonText();
    }

    private void setButtonText() {
        if(mChecked == 0){
            mSubmitButton.setEnabled(false);
            selectSeller.setChecked(false);
            mSubmitButton.setText(getString(R.string.contact_sellers));
        }
        else if(mChecked == mSellerCards.size()){
            selectSeller.setChecked(true);
            mSubmitButton.setText(getString(R.string.contact_all_seller));
            mSubmitButton.setEnabled(true);
        }
        else{
            if(mChecked == 1){

                mSubmitButton.setText(getString(R.string.contact) + " " + mChecked + " " + getString(R.string.seller));
            }
            else {
                mSubmitButton.setText(getString(R.string.contact) + " " + mChecked + " " + getString(R.string.sellers));
            }
            mSubmitButton.setEnabled(true);
        }
    }

    public class AllSellersAdapter extends RecyclerView.Adapter<AllSellersAdapter.ViewHolder> {
        private List<SellerCard> sellerCards;
        int width = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_width);
        int height = getResources().getDimensionPixelSize(R.dimen.serp_listing_card_seller_image_view_height);

        public class ViewHolder extends RecyclerView.ViewHolder{
            // each data item is just a string in this case
            CircleImageView mSellerImageView;
            TextView mSellerLogoTextView;
            TextView mSellerTotalProperty;
            TextView mSellerName;
            CustomRatingBar  mSellerRating;
            CheckBox mSellerCheckBox;
            ImageView mCallIcon;
            public ViewHolder(View v) {
                super(v);
                mSellerImageView = (CircleImageView) v.findViewById(R.id.seller_image_view);
                mSellerName = (TextView) v.findViewById(R.id.seller_name_text_view);
                mSellerLogoTextView = (TextView) v.findViewById(R.id.seller_logo_text_view);
                mSellerTotalProperty = (TextView) v.findViewById(R.id.seller_total_property_text_view);
                mSellerRating = (CustomRatingBar)v.findViewById(R.id.seller_rating);
                mSellerCheckBox = (CheckBox)v.findViewById(R.id.fragment_dialog_contact_sellers_select_item_checkbox);
                mCallIcon=(ImageView)v.findViewById(R.id.fragment_dialog_contact_sellers_contact_text_view);
            }

        }

        public AllSellersAdapter(List<SellerCard> sellerCards) {
            this.sellerCards = sellerCards;
        }

        public void updateData(){
            notifyDataSetChanged();
        }

        @Override
        public AllSellersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_dialog_contact_seller_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final SellerCard sellerCard = mSellerCards.get(position);
            if(!TextUtils.isEmpty(sellerCard.imageUrl)) {
                holder.mSellerLogoTextView.setVisibility(View.GONE);
                holder.mSellerImageView.setVisibility(View.VISIBLE);
                MakaanNetworkClient.getInstance().getImageLoader().get(ImageUtils.getImageRequestUrl(sellerCard.imageUrl, width, height, false), new CustomImageLoaderListener() {
                    @Override
                    public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean b) {
                        if(!isVisible()){
                            return;
                        }
                        if (b && imageContainer.getBitmap() == null) {
                            return;
                        }
                        holder.mSellerLogoTextView.setVisibility(View.GONE);
                        holder.mSellerImageView.setVisibility(View.VISIBLE);
                        holder.mSellerImageView.setImageBitmap(imageContainer.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                        showTextAsImage(position,holder);
                    }
                });
            } else {
                showTextAsImage(position,holder);
            }
            if (sellerCard.noOfProperties != null) {
                holder.mSellerTotalProperty.setText(String.valueOf(sellerCard.noOfProperties) + " " + getString(R.string.property));
            }
            if (sellerCard.rating != null) {
                holder.mSellerRating.setRating(sellerCard.rating.floatValue());
            }
            holder.mSellerCheckBox.setOnCheckedChangeListener(null);
            holder.mSellerCheckBox.setChecked(sellerCard.isChecked);
            holder.mSellerCheckBox.setTag(position + 1);
            holder.mCallIcon.setTag(position);
            holder.mSellerCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked && ScreenNameConstants.SCREEN_NAME_PROJECT.equalsIgnoreCase(((BaseJarvisActivity)getActivity()).getScreenName())) {
                        Bundle bundle = getArguments();
                        Properties properties = MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
                        properties.put(MakaanEventPayload.LABEL, bundle.get(MakaanEventPayload.PROJECT_ID) + "_" +
                                mSellerCards.get((int) buttonView.getTag() - 1).sellerId + "_" + buttonView.getTag() + "_" + MakaanTrackerConstants.Label.checked);
                        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectViewOtherSellers);
                    } else if (ScreenNameConstants.SCREEN_NAME_LISTING_DETAIL.equalsIgnoreCase(((BaseJarvisActivity)getActivity()).getScreenName())) {
                        Bundle bundle = getArguments();
                        Properties properties = MakaanEventPayload.beginBatch();
                        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.property);
                        properties.put(MakaanEventPayload.LABEL, bundle.get(MakaanEventPayload.PROJECT_ID) + "_" +
                                mSellerCards.get((int) buttonView.getTag() - 1).sellerId + "_" + buttonView.getTag() + "_" + MakaanTrackerConstants.Label.unChecked);
                        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickPropertyViewOtherSellers);
                    }

                    onItemClicked(isChecked);
                    sellerCard.isChecked = isChecked;
                }
            });

            holder.mCallIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null!=mSellerCards.get((int)view.getTag()).contactNo){

                        if(PermissionManager.isPermissionRequestRequired(getActivity(), Manifest.permission.CALL_PHONE)) {
                            PermissionManager.begin().addRequest(PermissionManager.CALL_PHONE_REQUEST).request(getActivity());
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                                    + mSellerCards.get((int) view.getTag()).contactNo));
                            startActivity(intent);
                        }
                    }
                    else {
                        Toast.makeText(getContext(),getResources().getString(R.string.seller_not_available),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void showTextAsImage(int position,ViewHolder holder) {
            Random random = new Random();
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            int color = ChartUtils.COLORS[position%5];
            drawable.getPaint().setColor(color);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.mSellerLogoTextView.setBackground(drawable);
            } else {
                holder.mSellerLogoTextView.setBackgroundDrawable(drawable);
            }
            if(mSellerCards.get(position).name!=null) {
                if(mSellerCards.get(position).type != null && "broker".equalsIgnoreCase(mSellerCards.get(position).type)) {
                    holder.mSellerName.setText(String.format("%s (%s)", mSellerCards.get(position).name, "agent").toLowerCase());
                } else if(!TextUtils.isEmpty(mSellerCards.get(position).type)) {
                    holder.mSellerName.setText(String.format("%s (%s)", mSellerCards.get(position).name, mSellerCards.get(position).type).toLowerCase());
                } else {
                    holder.mSellerName.setText(mSellerCards.get(position).name);
                }

                holder.mSellerLogoTextView.setText(String.valueOf(mSellerCards.get(position).name.charAt(0)));
            }
        }

        @Override
        public int getItemCount() {
            return sellerCards.size();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode & PermissionManager.CALL_PHONE_REQUEST)
                == PermissionManager.CALL_PHONE_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // todo add functionality if need to as call can now be requested
            } else if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // TODO show message or something
            }
        }
    }
}
