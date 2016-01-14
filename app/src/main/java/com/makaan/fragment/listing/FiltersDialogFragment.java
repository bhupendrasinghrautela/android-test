package com.makaan.fragment.listing;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.cache.MasterDataCache;
import com.makaan.request.selector.Selector;
import com.makaan.response.serp.FilterGroup;
import com.makaan.service.ListingService;
import com.makaan.adapter.listing.FiltersViewAdapter;
import com.makaan.ui.view.ExpandableHeightGridView;
import com.makaan.util.AppBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/8/16.
 */
public class FiltersDialogFragment extends DialogFragment implements View.OnClickListener {
    @Bind(R.id.activity_filters_layout_beds_grid_view)
    ExpandableHeightGridView mBedsLayoutGridView;
    @Bind(R.id.activity_filters_layout_property_type_grid_view)
    ExpandableHeightGridView mPropertyTypeLayoutGridView;
    @Bind(R.id.activity_filters_layout_bathroom_grid_view)
    ExpandableHeightGridView mBathroomLayoutGridView;
    @Bind(R.id.activity_filters_layout_ready_to_move_grid_view)
    ExpandableHeightGridView mReadyToMoveLayoutGridView;
    @Bind(R.id.activity_filters_layout_sale_type_grid_view)
    ExpandableHeightGridView mSaleTypeLayoutGridView;
    @Bind(R.id.activity_filters_layout_listed_by_grid_view)
    ExpandableHeightGridView mListedByLayoutGridView;
    @Bind(R.id.activity_filters_layout_furnishing_grid_view)
    ExpandableHeightGridView mFurnishingLayoutGridView;
    @Bind(R.id.activity_filters_layout_builders_grid_view)
    ExpandableHeightGridView mBuildersLayoutGridView;

    @Bind(R.id.activity_filters_cancel_button)
    Button mCancelButton;

    @Bind(R.id.activity_filters_view_properties_button)
    Button mViewPropertiesButton;

    private static final String FILTER_BEDS = "Beds";
    private static final String FILTER_PROPERTY_TYPE = "Property Type";

    public static FiltersDialogFragment init() {
        return new FiltersDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_filters, container, false);

        AppBus.getInstance().register(this); //TODO: move to base fragment
        ButterKnife.bind(this, view);

        mCancelButton.setOnClickListener(this);
        mViewPropertiesButton.setOnClickListener(this);


        if (MakaanBuyerApplication.isBuySearch) {
            populateFilters(MasterDataCache.getInstance().getAllBuyFilterGroups());
        } else {
            populateFilters(MasterDataCache.getInstance().getAllRentFilterGroups());
        }


        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    private void populateFilters(ArrayList<FilterGroup> filterGroups) {
        if (filterGroups != null && filterGroups.size() > 0) {
            for (FilterGroup filterGroup : filterGroups) {
                GridView gridView = null;
                int type = -1;
                List list = null;
                switch (filterGroup.displayName) {
                    case FILTER_BEDS:
                        gridView = mBedsLayoutGridView;
                        type = FiltersViewAdapter.CHECKBOX_RECTANGLE;
                        list = filterGroup.termFilterValues;
                        break;
                    case FILTER_PROPERTY_TYPE:
                        gridView = mPropertyTypeLayoutGridView;
                        type = FiltersViewAdapter.CHECKBOX_NORMAL;
                        list = filterGroup.termFilterValues;
                        break;
                }
                if (gridView != null) {
                    ((ExpandableHeightGridView) gridView).setExpanded(true);
                    FiltersViewAdapter adapter = new FiltersViewAdapter(getActivity(), list, type);
                    gridView.setAdapter(adapter);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
//        Selector request =
        if (v == mViewPropertiesButton) {
            FiltersViewAdapter adapter = (FiltersViewAdapter) mBedsLayoutGridView.getAdapter();
            List<String> list = adapter.getSelectedOptions();
            addFilters(list, MakaanBuyerApplication.serpSelector);

            adapter = (FiltersViewAdapter) mPropertyTypeLayoutGridView.getAdapter();
            list = adapter.getSelectedOptions();
            addFilters(list, MakaanBuyerApplication.serpSelector);
            MakaanBuyerApplication.serpSelector.sort("price", "desc");
            new ListingService().handleSerpRequest(MakaanBuyerApplication.serpSelector);
            dismiss();
        }
    }

    private void addFilters(List<String> list, Selector request) {
        if (list.size() > 1) {
            String fieldName = list.get(0);
            list.remove(0);
            for (String val : list) {
                request.term(fieldName, val);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppBus.getInstance().unregister(this);
    }
}
