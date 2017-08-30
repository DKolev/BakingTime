package com.example.android.bakingtime;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.jakewharton.espresso.OkHttp3IdlingResource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class WhenAtStep0ClickOnNextButtonShowsStep1BasicTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void whenAtStep0ClickOnNextButtonShowsStep1() {

        // Creating the Idling Resource using OkHttp3IdlingResource from Jake Wharton
        IdlingResource idlingResource = OkHttp3IdlingResource.create(
                "okhttp", OkHttpProvider.getOkHttpInstance());

        // Register the resources with Espresso
        Espresso.registerIdlingResources(idlingResource);

        // Performing a click on the first item of the recipe list
        onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Performing a click on the first item of the steps list
        onView(withId(R.id.steps_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Scrolling to reveal the next button and click it
        onView(withId(R.id.next_step_button)).perform(ViewActions.scrollTo()).perform(click());

        // Verifying that the step number counter has increased to 1
        onView(withId(R.id.step_number)).check(matches(withText("1")));

        // Unregister the Idling Resource
        Espresso.unregisterIdlingResources(idlingResource);
    }
}
