package com.example.cse110;

import android.view.View;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.cse110.View.ExpensesListActivity;
import com.example.cse110.View.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CreatingExpensesTest {

    @Rule
    public ActivityTestRule<LoginActivity> mExpensesCreateTestRule =
            new ActivityTestRule<LoginActivity>(LoginActivity.class);

    @Test
    public void test() throws InterruptedException {
        String email = "autoTest555@ucsd.edu";
        String password = "password123";
        onView(withId(R.id.username)).perform(ViewActions.click());
        onView(withId(R.id.username)).perform(ViewActions.typeText(email));
        onView(withId(R.id.username)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.password)).perform(ViewActions.click());
        onView(withId(R.id.password)).perform(ViewActions.typeText(password));
        onView(withId(R.id.password)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(ViewActions.click());
        Thread.sleep(3000);
        onView(withId(R.id.ExpensesButton)).perform(ViewActions.click());
        Thread.sleep(250);

        onView(withId(R.id.category_name)).perform(ViewActions.click(), ViewActions.typeText("Food"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.category_budget)).perform(ViewActions.click(), ViewActions.typeText("500"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.AddToList)).perform(ViewActions.click());

        Thread.sleep(250);
        //onView(withId(R.id.Categories)).


    }
}
