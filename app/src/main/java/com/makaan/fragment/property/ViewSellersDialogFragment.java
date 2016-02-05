package com.makaan.fragment.property;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.pojo.SellerCard;
import com.makaan.ui.view.CustomRatingBar;
import com.makaan.util.AppBus;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by aishwarya on 03/02/16.
 */
public class ViewSellersDialogFragment extends DialogFragment {

    @Bind(R.id.fragment_dialog_contact_sellers_recycler_view)
    RecyclerView mRecyclerView;

    private ArrayList<SellerCard> mSellerCards;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_contact_sellers, container, false);

        AppBus.getInstance().register(this); //TODO: move to base fragment
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullscreen_dialog_fragment_theme);
    }

    private void init() {
        LayoutManager layoutManager= new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new AllSellersAdapter(mSellerCards));
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        return dialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppBus.getInstance().unregister(this);
    }

    public void bindView(ArrayList<SellerCard> sellerCards) {
        mSellerCards = sellerCards;
    }
    private class AllSellersAdapter extends RecyclerView.Adapter<AllSellersAdapter.ViewHolder> {
        private List<SellerCard> sellerCards;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            CustomRatingBar mSellerRating;
            CircularImageView mSellerImageView;
            TextView mSellerLogoTextView;
            TextView mSellerName;
            public ViewHolder(View v) {
                super(v);
                mSellerImageView = (CircularImageView) v.findViewById(R.id.seller_image_view);
                mSellerName = (TextView) v.findViewById(R.id.seller_name_text_view);
                mSellerLogoTextView = (TextView) v.findViewById(R.id.seller_logo_text_view);
            }
        }

        public AllSellersAdapter(List<SellerCard> sellerCards) {
            this.sellerCards = sellerCards;
        }

        @Override
        public AllSellersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.all_seller_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mSellerName.setText(String.format("%s(%s)",mSellerCards.get(position).name,mSellerCards.get(position).type));
        }

        @Override
        public int getItemCount() {
            return sellerCards.size();
        }

    }
}
