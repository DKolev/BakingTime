package com.example.android.bakingtime;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Using references from this code here https://github.com/chiuki/espresso-samples/tree/master/idling-resource-okhttp
 * as well as https://github.com/JakeWharton/okhttp-idling-resource#okhttp-idling-resource
 */


@RunWith(AndroidJUnit4.class)
public class RecipeImageIsPresentBasicTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void recipeImageIsPresentBasicTest() {

        // Creating the Idling Resource using OkHttp3IdlingResource from Jake Wharton
        IdlingResource idlingResource = OkHttp3IdlingResource.create(
                "okhttp", OkHttpProvider.getOkHttpInstance());

        // Register the resources with Espresso
        Espresso.registerIdlingResources(idlingResource);

        // Performing a click on the first item of the list
        onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Checking if the recipe image is present
        onView(withId(R.id.recipe_image)).check(matches(isDisplayed()));

        // Unregister the Idling Resource
        Espresso.unregisterIdlingResources(idlingResource);
    }

}
