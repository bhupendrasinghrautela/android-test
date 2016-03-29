package com.makaan.ui.listing;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.makaan.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by rohitgarg on 1/14/16.
 */
// TODO remove hardcoding
public class RelevancePopupWindowController implements PopupWindow.OnDismissListener {
    RelevancePopupWindowCallback callback;
    PopupWindow mPopupWindow;

    public void showRelevancePopupWindow(Context context, RelativeLayout view, RelevancePopupWindowCallback callback, int selectedSortIndex) {
        this.callback = callback;

        View popupView = LayoutInflater.from(context).inflate(R.layout.relevance_popup_window, null);
        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this, popupView);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOnDismissListener(this);
        mPopupWindow.showAsDropDown(view, 0, -view.getHeight());

        if(selectedSortIndex >= 0) {
            switch (selectedSortIndex) {
                case 0:
                    popupView.findViewById(R.id.relevance_popup_window_relevance_button).setPressed(true);
                    break;
                case 1:
                    popupView.findViewById(R.id.relevance_popup_window_price_high_to_low_button).setPressed(true);
                    break;
                case 2:
                    popupView.findViewById(R.id.relevance_popup_window_price_low_to_high_button).setPressed(true);
                    break;
                case 3:
                    popupView.findViewById(R.id.relevance_popup_window_seller_rating_button).setPressed(true);
                    break;
                case 4:
                    popupView.findViewById(R.id.relevance_popup_window_date_posted_button).setPressed(true);
                    break;
                case 5:
                    popupView.findViewById(R.id.relevance_popup_window_livability_score_button).setPressed(true);
                    break;
                case 6:
                    popupView.findViewById(R.id.relevance_popup_window_distance_button).setPressed(true);
                    break;
            }
        }
    }

    @OnClick({R.id.relevance_popup_window_distance_button, R.id.relevance_popup_window_livability_score_button,
            R.id.relevance_popup_window_price_high_to_low_button, R.id.relevance_popup_window_price_low_to_high_button,
            R.id.relevance_popup_window_relevance_button, R.id.relevance_popup_window_seller_rating_button,
            R.id.relevance_popup_window_date_posted_button})
    void onRelevanceClicked(View view) {
        switch (view.getId()) {
            case R.id.relevance_popup_window_price_high_to_low_button:
                if(callback != null) {
                    callback.sortSelected("price - high to low","price", "DESC", 1);
                }
                break;
            case R.id.relevance_popup_window_price_low_to_high_button:
                if(callback != null) {
                    callback.sortSelected("price - low to high","price", "ASC", 2);
                }
                break;
            case R.id.relevance_popup_window_distance_button:
                /*if(callback != null) {
                    callback.sortSelected("price - low to high","price", "asc", 5);
                }*/

                break;
            case R.id.relevance_popup_window_livability_score_button:
                if(callback != null) {
                    callback.sortSelected("livability","listingLivabilityScore", "DESC", 5);
                }
                break;
            case R.id.relevance_popup_window_relevance_button:
                if(callback != null) {
                    callback.sortSelected("relevance", null, null, 0);
                }
                break;
            case R.id.relevance_popup_window_seller_rating_button:
                if(callback != null) {
                    callback.sortSelected("seller rating","listingSellerCompanyScore", "DESC", 3);
                }
                break;
            case R.id.relevance_popup_window_date_posted_button:
                if(callback != null) {
                    callback.sortSelected("date posted","listingPostedDate", "DESC", 4);
                }
                break;
        }
        mPopupWindow.dismiss();
    }

    @Override
    public void onDismiss() {
        if(callback != null) {
            callback.popupWindowDismissed();
        }
    }

    public interface RelevancePopupWindowCallback {
        void popupWindowDismissed();
        void sortSelected(String sort, String fieldName, String value, int i);
    }
}
