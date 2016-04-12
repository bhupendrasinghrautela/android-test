package com.makaan.activity.shortlist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.toolbox.FadeInNetworkImageView;
import com.makaan.R;
import com.makaan.constants.LeadPhaseConstants;
import com.makaan.response.buyerjourney.ClientEvent;
import com.makaan.ui.CustomNetworkImageView;
import com.makaan.ui.view.CustomRatingBar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortListEnquiredViewHolder extends RecyclerView.ViewHolder {
    public CustomNetworkImageView mMainImage;
    public TextView mAddress;
    public TextView mName;
    public TextView mSellerText;
    public CustomRatingBar mRating;
    public CircleImageView mSellerImage;
    public TextView mRequestSiteVisit;
    private int position;
    private ScheduleSiteVisit listener;
    private int mPhaseId;

    public void setPhaseAndCompanyId(int phaseId, Long companyId) {
        if(companyId != null && companyId.equals(499L)) {
            mRequestSiteVisit.setText(R.string.request_site_visit);
            mRequestSiteVisit.setEnabled(false);
        } else if(phaseId == LeadPhaseConstants.LEAD_PHASE_SITE_VIST) {
            mRequestSiteVisit.setText(R.string.site_visit_already_requested);
            mRequestSiteVisit.setEnabled(false);
        } else {
            mRequestSiteVisit.setText(R.string.request_site_visit);
            mRequestSiteVisit.setEnabled(true);
        }
    }

    public interface ScheduleSiteVisit{
        void onSiteVisitClicked(int position);

        void onItemClick(int position);
    }

    public ShortListEnquiredViewHolder(View itemView) {
        super(itemView);
        mMainImage = (CustomNetworkImageView) itemView.findViewById(R.id.iv_content);
        mAddress = (TextView) itemView.findViewById(R.id.address);
        mName = (TextView) itemView.findViewById(R.id.name);
        mSellerImage = (CircleImageView) itemView.findViewById(R.id.seller_image_view);
        mSellerText = (TextView) itemView.findViewById(R.id.seller_logo_text_view);
        mRating = (CustomRatingBar) itemView.findViewById(R.id.seller_rating);
        mRequestSiteVisit = (TextView) itemView.findViewById(R.id.txt_content);
        mRequestSiteVisit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPhaseId != LeadPhaseConstants.LEAD_PHASE_SITE_VIST) {
                    listener.onSiteVisitClicked(position);
                }
            }
        });
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    public void setPosition(int position){
        this.position = position;
    }

    public void setListener(ScheduleSiteVisit listener){
        this.listener = listener;
    }
}
