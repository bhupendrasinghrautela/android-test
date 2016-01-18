package com.makaan.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.makaan.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by aishwarya on 08/01/16.
 */
public class CompressedTextView extends BaseLinearLayout<String> {

    @Bind(R.id.header_text)
    TextView mHeaderText;
    @Bind(R.id.content_text)
    TextView mContentText;
    @Bind(R.id.read_more)
    TextView mReadMore;
    private Boolean isCollapsed = true;
    private static final int MAX_LINE = 5;
    @OnClick(R.id.read_more) void click(){
        if(isCollapsed){
            mReadMore.setText(mContext.getString(R.string.read_less));
            mContentText.setMaxLines(Integer.MAX_VALUE);
        }
        else{
            mReadMore.setText(mContext.getString(R.string.read_more));
            mContentText.setMaxLines(MAX_LINE);
        }
        isCollapsed=!isCollapsed;
    }

    public CompressedTextView(Context context) {
        super(context);
    }

    public CompressedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompressedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bindView(String item) {

    }
}
