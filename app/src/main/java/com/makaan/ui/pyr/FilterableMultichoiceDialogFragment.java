package com.makaan.ui.pyr;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.makaan.R;
import com.makaan.response.search.SearchResponseItem;
import com.makaan.response.search.SearchType;
import com.makaan.response.search.event.SearchResultEvent;
import com.makaan.service.MakaanServiceFactory;
import com.makaan.service.SearchService;
import com.makaan.fragment.pyr.PyrPagePresenter;
import com.makaan.adapter.pyr.SearchableListviewAdapter;
import com.makaan.adapter.pyr.SelectedListViewAdapter;
import com.makaan.util.AppBus;
import com.squareup.otto.Subscribe;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

/**
 * Filterable multichoice dialog fragment maintaining different lists for
 * selected and unselected items
 * 
 * 
 */

public class FilterableMultichoiceDialogFragment extends DialogFragment {

	@Bind(R.id.multichoice_dialog_list_view)ListView mMultiselectionListview;
	@Bind(R.id.multichoice_dialog_selected_items_list_view)ListView mSelectedListview;
	@Bind(R.id.clear_button)TextView mClearButton;
	@Bind(R.id.multichoice_dialog_search_tv)EditText mMultiChoiceSearchTextView;
	@Bind(R.id.multichoice_card_view)CardView mMultiChoiceCardView;
	@Bind(R.id.empty_selection)TextView mEmptySelectedItemsTextView;
	private boolean[] mSelectedItemsFlag;
	private ArrayList<Item> mCompleteItemsList;
	private ArrayList<SearchResponseItem> mOriginalItemsList;
	private ArrayList<SearchResponseItem> mUnselectedItemsList;
	private ArrayList<SearchResponseItem> mSelectedItemsList;
	private SelectedListViewAdapter mSelectedItemsAdapter;
	private SearchableListviewAdapter mUnselectedItemsAdapter;
	private boolean mSelectedItemsAvaliable;
	private PyrPagePresenter mPyrPresenter;
	private Integer[] locaityIds;

	public FilterableMultichoiceDialogFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AppBus.getInstance().register(this);
		try {
			final Dialog multiSelectionDialog = new Dialog(getActivity());
			mCompleteItemsList = new ArrayList<Item>();
			mUnselectedItemsList = new ArrayList<SearchResponseItem>();
			mSelectedItemsList = new ArrayList<SearchResponseItem>();
			mPyrPresenter=PyrPagePresenter.getPyrPagePresenter();
			if(mPyrPresenter.getAlreadySelectedProjects().size()>0){
				mSelectedItemsList.clear();
				mSelectedItemsList.addAll(mPyrPresenter.getAlreadySelectedProjects());
			}
			View dialogCustomView = getActivity().getLayoutInflater().inflate(R.layout.multichoice_dialog_fragment_custom_view, null);
			ButterKnife.bind(this,dialogCustomView);

			mUnselectedItemsAdapter = new SearchableListviewAdapter(getActivity(), mUnselectedItemsList,
					R.layout.unselected_list_item_layout);

			if(mSelectedItemsList.size()>0){
			    mSelectedItemsAvaliable = true;
			    mEmptySelectedItemsTextView.setVisibility(View.GONE);
			    mSelectedListview.setVisibility(View.VISIBLE);
			}else{
			    mSelectedItemsAvaliable = false;
                mEmptySelectedItemsTextView.setVisibility(View.VISIBLE);
                mSelectedListview.setVisibility(View.GONE);
			}

			mMultiselectionListview.setAdapter(mUnselectedItemsAdapter);
			if(mUnselectedItemsList.size()==0){
				mMultiChoiceCardView.setVisibility(View.INVISIBLE);
			}
			mSelectedItemsAdapter = new SelectedListViewAdapter(getActivity(),
					mSelectedItemsList, R.layout.selected_list_item_layout);
			mSelectedListview.setAdapter(mSelectedItemsAdapter);
			if(mSelectedItemsList.size()>0){
				mSelectedItemsAdapter.updateDataItems(mSelectedItemsList);
			}

			multiSelectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			multiSelectionDialog.setContentView(dialogCustomView);
			multiSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
			multiSelectionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	        
			return multiSelectionDialog;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@OnTextChanged(R.id.multichoice_dialog_search_tv)
	public void onSearchTextChange(CharSequence cs, int arg1, int arg2, int arg3){
		SearchService searchService = (SearchService) MakaanServiceFactory.getInstance().getService(SearchService.class);
		try {
			if(!cs.toString().isEmpty()) {
				searchService.getSearchResults(cs.toString(), "noida", SearchType.LOCALITY, false);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@OnItemClick(R.id.multichoice_dialog_list_view)
	public void multiChoiceClick(AdapterView<?> arg0, View arg1,
								 int clicked, long id){

			String clickedItem = (String) mUnselectedItemsAdapter.getItem(clicked).entityName;
			mPyrPresenter.updateSelectedItemsList(clickedItem, mSelectedItemsList,
					mUnselectedItemsAdapter.getItem(clicked));
			mPyrPresenter.setLocalityIds(mUnselectedItemsAdapter.getItem(clicked), getActivity());
			mSelectedItemsAdapter.updateDataItems(mSelectedItemsList);

			if (mSelectedItemsList.isEmpty()) {
				mSelectedItemsAvaliable = false;
				mSelectedListview.setVisibility(View.GONE);
				mEmptySelectedItemsTextView.setVisibility(View.VISIBLE);
			} else {
				if (!mSelectedItemsAvaliable) {
					mSelectedItemsAvaliable = true;
					mSelectedListview.setVisibility(View.VISIBLE);
					mEmptySelectedItemsTextView.setVisibility(View.GONE);
				}
			}
	}

	@OnItemClick(R.id.multichoice_dialog_selected_items_list_view)
	public void onSelectedItemsClick(AdapterView<?> arg0, View arg1,
								 int clicked, long id){

		String clickedItem = (String) mSelectedItemsAdapter.getItem(clicked).entityName;
		mPyrPresenter.removeLocalityId(mSelectedItemsAdapter.getItem(clicked));
		mSelectedItemsList=mPyrPresenter.getSelectedItemList(clickedItem, mSelectedItemsList);
		mSelectedItemsAdapter.updateDataItems(mSelectedItemsList);

		if (mSelectedItemsList.isEmpty()) {
			mSelectedItemsAvaliable = false;
			mSelectedListview.setVisibility(View.GONE);
			mEmptySelectedItemsTextView.setVisibility(View.VISIBLE);
		} else {
			if (!mSelectedItemsAvaliable) {
				mSelectedItemsAvaliable = true;
				mSelectedListview.setVisibility(View.VISIBLE);
				mEmptySelectedItemsTextView.setVisibility(View.GONE);
			}
		}
	}

	@OnClick(R.id.clear_button)
	public void onClearButtonClick(){
		if(getActivity() == null || getActivity().isFinishing()) {
			return;
		}

		mSelectedItemsList.clear();
		mSelectedItemsAvaliable = false;
		mSelectedListview.setVisibility(View.GONE);
		mEmptySelectedItemsTextView.setVisibility(View.VISIBLE);
		mSelectedItemsAdapter.updateDataItems(mSelectedItemsList);
		mSelectedItemsAdapter.notifyDataSetChanged();
	}

	private class Item {
		protected SearchResponseItem mItemName;
		protected boolean mIsSelected;

		public Item(SearchResponseItem mItemName, boolean mIsSelected) {
			super();
			this.mItemName = mItemName;
			this.mIsSelected = mIsSelected;
		}

		public SearchResponseItem getItemName() {
			return mItemName;
		}

		public void setItemName(SearchResponseItem mItemName) {
			this.mItemName = mItemName;
		}

		public boolean isItemSelected() {
			return mIsSelected;
		}

		public void setItemSelected(boolean mIsSelected) {
			this.mIsSelected = mIsSelected;
		}

	}

	@Subscribe
	public void searchResult(SearchResultEvent searchResultEvent){
		mCompleteItemsList.clear();
		ArrayList<SearchResponseItem> mOriginalList=searchResultEvent.searchResponse.getData();
		mSelectedItemsFlag=new boolean[mOriginalList.size()];
		if(mOriginalList.size()>0){
			mMultiChoiceCardView.setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < mOriginalList.size(); i++) {
			mCompleteItemsList.add(new Item(mOriginalList.get(i), mSelectedItemsFlag[i]));
		}
		mUnselectedItemsAdapter.updateDataItems(mOriginalList);
	}

}