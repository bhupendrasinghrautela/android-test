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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.MakaanBuyerApplication;
import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.cache.MasterDataCache;
import com.makaan.request.selector.Selector;
import com.makaan.response.serp.FilterGroup;
import com.makaan.response.serp.RangeFilter;
import com.makaan.response.serp.TermFilter;
import com.makaan.service.listing.ListingService;
import com.makaan.adapter.listing.FiltersViewAdapter;
import com.makaan.ui.view.ExpandableHeightGridView;
import com.makaan.util.AppBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/8/16.
 */
public class FiltersDialogFragment extends DialogFragment implements View.OnClickListener {
    ExpandableHeightGridView mBedsLayoutGridView;
    ExpandableHeightGridView mPropertyTypeLayoutGridView;
    ExpandableHeightGridView mBathroomLayoutGridView;
    ExpandableHeightGridView mReadyToMoveLayoutGridView;
    ExpandableHeightGridView mSaleTypeLayoutGridView;
    ExpandableHeightGridView mListedByLayoutGridView;
    ExpandableHeightGridView mFurnishingLayoutGridView;
    ExpandableHeightGridView mBuildersLayoutGridView;

    @Bind(R.id.fragment_dialog_filters_back_buttom)
    Button mBackButton;
    @Bind(R.id.fragment_dialog_filters_submit_button)
    Button mApplyButton;
    @Bind(R.id.fragment_dialog_filters_reset_buttom)
    Button mResetButton;
    @Bind(R.id.fragment_dialog_filters_filter_linear_layout)
    LinearLayout mFiltersLinearLayout;

    private static final String FILTER_BEDS = "Beds";
    private static final String FILTER_PROPERTY_TYPE = "Property Type";
    private static final String FILTER_BATHROOMS = "Bathroom";
    private static final String FILTER_READY_TO_MOVE = "ready to move (age by property)";
    private static final String FILTER_LISTED_BY = "listed by";
    private static final String FILTER_FURNISHING_STATUS = "furnishing status";
    private static final String FILTER_TOP_BUILDERS = "top builders";
    private static final String NEW_RESALE = "new/resale";
    private static final String BUDGET = "Budget";


    public static FiltersDialogFragment init() {
        return new FiltersDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_filters, container, false);

        AppBus.getInstance().register(this); //TODO: move to base fragment
        ButterKnife.bind(this, view);

        mApplyButton.setOnClickListener(this);


        if (MakaanBuyerApplication.isBuySearch) {
            populateFilters(MasterDataCache.getInstance().getAllBuyFilterGroups());
        } else {
            populateFilters(MasterDataCache.getInstance().getAllRentFilterGroups());
        }


        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }


    private void populateFilters(ArrayList<FilterGroup> filterGroups) {
        if (filterGroups != null && filterGroups.size() > 0) {
            for (FilterGroup filterGroup : filterGroups) {
                GridView gridView = null;
                List<TermFilter> term = null;
                List<RangeFilter> range = null;
                View view = null;
                switch (filterGroup.layoutType) {
                    case FiltersViewAdapter.TOGGLE_BUTTON:
                        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_filters_item_layout, mFiltersLinearLayout, false);
                        ((TextView)view.findViewById(R.id.fragment_dialog_filters_item_layout_display_text_view)).setText(filterGroup.displayName);
                        mFiltersLinearLayout.addView(view);
                        gridView = (GridView) view.findViewById(R.id.fragment_dialog_filters_item_layout_grid_view);
                        term = filterGroup.termFilterValues;
                        range = filterGroup.rangeFilterValues;
                        break;
                    case FiltersViewAdapter.CHECKBOX:
                        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_filters_item_layout, mFiltersLinearLayout, false);
                        ((TextView)view.findViewById(R.id.fragment_dialog_filters_item_layout_display_text_view)).setText(filterGroup.displayName);
                        mFiltersLinearLayout.addView(view);
                        gridView = (GridView) view.findViewById(R.id.fragment_dialog_filters_item_layout_grid_view);
                        gridView.setNumColumns(1);
                        term = filterGroup.termFilterValues;
                        range = filterGroup.rangeFilterValues;
                        break;
                    case FiltersViewAdapter.RADIO_BUTTON:
                        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_filters_item_layout, mFiltersLinearLayout, false);
                        ((TextView)view.findViewById(R.id.fragment_dialog_filters_item_layout_display_text_view)).setText(filterGroup.displayName);
                        mFiltersLinearLayout.addView(view);
                        gridView = (GridView) view.findViewById(R.id.fragment_dialog_filters_item_layout_grid_view);
                        gridView.setNumColumns(1);
                        term = filterGroup.termFilterValues;
                        range = filterGroup.rangeFilterValues;
                        break;
                    case FiltersViewAdapter.SEEKBAR:
                        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_filters_item_layout, mFiltersLinearLayout, false);
                        ((TextView)view.findViewById(R.id.fragment_dialog_filters_item_layout_display_text_view)).setText(filterGroup.displayName);
                        mFiltersLinearLayout.addView(view);
                        gridView = (GridView) view.findViewById(R.id.fragment_dialog_filters_item_layout_grid_view);
                        gridView.setNumColumns(1);
                        term = filterGroup.termFilterValues;
                        range = filterGroup.rangeFilterValues;
                        break;
                }
                switch (filterGroup.displayName) {
                    case FILTER_BEDS:
                        mBedsLayoutGridView = (ExpandableHeightGridView) gridView;
                        if(view != null) {
                            ((ImageView) view.findViewById(R.id.fragment_dialog_filters_item_layout_image_view)).setImageResource(R.drawable.bed);
                        }
                        break;
                    case FILTER_PROPERTY_TYPE:
                        mPropertyTypeLayoutGridView = (ExpandableHeightGridView) gridView;
                        if(view != null) {
                            // TODO change image to property type
                            ((ImageView) view.findViewById(R.id.fragment_dialog_filters_item_layout_image_view)).setImageResource(R.drawable.floor);
                        }
                    case FILTER_BATHROOMS:
                        if(view != null) {
                            ((ImageView) view.findViewById(R.id.fragment_dialog_filters_item_layout_image_view)).setImageResource(R.drawable.shower);
                        }
                        break;
                    case FILTER_READY_TO_MOVE:
                        if(view != null) {
                            ((ImageView) view.findViewById(R.id.fragment_dialog_filters_item_layout_image_view)).setImageResource(R.drawable.shower);
                        }
                        break;
                    case FILTER_LISTED_BY:
                        if(view != null) {
                            // TODO builder image is incorrect
                            ((ImageView) view.findViewById(R.id.fragment_dialog_filters_item_layout_image_view)).setImageResource(R.drawable.builder);
                        }
                        break;
                    case FILTER_FURNISHING_STATUS:
                        if(view != null) {
                            // TODO furnishing status image is incorrect
                            ((ImageView) view.findViewById(R.id.fragment_dialog_filters_item_layout_image_view)).setImageResource(R.drawable.builder);
                        }
                        break;
                    case FILTER_TOP_BUILDERS:
                        if(view != null) {
                            // TODO top builder image is incorrect
                            ((ImageView) view.findViewById(R.id.fragment_dialog_filters_item_layout_image_view)).setImageResource(R.drawable.floor);
                        }
                        break;
                    case NEW_RESALE:
                        if(view != null) {
                            // TODO change image to new/resale icon
                            ((ImageView) view.findViewById(R.id.fragment_dialog_filters_item_layout_image_view)).setImageResource(R.drawable.floor);
                        }
                    case BUDGET:
                        if(view != null) {
                            ((ImageView) view.findViewById(R.id.fragment_dialog_filters_item_layout_image_view)).setImageResource(R.drawable.money_icon);
                        }
                }
                if (gridView != null) {
                    ((ExpandableHeightGridView) gridView).setExpanded(true);
                    FiltersViewAdapter adapter = new FiltersViewAdapter(getActivity(), term, range, filterGroup.layoutType);
                    gridView.setAdapter(adapter);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
//        Selector request =
        if (v == mApplyButton) {
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
        if(getActivity() instanceof FilterDialogFragmentCallback) {
            ((FilterDialogFragmentCallback)getActivity()).dialogDismissed();;
        }
    }

    @OnClick(R.id.fragment_dialog_filters_back_buttom)
    public void onBackPressed(View view) {
        getActivity().onBackPressed();
    }

    public interface FilterDialogFragmentCallback {
        public void dialogDismissed();
    }
}
