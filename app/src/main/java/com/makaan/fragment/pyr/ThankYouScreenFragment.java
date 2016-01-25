package com.makaan.fragment.pyr;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by proptiger on 22/1/16.
 */
public class ThankYouScreenFragment extends Fragment {

    private boolean makaanAssist=false;
    private boolean noSellers=false;
    TextView mSellerName;
    TextView mAssistMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        makaanAssist=getArguments().getBoolean(PyrPagePresenter.MAKAAN_ASSIST_VALUE);
        noSellers=getArguments().getBoolean(PyrPagePresenter.NO_SELLERS_FRAGMENT);
        if(makaanAssist) {
            view = inflater.inflate(R.layout.thank_you_screen_makaan_assist, container, false);
            mAssistMessage=(TextView)view.findViewById(R.id.makaan_assist_mesg);
        }
        else{
            view = inflater.inflate(R.layout.thank_you_screen_makaan, container, false);
            mSellerName=(TextView)view.findViewById(R.id.seller_name);
        }
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PyrPagePresenter pyrPagePresenter=PyrPagePresenter.getPyrPagePresenter();
        if(pyrPagePresenter.getNonMakaanAssistCount()==1&& mSellerName!=null){
            mSellerName.setText(pyrPagePresenter.getSellerName());
        }
        if(noSellers){
            mAssistMessage.setText(getResources().getString(R.string.no_sellers_assist_string));
        }

    }

    @OnClick(R.id.thank_you_continue)
    public void onContinueClick(){
        getActivity().onBackPressed();
    }

}
