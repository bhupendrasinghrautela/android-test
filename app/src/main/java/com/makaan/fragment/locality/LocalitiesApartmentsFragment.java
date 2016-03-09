package com.makaan.fragment.locality;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.locality.ListingAggregation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import lecho.lib.hellocharts.dto.PieChartDTO;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by tusharchaudhary on 1/19/16.
 */
public class LocalitiesApartmentsFragment extends MakaanBaseFragment {
    private LinearLayoutManager mLayoutManager;
    private AvailablePropertyStatusAdapter mAdapter;
    private String title;
    private List<ListingAggregation> primaryListingAggregations;
    private List<ListingAggregation> secondaryListingAggregations;
    private AvailablePropertyStatusAdapter mAdapterSecondary;

    @Bind(R.id.switch_localities_props)
    Switch switchRentSale;
    @Bind(R.id.chart_donut)
    PieChartView donutChart;
    @Bind(R.id.tv_localities_aprmnts_title)
    TextView titleTv;
    @Bind(R.id.rv_localities_aprmnts)
    RecyclerView mRecyclerView;
    @Bind(R.id.ll_property_status)
    LinearLayout frame;
    private Set<DataItem> dataItemSet;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_localities_apartments;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initChart() {
        float[] values_num= new float[3];
        values_num[0] = 5f;
        values_num[1] = 2f;
        values_num[2] = 8f;
        String[] values_Category = new String[3];
        values_Category[0] = "1 BHK";
        values_Category[1] = "2 BHK";
        values_Category[2] = "3 BHK";
        if(values_num.length>0) {
            donutChart.setCategories(values_Category);
            PieChartDTO pieChartDTO = new PieChartDTO(values_num, 15, values_Category);
            donutChart.setPieChartData(pieChartDTO.getData());
            donutChart.setChartRotationEnabled(false);
            donutChart.setValueSelectionEnabled(true);
            donutChart.setCircleFillRatio(0.7f);
        }
    }

    private void initView() {
        title = getArguments().getString("title");
        titleTv.setText(title);
        initSwitch();
        initChart();
        initRecyclerView();
    }

    private void initRecyclerView() {
        if(dataItemSet!=null && dataItemSet.size()>0) {
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
        }else{
            frame.setVisibility(View.GONE);
        }
    }

    private void initSwitch() {
        switchRentSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    changeAdapter(isChecked);

            }
        });
    }

    public void setData(ArrayList<ListingAggregation> nearByLocalities) {
        dataItemSet = getData(nearByLocalities);
            mAdapter = new AvailablePropertyStatusAdapter(dataItemSet);
            mAdapterSecondary = new AvailablePropertyStatusAdapter(removeAndCalculateMinMax(secondaryListingAggregations));
            if (mRecyclerView != null)
                mRecyclerView.setAdapter(mAdapter);
    }

    private void changeAdapter(boolean isChecked){
        if(isChecked)
            mRecyclerView.setAdapter(mAdapterSecondary);
        else
            mRecyclerView.setAdapter(mAdapter);
    }

    private Set<DataItem> getData(ArrayList<ListingAggregation> nearByLocalities) {
        filterPrimaryAndSecondary(nearByLocalities);
        return removeAndCalculateMinMax(primaryListingAggregations);
    }

    private Set<DataItem> removeAndCalculateMinMax(List<ListingAggregation> primaryListingAggregations) {
        Set<DataItem> dataItemSet = getSetOfDataItemsBasedOnUnitTypes(primaryListingAggregations);
        for(DataItem dataItem : dataItemSet){
            for(ListingAggregation listingAggregation:primaryListingAggregations){
                if(listingAggregation.listingCategory.equalsIgnoreCase(dataItem.unitType)){
                    switch (listingAggregation.bedrooms){
                        case 1:
                            calculateMaxForOneBhks(dataItem, listingAggregation);
                            calculateMinForOneBhks(dataItem, listingAggregation);

                            break;
                        case 2:
                            calculateMaxForTwoBhks(dataItem, listingAggregation);
                            calculateMinForTwoBhks(dataItem, listingAggregation);
                            break;
                        case 3:
                            calculateMaxForThreeBhks(dataItem, listingAggregation);
                            calculateMinsForThreeBhks(dataItem, listingAggregation);
                            break;
                    }
                }
            }
        }
        return dataItemSet;
    }

    @NonNull
    private Set<DataItem> getSetOfDataItemsBasedOnUnitTypes(List<ListingAggregation> primaryListingAggregations) {
        Set<DataItem> dataItemSet = new HashSet<>();
        for(ListingAggregation listingAggregation:primaryListingAggregations){
            if(listingAggregation.listingCategory!=null)
            dataItemSet.add(new DataItem(listingAggregation.listingCategory,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d,0d));
        }
        return dataItemSet;
    }

    private void calculateMaxForOneBhks(DataItem dataItem, ListingAggregation listingAggregation) {
        if(dataItem.oneBhkMaxArea!=null && listingAggregation.maxSize!=null) {
            if (dataItem.oneBhkMaxArea < listingAggregation.maxSize) {
                dataItem.oneBhkMaxArea = listingAggregation.maxSize;
            }
        }
        if(dataItem.oneBhkMaxPrice !=null && listingAggregation.maxPrice!=null) {
            if (dataItem.oneBhkMaxPrice < listingAggregation.maxPrice) {
                dataItem.oneBhkMaxPrice = listingAggregation.maxPrice;
            }
        }
        if(dataItem.oneBhkMaxPricePerArea!=null && listingAggregation.maxPricePerUnitArea!=null) {
            if (dataItem.oneBhkMaxPricePerArea < listingAggregation.maxPricePerUnitArea) {
                dataItem.oneBhkMaxPricePerArea = listingAggregation.maxPricePerUnitArea;
            }
        }
    }

    private void calculateMinForOneBhks(DataItem dataItem, ListingAggregation listingAggregation) {
        if(dataItem.oneBhkMinArea == 0&&listingAggregation.minSize>100)
            dataItem.oneBhkMinArea= listingAggregation.minSize;
        else
        if(dataItem.oneBhkMinArea>listingAggregation.minSize&&listingAggregation.minSize>100){dataItem.oneBhkMinArea=listingAggregation.minSize;}

        if(dataItem.oneBhkMinPricePerArea == 0&&listingAggregation.minPricePerUnitArea>100)
            dataItem.oneBhkMinPricePerArea= listingAggregation.minPricePerUnitArea;
        else
        if(dataItem.oneBhkMinPricePerArea>listingAggregation.minPricePerUnitArea&&listingAggregation.minPricePerUnitArea>100){dataItem.oneBhkMinPricePerArea=listingAggregation.minPricePerUnitArea;}

        if(dataItem.oneBhkMinPrice == 0&&listingAggregation.minPrice>100000)
            dataItem.oneBhkMinPrice= listingAggregation.minPrice;
        else
        if(dataItem.oneBhkMinPrice>listingAggregation.minPrice&&listingAggregation.minPrice>100000){dataItem.oneBhkMinPrice=listingAggregation.minPrice;}

    }

    private void calculateMaxForTwoBhks(DataItem dataItem, ListingAggregation listingAggregation) {
        if(dataItem.twoBhkMaxArea<listingAggregation.maxSize){ dataItem.twoBhkMaxArea = listingAggregation.maxSize;}
        if(dataItem.twoBhkMaxPrice<listingAggregation.maxPrice){ dataItem.twoBhkMaxPrice = listingAggregation.maxPrice;}
        if(dataItem.twoBhkMaxAPricePerArea<listingAggregation.maxPricePerUnitArea){ dataItem.twoBhkMaxAPricePerArea = listingAggregation.maxPricePerUnitArea;}
    }

    private void calculateMinForTwoBhks(DataItem dataItem, ListingAggregation listingAggregation) {
        if(dataItem.twoBhkMinArea == 0&&listingAggregation.minSize>100)
            dataItem.twoBhkMinArea= listingAggregation.minSize;
        else
        if(dataItem.twoBhkMinArea>listingAggregation.minSize&&listingAggregation.minSize>100){dataItem.twoBhkMinArea=listingAggregation.minSize;}

        if(dataItem.twoBhkMinPricePerArea == 0&&listingAggregation.minPrice>100000)
            dataItem.twoBhkMinPricePerArea= listingAggregation.minPrice;
        else
        if(dataItem.twoBhkMinPricePerArea>listingAggregation.minPrice&&listingAggregation.minPrice>100000){dataItem.twoBhkMinPricePerArea=listingAggregation.minPrice;}

        if(dataItem.twoBhkMinPrice == 0&&listingAggregation.minPricePerUnitArea>100)
            dataItem.twoBhkMinPrice= listingAggregation.minPricePerUnitArea;
        else
        if(dataItem.twoBhkMinPrice>listingAggregation.minPricePerUnitArea&&listingAggregation.minPricePerUnitArea>100){dataItem.twoBhkMinPrice=listingAggregation.minPricePerUnitArea;}
    }

    private void calculateMaxForThreeBhks(DataItem dataItem, ListingAggregation listingAggregation) {
        if(dataItem.threeBhkMaxArea<listingAggregation.maxSize){ dataItem.threeBhkMaxArea = listingAggregation.maxSize;}
        if(dataItem.threeBhkMaxPrice<listingAggregation.maxPrice){ dataItem.threeBhkMaxPrice = listingAggregation.maxPrice;}
        if(dataItem.threeBhkMaxPricePerArea<listingAggregation.maxPricePerUnitArea){ dataItem.threeBhkMaxPricePerArea = listingAggregation.maxPricePerUnitArea;}
    }

    private void calculateMinsForThreeBhks(DataItem dataItem, ListingAggregation listingAggregation) {
        if(dataItem.threeBhkMinArea == 0&&listingAggregation.minSize>100)
            dataItem.threeBhkMinArea= listingAggregation.minSize;
        else
        if(dataItem.threeBhkMinArea>listingAggregation.minSize&&listingAggregation.minSize>100){dataItem.threeBhkMinArea=listingAggregation.minSize;}

        if(dataItem.threeBhkMinPricePerArea == 0&&listingAggregation.minPricePerUnitArea>100)
            dataItem.threeBhkMinPricePerArea= listingAggregation.minPricePerUnitArea;
        else
        if(dataItem.threeBhkMinPricePerArea>listingAggregation.minPricePerUnitArea&&listingAggregation.minPricePerUnitArea>100){dataItem.threeBhkMinPricePerArea=listingAggregation.minPricePerUnitArea;}

        if(dataItem.threeBhkMinPrice == 0&&listingAggregation.minPrice>100000)
            dataItem.threeBhkMinPrice= listingAggregation.minPrice;
        else
        if(dataItem.threeBhkMinPrice>listingAggregation.minPrice&&listingAggregation.minPrice>100000){dataItem.threeBhkMinPrice=listingAggregation.minPrice;}
    }

    private void filterPrimaryAndSecondary(ArrayList<ListingAggregation> nearByLocalities) {
        primaryListingAggregations = new ArrayList<>();
        secondaryListingAggregations = new ArrayList<>();
        for(ListingAggregation listingAggregation : nearByLocalities){
            if((listingAggregation.listingCategory.equalsIgnoreCase("Resale")||listingAggregation.listingCategory.equalsIgnoreCase("Primary"))&&(listingAggregation.bedrooms>0)){
                primaryListingAggregations.add(listingAggregation);
            }else{
                if(listingAggregation.bedrooms>0)
                secondaryListingAggregations.add(listingAggregation);
            }
        }
    }

    private class AvailablePropertyStatusAdapter extends RecyclerView.Adapter<AvailablePropertyStatusAdapter.ViewHolder> {
        private List<DataItem> propertiesList =new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView unitTypeTv;
            public TextView oneBhkLabel, oneBhkPriceRange, oneBhkPriceAreaRange, oneBhkAreaRange;
            public TextView twoBhkLabel, twoBhkPriceRange, twoBhkPriceAreaRange, twoBhkAreaRange;
            public TextView threeBhkLabel, threeBhkPriceRange, threeBhkPriceAreaRange, threeBhkAreaRange;

            public ViewHolder(View v) {
                super(v);
                oneBhkLabel = (TextView) v.findViewById(R.id.apartment_row_one_bhk);
                oneBhkPriceRange = (TextView) v.findViewById(R.id.apartment_row_one_price);
                oneBhkPriceAreaRange = (TextView) v.findViewById(R.id.apartment_row_one_price_per_sqft);
                oneBhkAreaRange = (TextView) v.findViewById(R.id.apartment_row_one_area);
                unitTypeTv = (TextView) v.findViewById(R.id.apartment_unit_type);


                twoBhkLabel = (TextView) v.findViewById(R.id.apartment_row_two_bhk);
                twoBhkPriceRange = (TextView) v.findViewById(R.id.apartment_row_two_price);
                twoBhkPriceAreaRange = (TextView) v.findViewById(R.id.apartment_row_two_price_per_sqft);
                twoBhkAreaRange = (TextView) v.findViewById(R.id.apartment_row_two_area);


                threeBhkLabel = (TextView) v.findViewById(R.id.apartment_row_three_bhk);
                threeBhkPriceRange = (TextView) v.findViewById(R.id.apartment_row_three_price);
                threeBhkPriceAreaRange = (TextView) v.findViewById(R.id.apartment_row_three_price_per_sqft);
                threeBhkAreaRange = (TextView) v.findViewById(R.id.apartment_row_three_area);
            }
        }

        public AvailablePropertyStatusAdapter(Set<DataItem> propertiesList) {
            this.propertiesList.addAll(propertiesList);
        }

        @Override
        public AvailablePropertyStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.locality_apartments_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final DataItem property = propertiesList.get(position);
            holder.unitTypeTv.setText(property.unitType);

            holder.oneBhkLabel.setText("1 bhk");
            holder.oneBhkAreaRange.setText(property.oneBhkMinArea+" - "+property.oneBhkMaxArea+" sq ft");
            holder.oneBhkPriceAreaRange.setText(property.oneBhkMinPricePerArea+" - "+property.oneBhkMaxPricePerArea+" / sq ft");
            holder.oneBhkPriceRange.setText("\u20B9 "+(String.format("%.1f", property.oneBhkMinPrice/100000))+" L"+" - "+(String.format("%.1f", property.oneBhkMaxPrice/100000))+" L");


            holder.twoBhkLabel.setText("2 bhk");
            holder.twoBhkAreaRange.setText(property.twoBhkMinArea+" - "+property.twoBhkMaxArea+" sq ft");
            holder.twoBhkPriceRange.setText("\u20B9 "+(String.format("%.1f", property.twoBhkMinPrice/100000))+" L"+" - "+(String.format("%.1f", property.twoBhkMaxPrice/100000))+" L");

            holder.threeBhkLabel.setText("3 bhk");
            holder.threeBhkAreaRange.setText(property.threeBhkMinArea+" - "+property.threeBhkMaxArea+" sq ft");
            holder.threeBhkPriceAreaRange.setText(property.threeBhkMinPricePerArea+" - "+property.threeBhkMaxPricePerArea+" / sq ft");
            holder.threeBhkPriceRange.setText("\u20B9 "+(String.format("%.1f", property.threeBhkMinPrice/100000))+" L"+" - "+(String.format("%.1f", property.threeBhkMaxPrice/100000))+" L");
        }

        @Override
        public int getItemCount() {
            return propertiesList.size();
        }

    }


    private class DataItem{
        String unitType;
        Double oneBhkMinPrice, twoBhkMinPrice, threeBhkMinPrice;
        Double oneBhkMaxPrice, twoBhkMaxPrice, threeBhkMaxPrice;
        Double oneBhkMaxArea, twoBhkMaxArea, threeBhkMaxArea;
        Double oneBhkMinArea, twoBhkMinArea, threeBhkMinArea;
        Double oneBhkMaxPricePerArea, twoBhkMaxAPricePerArea, threeBhkMaxPricePerArea;
        Double oneBhkMinPricePerArea, twoBhkMinPricePerArea, threeBhkMinPricePerArea;

        public DataItem(String unitType, Double twoBhkMinPricePerArea, Double oneBhkMinPrice, Double twoBhkMinPrice, Double threeBhkMinPrice, Double oneBhkMaxPrice, Double twoBhkMaxPrice, Double threeBhkMaxPrice, Double oneBhkMaxArea, Double twoBhkMaxArea, Double threeBhkMaxArea, Double oneBhkMinArea, Double twoBhkMinArea, Double threeBhkMinArea, Double oneBhkMaxPricePerArea, Double twoBhkMaxAPricePerArea, Double threeBhkMaxPricePerArea, Double oneBhkMinPricePerArea, Double threeBhkMinPricePerArea) {
            this.unitType = unitType;
            this.twoBhkMinPricePerArea = twoBhkMinPricePerArea;
            this.oneBhkMinPrice = oneBhkMinPrice;
            this.twoBhkMinPrice = twoBhkMinPrice;
            this.threeBhkMinPrice = threeBhkMinPrice;
            this.oneBhkMaxPrice = oneBhkMaxPrice;
            this.twoBhkMaxPrice = twoBhkMaxPrice;
            this.threeBhkMaxPrice = threeBhkMaxPrice;
            this.oneBhkMaxArea = oneBhkMaxArea;
            this.twoBhkMaxArea = twoBhkMaxArea;
            this.threeBhkMaxArea = threeBhkMaxArea;
            this.oneBhkMinArea = oneBhkMinArea;
            this.twoBhkMinArea = twoBhkMinArea;
            this.threeBhkMinArea = threeBhkMinArea;
            this.oneBhkMaxPricePerArea = oneBhkMaxPricePerArea;
            this.twoBhkMaxAPricePerArea = twoBhkMaxAPricePerArea;
            this.threeBhkMaxPricePerArea = threeBhkMaxPricePerArea;
            this.oneBhkMinPricePerArea = oneBhkMinPricePerArea;
            this.threeBhkMinPricePerArea = threeBhkMinPricePerArea;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DataItem dataItem = (DataItem) o;

            return unitType.equals(dataItem.unitType);

        }

        @Override
        public int hashCode() {
            return unitType.hashCode();
        }
    }
}
