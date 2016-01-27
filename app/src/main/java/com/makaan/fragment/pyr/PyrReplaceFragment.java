package com.makaan.fragment.pyr;

import android.support.v4.app.Fragment;

import com.makaan.ui.pyr.FilterableMultichoiceDialogFragment;

/**
 * Created by proptiger on 8/1/16.
 */
public interface PyrReplaceFragment {
    void replaceFragment(Fragment fragment, boolean shouldAddToBackStack);

    void showPropertySearchFragment(FilterableMultichoiceDialogFragment fragment);

    void popFromBackstack(int popCount);
}
