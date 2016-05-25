package com.makaan;


import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;

import com.makaan.activity.HomeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by bhupendra on 04/05/16.
 */
public class SearchScreenTest {

    private String mStringToBetyped;

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(
            HomeActivity.class);

    @Before
    public void initValidString() {
        // Specify a valid string.
        mStringToBetyped = "Espresso";
    }

    @Test
    public void TestBuySearch_Project() {

        onView(withId(R.id.activity_home_property_buy_radio_button)).perform(click());

        onView(withId(R.id.activity_home_search_relative_view)).perform(click());

        onView(withId(R.id.activity_search_base_layout_search_bar_search_edit_text))
                .perform(typeText("Supertech cape town"), closeSoftKeyboard());
        onView(withText("supertech capetown, sector 74, noida")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("supertech capetown, sector 74, noida")).perform(click());

    }

    @Test
    public void TestRentSearch_Project() {

        onView(withId(R.id.activity_home_property_rent_radio_button)).perform(click());

        onView(withId(R.id.activity_home_search_relative_view)).perform(click());

        onView(withId(R.id.activity_search_base_layout_search_bar_search_edit_text))
                .perform(typeText("Supertech cape town"), closeSoftKeyboard());
        onView(withText("supertech capetown, sector 74, noida")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("supertech capetown, sector 74, noida")).perform(click());

    }


    @Test
    public void TestbuySearch_City() {

        onView(withId(R.id.activity_home_property_buy_radio_button)).perform(click());

        onView(withId(R.id.activity_home_search_relative_view)).perform(click());

        onView(withId(R.id.activity_search_base_layout_search_bar_search_edit_text))
                .perform(typeText("bangalore"), closeSoftKeyboard());
        onView(withText("mumbai")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("mumbai")).perform(click());

    }

    @Test
    public void TestRentSearch_City() {

        onView(withId(R.id.activity_home_property_rent_radio_button)).perform(click());

        onView(withId(R.id.activity_home_search_relative_view)).perform(click());

        onView(withId(R.id.activity_search_base_layout_search_bar_search_edit_text))
                .perform(typeText("mumbai"), closeSoftKeyboard());
        onView(withText("mumbai")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("mumbai")).perform(click());



    }

}
