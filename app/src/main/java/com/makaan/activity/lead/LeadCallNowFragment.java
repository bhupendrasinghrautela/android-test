package com.makaan.activity.lead;

import android.content.Intent;
import android.net.Uri;

import com.makaan.R;
import com.makaan.fragment.MakaanBaseFragment;

import butterknife.OnClick;

/**
 * Created by makaanuser on 23/1/16.
 */
public class LeadCallNowFragment extends MakaanBaseFragment {

    @Override
    protected int getContentViewId() {
        return R.layout.layout_lead_call_now;
    }

   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_lead_call_now, container, false);
        ButterKnife.bind(this, view);
        return view;
    }*/

    @OnClick(R.id.btn_call)
    void callClick(){
        //TODO remove this hardcoded number
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                + 1234567890));
        startActivity(intent);
    }

    @OnClick(R.id.tv_get_callback)
    void getCallBackClick(){
        LeadFormPresenter.getLeadFormPresenter().showLeadInstantCallBackFragment();
    }
}
