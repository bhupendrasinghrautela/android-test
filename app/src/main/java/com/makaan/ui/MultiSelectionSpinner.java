package com.makaan.ui;

/**
 * Created by aishwarya on 26/6/15.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.makaan.R;
import com.makaan.response.master.ApiIntLabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MultiSelectionSpinner extends Spinner implements  OnMultiChoiceClickListener {
    String[] _items = null;
    Integer[] _ids = null;

    boolean[] mSelection = null;
    boolean doneClicked = false;
    private String mPrefix,mPostFix;
    private AlertDialog.Builder builder;
    private String displayString;

    ArrayAdapter<String> simple_adapter;
    private OnSelectionChangeListener listener;
    public interface OnSelectionChangeListener{
        void onSelectionChanged();
    }

    public MultiSelectionSpinner(Context context) {
        super(context);

        simple_adapter = new ArrayAdapter<String>(context,
                R.layout.multi_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        simple_adapter = new ArrayAdapter<String>(context,
                R.layout.multi_spinner_item);
        super.setAdapter(simple_adapter);
    }
    public void setMessage(String prefix,String postFix){
        mPrefix = prefix;
        mPostFix = postFix;
    }
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;

            simple_adapter.clear();
            simple_adapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener selectionChangeListener){
        this.listener = selectionChangeListener;
    }

    @Override
    public boolean performClick() {
        builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setMultiChoiceItems(_items, mSelection, this);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(displayString == null || !displayString.equals(buildSelectedItemString())){
                    if(listener !=null){
                        listener.onSelectionChanged();
                    }
                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simple_adapter.clear();
                int [] clear = new int[0];
                setSelection(clear);
            }
        });
        AlertDialog d = builder.create();
        d.show();
        /*d.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(displayString == null || !displayString.equals(buildSelectedItemString())){
                    if(listener !=null){
                        listener.onSelectionChanged();
                    }
                }
            }
        });*/
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
        Arrays.fill(mSelection, false);
    }

    public void setItems(ArrayList<ApiIntLabel> items) {
        _items = new String[items.size()];
        _ids = new Integer[items.size()];
        for(int i=0;i< items.size();i++){
            _items[i] = items.get(i).name.toLowerCase();
            _ids[i] = items.get(i).id;
        }
        mSelection = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
        Arrays.fill(mSelection, false);
    }

    public void setSelection(String[] selection) {
        for (String cell : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(cell)) {
                    mSelection[j] = true;
                }
            }
        }
    }

    public void setSelection(List<String> selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndicies) {
        if(mSelection == null){
            return;
        }
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
        }
        for (int index : selectedIndicies) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<String>();
        for (int i = 0; i < _items.length;i++) {
            if (mSelection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndicies() {
        List<Integer> selection = new LinkedList<Integer>();
        for (int i = 0; i < _items.length;i++) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIds(){
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < _items.length;i++) {
            if (mSelection[i]) {
                ids.add(_ids[i]);
            }
        }
        return ids;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        if(mPrefix!=null){
            sb.append(mPrefix);
            String prefix = " ";
            for (int i = 0; i < _items.length;i++) {
                if (mSelection[i]) {
                    sb.append(prefix);
                    prefix = ",";
                    sb.append(" "+_items[i]);
                }
            }
        }
        else if(mPostFix != null){
            String prefix = " ";
            boolean selection = false;
            for (int i = 0; i < _ids.length;i++) {
                if (mSelection[i]) {
                    sb.append(prefix);
                    prefix = ",";
                    selection = true;
                    sb.append(" "+_ids[i]);
                }
            }
            sb.append(mPostFix);
            if(selection){
                return sb.toString().replace(":","");
            }
        }

        return sb.toString();
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length;i++) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(_items[i]);
            }
        }
        return sb.toString();
    }
}
