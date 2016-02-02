package com.makaan.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.makaan.R;
import com.makaan.util.KeyUtil;

import butterknife.Bind;

/**
 * Created by rohitgarg on 1/29/16.
 */
public class WebViewFragment extends MakaanBaseFragment {
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_web_view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        String url;

        Bundle data = getArguments();
        if (data != null) {
            url = data.getString(KeyUtil.KEY_REQUEST_URL);
            mProgressBar.setMax(100);

            try {
                loadUrl(url);
            } catch (Exception e) {
                getActivity().onBackPressed();
            }
        }

        return view;
    }

    private void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("Please supply valid url");
        }

        if (mProgressBar != null) {
            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mWebView.setWebChromeClient(new CommonWebChromeClient());
        mWebView.setWebViewClient(new CommonWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.loadUrl(url);
    }

    public void setValue(int progress) {
        if (mProgressBar != null) {
            mProgressBar.setProgress(progress);
        }
    }

    private Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    private class CommonWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("mailto")) {
                final Activity activity = WebViewFragment.this.getActivity();
                if (activity != null) {
                    try {
                        MailTo mt = MailTo.parse(url);
                        Intent i = newEmailIntent(activity, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                        activity.startActivity(i);
                    } catch (Exception e) {
                    }
                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, url);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.cancel();
        }
    }

    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        getActivity().onBackPressed();
    }

    private class CommonWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            WebViewFragment.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }
}
