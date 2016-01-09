package com.makaan.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/9/16.
 */
public class SearchBarFragment extends Fragment {

    private static final String TYPE = "type";
    public static final int TYPE_SEARCH = 1;
    public static final int TYPE_PROPERTY_INFO = 2;

    @Bind(R.id.fragment_search_bar_back_button)
    Button mBackButton;
    @Bind(R.id.fragment_search_bar_builder_image_view)
    ImageView mBuilderImageView;
    @Bind(R.id.fragment_search_bar_property_info_linear_layout)
    LinearLayout mPropertyInfoLinearLayout;
    @Bind(R.id.fragment_search_bar_property_info_text_view)
    TextView mProperyInfoTextView;
    @Bind(R.id.fragment_search_bar_property_address_text_view)
    TextView mPropertyAddressTextView;
    @Bind(R.id.fragment_search_bar_search_view)
    SearchView mSearchView;
    private SearchBarFragmentCallbacks mCallbacks;

    public static SearchBarFragment init(int type) {
        SearchBarFragment fragment = new SearchBarFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_bar, container, false);
        // bind view to ButterKnife
        ButterKnife.bind(this, view);

        // get arguments
        Bundle args = getArguments();
        if(args != null) {
            int type = args.getInt(TYPE);
            if(type == TYPE_PROPERTY_INFO) {
                mSearchView.setVisibility(View.GONE);
                mPropertyInfoLinearLayout.setVisibility(View.VISIBLE);
            }
        }
        Activity activity = getActivity();
        if(activity instanceof SearchBarFragmentCallbacks) {
            mCallbacks = (SearchBarFragmentCallbacks)activity;
        }
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO handle search
            }
        });
        mSearchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mCallbacks.onQueryChanged(newText);
                return true;
            }
        });
        return view;
    }

    public interface SearchBarFragmentCallbacks {
        public void onQueryChanged(String query);
    }
}
