package com.makaan.request;

import com.google.gson.Gson;
import com.makaan.request.selector.PagingSelector;
import com.makaan.request.selector.RangeSelector;
import com.makaan.request.selector.Selector;
import com.makaan.request.selector.SortSelector;
import com.makaan.request.selector.TermSelector;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 04/01/16.
 */
public class SerpRequest {

    private static final Integer SERP_PAGE_SIZE = 20;


    /*public long cityId;
    public long localityId;
    public long landMarkId;
    public int pageNo = 1;
    public int sortFieldUId;            // check enum SerpSortField
    public boolean sortAsc;
*/







    public String getUrl(){
        return "https://marketplace-qa.proptiger-ws.com/app/v1/listing?selector=%7B%22filters%22:%7B%22and%22:[%7B%22equal%22:%7B%22cityId%22:%222%22%7D%7D]%7D,%22paging%22:%7B%22start%22:0,%22rows%22:10%7D%7D";
    }


   /* private ;


    private void buildSelector() {

        Map<String,> rangeKeyMap = new HashMap();

        List<Map<String, Map<String, Object>>> equalKeyMapList = new ArrayList<>();

        for (Map.Entry<String, List<String>> termFilterEntry : termFilterValues.entrySet()) {
            Map<String, Map<String, List<String>>> equalKeyMap = new HashMap();
            Map<String, List<String>> equalKeyMapValue = new HashMap<>();
            equalKeyMapValue.put(termFilterEntry.getKey(), termFilterEntry.getValue());
            equalKeyMap.put(EQUAL, equalKeyMapValue);

            equalKeyMapList.add(equalKeyMap);
        }


        HashMap<String, List<Map<String, List<String>>>> andFilters = new HashMap();

        filters.put(AND, equalKeyMapList);



        HashMap<String, String> paginationMap = new HashMap<>();
        Integer start = ((pageNo - 1) * PAGE_SIZE + 1);
        paginationMap.put(START, start.toString());
        paginationMap.put(ROWS, PAGE_SIZE.toString());


        Map<String, Map<String, String>> selector = new HashMap<>();
        selector.put(PAGING, paginationMap);

        HashMap<String, String> sortingMap = new HashMap<>();
        sortingMap.put(FIELD, SerpSortField.getSortField(sortFieldUId));
        sortingMap.put(SORT_ORDER, sortAsc ? ASC : DESC);
        selector.put(SORT, sortingMap);




    }*/
}
