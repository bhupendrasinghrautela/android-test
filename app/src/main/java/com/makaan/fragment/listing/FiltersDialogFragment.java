package com.makaan.fragment.listing;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.activity.listing.SerpActivity;
import com.makaan.adapter.listing.FiltersViewAdapter;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.pojo.SerpObjects;
import com.makaan.pojo.SerpRequest;
import com.makaan.request.selector.Selector;
import com.makaan.response.serp.FilterGroup;
import com.makaan.ui.view.ExpandableHeightGridView;
import com.makaan.util.AppBus;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/8/16.
 */
public class FiltersDialogFragment extends DialogFragment {
    List<ExpandableHeightGridView> mFilterGridViews = new ArrayList<>();

    @Bind(R.id.fragment_dialog_filters_filter_linear_layout)
    LinearLayout mFiltersLinearLayout;


    public static FiltersDialogFragment init() {
        return new FiltersDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_filters, container, false);

        AppBus.getInstance().register(this); //TODO: move to base fragment
        ButterKnife.bind(this, view);


        try {
            ArrayList<FilterGroup> filterGroups = SerpObjects.getFilterGroups(getActivity());
            populateFilters(getClonedFilterGroups(filterGroups));
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullscreen_dialog_fragment_theme);
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

    private ArrayList<FilterGroup> getClonedFilterGroups(ArrayList<FilterGroup> filterGroups) throws CloneNotSupportedException {
        ArrayList<FilterGroup> group = new ArrayList<>(filterGroups.size());
        for (FilterGroup filter : filterGroups) {
            group.add(filter.clone());
        }
        return group;
    }

    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getDecorView().setPadding(0,0,0,0);
        return dialog;
    }*/


    private void populateFilters(ArrayList<FilterGroup> filterGroups) {
        if (filterGroups != null && filterGroups.size() > 0) {
            for (FilterGroup filterGroup : filterGroups) {
                if (filterGroup.displayOrder < 0) {
                    continue;
                }

                View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_filters_item_layout, mFiltersLinearLayout, false);
                ((TextView) view.findViewById(R.id.fragment_dialog_filters_item_layout_display_text_view)).setText(filterGroup.displayName);
                GridView gridView = (GridView) view.findViewById(R.id.fragment_dialog_filters_item_layout_grid_view);

                mFilterGridViews.add((ExpandableHeightGridView) gridView);

                if (mFiltersLinearLayout.getChildCount() >= filterGroup.displayOrder) {
                    mFiltersLinearLayout.addView(view, filterGroup.displayOrder - 1);
                } else {
                    mFiltersLinearLayout.addView(view, mFiltersLinearLayout.getChildCount());
                }

                if (filterGroup.layoutType != FiltersViewAdapter.TOGGLE_BUTTON) {
                    gridView.setNumColumns(1);
                }/* else {
                    gridView.setNumColumns(filterGroup.termFilterValues.size());
                }*/

                if(filterGroup.layoutType != FiltersViewAdapter.SINGLE_CHECKBOX) {
                    int id = this.getResources().getIdentifier(filterGroup.imageName, "drawable", "com.makaan");
                    if (id != 0) {
                        ((ImageView) view.findViewById(R.id.fragment_dialog_filters_item_layout_image_view)).setImageResource(id);
                    }
                } else {
                    view.findViewById(R.id.fragment_dialog_filters_item_layout_left_linear_layout).setVisibility(View.GONE);
                }
                if (gridView != null) {
                    ((ExpandableHeightGridView) gridView).setExpanded(true);
                    FiltersViewAdapter adapter = new FiltersViewAdapter(getActivity(), filterGroup, filterGroup.layoutType);
                    gridView.setAdapter(adapter);
                }
            }
        }
    }

    @OnClick(R.id.fragment_dialog_filters_submit_button)
    public void onSubmitClick(View v) {
        ArrayList<FilterGroup> filterGroups = SerpObjects.getFilterGroups(getActivity());
        Selector selector = SerpObjects.getSerpSelector(getActivity());

        if(filterGroups != null && selector != null) {
            MakaanEventPayload.beginBatch();
            for (ExpandableHeightGridView gridView : mFilterGridViews) {
                FiltersViewAdapter adapter = (FiltersViewAdapter) (gridView.getAdapter());
                adapter.applyFilters(selector, filterGroups);
            }
            MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.selectFilterMore);
        }
        SerpRequest request = new SerpRequest(SerpActivity.TYPE_FILTER);
        request.launchSerp(getActivity());
        dismiss();
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
        if (getActivity() instanceof FilterDialogFragmentCallback) {
            ((FilterDialogFragmentCallback) getActivity()).dialogDismissed();
        }
    }

    @OnClick(R.id.fragment_dialog_filters_back_button)
    public void onBackClicked(View view) {
        dismiss();
    }

    @OnClick(R.id.fragment_dialog_filters_reset_buttom)
    public void onResetClicked(View view) {
        for (ExpandableHeightGridView gridView : mFilterGridViews) {
            FiltersViewAdapter adapter = (FiltersViewAdapter) (gridView.getAdapter());
            adapter.reset();
        }
    }

    public interface FilterDialogFragmentCallback {
        public void dialogDismissed();
    }
}
