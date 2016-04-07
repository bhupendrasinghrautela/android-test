package com.makaan.activity.shortlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.makaan.R;
import com.makaan.activity.shortlist.ShortListEnquiredAdapter.Enquiry;
import com.makaan.activity.shortlist.ShortListEnquiredAdapter.EnquiryType;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.buyerjourney.ClientLeadsByGetEvent;
import com.makaan.event.buyerjourney.SiteVisitEventGetEvent;
import com.makaan.event.listing.ListingByIdGetEvent;
import com.makaan.event.locality.LocalityByIdEvent;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.event.suburb.SuburbByIdGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.buyerjourney.ClientLead;
import com.makaan.response.buyerjourney.ClientLead.PropertyRequirement;
import com.makaan.response.buyerjourney.Company;
import com.makaan.service.ClientLeadsService;
import com.makaan.service.ListingService;
import com.makaan.service.LocalityService;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.ProjectService;
import com.makaan.service.SuburbService;
import com.makaan.util.ErrorUtil;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.Bind;

/**
 * Created by makaanuser on 2/2/16.
 */
public class ShortlistEnquiredFragment extends MakaanBaseFragment implements ShortListEnquiredAdapter.OnSiteVisitRequestLitener{
    @Bind(R.id.enquired_recycler_view)
    RecyclerView enquiredRecyclerView;

    private LinkedHashMap<Long, Enquiry> mEnquiryHashMap;
    private ShortListEnquiredAdapter mAdapter;
    private int position;
    private ShortListCallback callback;
    private ArrayList<Long> mSellerIds;
    private Long enquiryId;


    @Override
    protected int getContentViewId() {
        return R.layout.layout_fragment_enquired;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        enquiredRecyclerView.setLayoutManager(mLayoutManager);
        ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class)).requestClientLeads();
        showProgress();
        // enquiredRecyclerView.setAdapter(new ShortListEnquiredAdapter(getActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Subscribe
    public void onResults(ClientLeadsByGetEvent clientLeadsByGetEvent) {
        if (!isVisible()) {
            return;
        }
        if (clientLeadsByGetEvent == null || clientLeadsByGetEvent.error != null) {
            if (clientLeadsByGetEvent != null && clientLeadsByGetEvent.error.msg != null) {
                showNoResults(clientLeadsByGetEvent.error.msg);
            } else {
                showNoResults();
            }
            return;
        }
        if (mEnquiryHashMap == null) {
            mEnquiryHashMap = new LinkedHashMap<>();
        }

        if(clientLeadsByGetEvent.results!=null && clientLeadsByGetEvent.results.size()>0) {
            mAdapter = new ShortListEnquiredAdapter(getActivity(),this);
            mSellerIds = new ArrayList<>();
            for (ClientLead clientLead : clientLeadsByGetEvent.results) {
                if(clientLead.propertyRequirements != null) {
                    for (PropertyRequirement propertyRequirement : clientLead.propertyRequirements) {
                        if (propertyRequirement != null) {
                            Enquiry enquiry = mAdapter.new Enquiry();
                            enquiry.id = propertyRequirement.id;
                            enquiry.propertyRequirement = propertyRequirement;
                            if (propertyRequirement.listingId != null) {
                                enquiry.type = EnquiryType.LISTING;
                                ((ListingService) MakaanServiceFactory.getInstance().getService(ListingService.class)).getListingDetailForEnquiry(propertyRequirement.listingId);
                            } else if (propertyRequirement.projectId != null) {
                                enquiry.type = EnquiryType.PROJECT;
                                ((ProjectService) MakaanServiceFactory.getInstance().getService(ProjectService.class)).getProjectByIdEnquiry(propertyRequirement.projectId);
                            } else if (propertyRequirement.localityId != null) {
                                enquiry.type = EnquiryType.LOCALITY;
                                // todo change locality call
                                ((LocalityService) MakaanServiceFactory.getInstance().getService(LocalityService.class)).getLocalityByIdForEnquiry(propertyRequirement.localityId);
                            } else if (propertyRequirement.suburbId != null) {
                                enquiry.type = EnquiryType.SUBURB;
                                // todo change to suburb call
                                ((SuburbService) MakaanServiceFactory.getInstance().getService(SuburbService.class)).getSuburbByIdForEnquiry(propertyRequirement.suburbId);
                            } else if (propertyRequirement.bedroom != null) {
                                enquiry.type = EnquiryType.BEDROOM;
                            } else if (propertyRequirement.minBudget != null || propertyRequirement.maxBudget != null) {
                                enquiry.type = EnquiryType.BUDGET;
                            } else if (propertyRequirement.minSize != null || propertyRequirement.maxSize != null) {
                                enquiry.type = EnquiryType.SIZE;
                            } else if (propertyRequirement.latitude != null || propertyRequirement.longitude != null) {
                                enquiry.type = EnquiryType.LAT_LON;
                            } else if (propertyRequirement.radiusKm != null) {
                                enquiry.type = EnquiryType.RADIUS;
                            } else {
                                enquiry.type = EnquiryType.SELLER;
                            }
                            if (enquiry != null) {
                                enquiry.companyId = clientLead.companyId;
                                enquiry.leadId = propertyRequirement.leadId;
                                mEnquiryHashMap.put(enquiry.id, enquiry);
                            }
                        }
                    }
                }
                mSellerIds.add(clientLead.companyId);
            }
            ((ClientLeadsService) MakaanServiceFactory.getInstance().getService(ClientLeadsService.class)).requestClientLeadCompanies(mSellerIds);
            callback.updateCount(position, mEnquiryHashMap.size());
            mAdapter.setData(mEnquiryHashMap);
            enquiredRecyclerView.setVisibility(View.VISIBLE);
            enquiredRecyclerView.setAdapter(mAdapter);
            showContent();
        } else {
            showNoResults(ErrorUtil.getErrorMessageId(ErrorUtil.STATUS_CODE_NO_CONTENT, false));
        }


    }

    @Subscribe
    public void onResultsListing(ListingByIdGetEvent listingByIdGetEvent) {
        if (!isVisible()) {
            return;
        }
        if (listingByIdGetEvent.listingDetail != null && listingByIdGetEvent.error == null) {
            if (mEnquiryHashMap == null) {
                mEnquiryHashMap = new LinkedHashMap<>();
            }
            for(Enquiry enquiry : mEnquiryHashMap.values()) {
                if(enquiry.propertyRequirement != null && enquiry.propertyRequirement.listingId != null) {
                    if(enquiry.propertyRequirement.listingId.equals(listingByIdGetEvent.listingDetail.id)) {
                        enquiry.listingDetail = listingByIdGetEvent.listingDetail;
                    }
                }
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void onResultsProject(ProjectByIdEvent projectByIdEvent) {
        if (!isVisible()) {
            return;
        }
        if (projectByIdEvent.project != null && projectByIdEvent.error == null) {
            if (mEnquiryHashMap == null) {
                mEnquiryHashMap = new LinkedHashMap<>();
            }

            for(Enquiry enquiry : mEnquiryHashMap.values()) {
                if(enquiry.propertyRequirement != null && enquiry.propertyRequirement.projectId != null) {
                    if(enquiry.propertyRequirement.projectId.equals(projectByIdEvent.project.projectId)) {
                        enquiry.project = projectByIdEvent.project;
                    }
                }
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void onResultsProject(LocalityByIdEvent localityByIdEvent) {
        if (!isVisible()) {
            return;
        }
        if (localityByIdEvent.locality != null && localityByIdEvent.error == null) {
            if (mEnquiryHashMap == null) {
                mEnquiryHashMap = new LinkedHashMap<>();
            }

            for(Enquiry enquiry : mEnquiryHashMap.values()) {
                if(enquiry.propertyRequirement != null && enquiry.propertyRequirement.localityId != null) {
                    if(enquiry.propertyRequirement.localityId.equals(localityByIdEvent.locality.localityId)) {
                        enquiry.locality = localityByIdEvent.locality;
                    }
                }
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void onResultsProject(SuburbByIdGetEvent suburbByIdGetEvent) {
        if (!isVisible()) {
            return;
        }
        if (suburbByIdGetEvent.suburb != null && suburbByIdGetEvent.error == null) {
            if (mEnquiryHashMap == null) {
                mEnquiryHashMap = new LinkedHashMap<>();
            }

            for(Enquiry enquiry : mEnquiryHashMap.values()) {
                if(enquiry.propertyRequirement != null && enquiry.propertyRequirement.suburbId != null) {
                    if(enquiry.propertyRequirement.suburbId.equals(suburbByIdGetEvent.suburb.id)) {
                        enquiry.suburb = suburbByIdGetEvent.suburb;
                    }
                }
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void onCompanyResult(ArrayList<Company> companies) {
        if (!isVisible()) {
            return;
        }
    if (companies != null && companies.size() > 0) {
            for (Company company : companies) {
                for(Enquiry enquiry : mEnquiryHashMap.values()) {
                    if(enquiry.companyId != null && enquiry.companyId.equals(company.id)) {
                        enquiry.company = company;
                    }
                }
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe
    public void showSitevisitMessage(SiteVisitEventGetEvent siteVisitEventGetEvent) {
        if (!isVisible()) {
            return;
        }
        if (siteVisitEventGetEvent != null) {
            if (siteVisitEventGetEvent.error != null) {
                Toast.makeText(getActivity(), siteVisitEventGetEvent.error.msg
                        , Toast.LENGTH_SHORT).show();
            }
            else if(siteVisitEventGetEvent.isScheduled){
                /*----- track events--------*/
                Properties properties = MakaanEventPayload.beginBatch();
                properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerDashboard);
                properties.put(MakaanEventPayload.LABEL, String.format("%s_%s", enquiryId,
                        MakaanTrackerConstants.Label.siteVisitOk));
                MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.shortlistEnquire);
                /*-------------------------*/
                Toast.makeText(getActivity(),getString(R.string.site_visit_scheduled),Toast.LENGTH_LONG).show();
            }
        }
    }

    public void bindView(ShortListCallback shortListCallback, int i) {
        position = i;
        callback = shortListCallback;
    }

    @Override
    public void setEnquiryId(Long id) {
        enquiryId=id;
    }
}
