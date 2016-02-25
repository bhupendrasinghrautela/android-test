package com.makaan.ui.project;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.event.project.SpecificationLessClickedEvent;
import com.makaan.event.project.SpecificationMoreClickedEvent;
import com.makaan.pojo.SpecificaitonsUI;
import com.makaan.util.AppBus;

import java.util.List;


/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class ProjectSpecificationItemView extends LinearLayout implements View.OnClickListener {
    private Context mContext;
    private View moreView;
    private ProjectSpecificationItemView specifactionItemView;
    private List<SpecificaitonsUI> item;
    private LayoutInflater mLayoutInflater;
    private TextView moreTv;

    public ProjectSpecificationItemView(Context context) {
        super(context);
        mContext = context;
    }

    public ProjectSpecificationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ProjectSpecificationItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }


    public void bindView(List<SpecificaitonsUI> item, ProjectSpecificationItemView specifactionItemView) {
        this.specifactionItemView = specifactionItemView;
        this.item = item;
         mLayoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < item.size() && i<4; i++) {
            addViews(item.get(i), i);
        }
        if(item.size()>4){
            moreView =  mLayoutInflater.inflate(R.layout.row_project_specification_item_expanded, null);
            moreTv = (TextView) moreView.findViewById(R.id.project_specification_read_more);
            moreTv.setOnClickListener(this);
            specifactionItemView.addView(moreView,4);
        }
    }

    private void addViews(SpecificaitonsUI item, int i) {
        final View specificationView =
                mLayoutInflater.inflate(R.layout.row_project_specification_item, null);
        ((TextView) specificationView.findViewById(R.id.tv_project_specification_item_labe_one)).setText(item.label1);
        ((TextView) specificationView.findViewById(R.id.tv_project_specification_item_labe_two)).setText(item.label2);
        specifactionItemView.addView(specificationView,i);
    }
    private void removeViews(int i) {
        if(specifactionItemView.getChildAt(i)!=null)
        specifactionItemView.removeViewAt(i);
    }

    @Override
    public void onClick(View v) {
        if(specifactionItemView.getChildAt(5)==null) {
            AppBus.getInstance().post(new SpecificationMoreClickedEvent(specifactionItemView.getChildAt(3).getMeasuredHeight() * (item.size()-4)));
            moreTv.setText("less");
            for(int i = 4; i< item.size();i++)
                addViews(item.get(i),i+1);
        }else {
            AppBus.getInstance().post(new SpecificationLessClickedEvent(specifactionItemView.getChildAt(5).getMeasuredHeight() * (item.size()-4)));
            moreTv.setText("more");
            for(int i = item.size()-1; i>= 4;i--)
                removeViews(i+1);
        }
    }
}
