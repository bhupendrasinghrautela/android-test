package com.makaan;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.makaan.activity.HomeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by bhupendra on 04/05/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomePageTest {


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
    public void TapOnBuyRentTabs() {

        onView(withId(R.id.activity_home_property_buy_radio_button)).perform(click());
        onView(withId(R.id.activity_home_property_rent_radio_button)).perform(click());
    }
    @Test
    public void TapOnSearchTextBox() {


        onView(withId(R.id.activity_home_search_relative_view)).perform(click());

        //  onView(withId(R.id.activity_home_property_buy_radio_button))
              //  .check(matches(isDisplayed()));

    }
    @Test
    public void CheckExistanceOfBuyTab() {

         onView(withId(R.id.activity_home_property_buy_radio_button))
         .check(matches(isDisplayed()));

    }

    @Test
    public void CheckExistanceOfRentTab() {

        onView(withId(R.id.activity_home_property_rent_radio_button))
                .check(matches(isDisplayed()));

    }

    @Test
    public void CheckPlaceHolderText() {

      //  onView(withText("find joy…")).check(ViewAssertions.matches(isDisplayed()));

        onView(withHint("find joy…")).check(ViewAssertions.matches(isDisplayed()));

       // onView(withId(R.id.activity_home_search_text_view))
         //       .check(matches(anyOf(withText(endsWith("...")), withText(containsString("find joy")))));


    }

    @Test
    public void CheckLoginLinkPresence() {

        onView(withText("login")).check(ViewAssertions.matches(isDisplayed()));

    }

    @Test
    public void CheckRedirectionOfBuyerJourney() {

        onView(withText("login")).perform(click());
        onView(withText("personalized assistance")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("from start to finish")).check(ViewAssertions.matches(isDisplayed()));

    }

    @Test
    public void CheckBlinkingCursor() {

        onView(withId(R.id.activity_home_blinking_view))
                .check(matches(isDisplayed()));
    }
}
