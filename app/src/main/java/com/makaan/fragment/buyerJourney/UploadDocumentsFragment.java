package com.makaan.fragment.buyerJourney;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.makaan.R;
import com.makaan.fragment.MakaanBaseFragment;

import butterknife.OnClick;

/**
 * Created by rohitgarg on 2/18/16.
 */
public class UploadDocumentsFragment extends MakaanBaseFragment {
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_upload_documents;
    }

    public void setData(Object obj) {

    }

    @OnClick(R.id.fragment_upload_documents_send_button)
    void onSendClicked(View view) {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "cashback@makaan.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "cashback request");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email"));
    }

    @OnClick(R.id.fragment_upload_documents_done_button)
    void onDoneClicked(View view) {
        getActivity().finish();
    }
}
