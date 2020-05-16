package com.example.cse110;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.cse110.View.CreateAccountActivity;
import com.example.cse110.View.ExpensesListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AccountCreationTest {

    @Rule
    public ActivityTestRule<CreateAccountActivity> mCreateAccountActivityTestRule =
            new ActivityTestRule<CreateAccountActivity>(CreateAccountActivity.class);

    @Test
    public void createDummyAccount() {
        String email = "autoTest555@ucsd.edu";
        String password = "password123";
        onView(withId(R.id.username)).perform(ViewActions.click());
        onView(withId(R.id.username)).perform(ViewActions.typeText(email));
        onView(withId(R.id.password)).perform(ViewActions.click());
        onView(withId(R.id.password)).perform(ViewActions.typeText(password));
        onView(withId(R.id.password)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.confirmPassword)).perform(ViewActions.click());
        onView(withId(R.id.confirmPassword)).perform(ViewActions.typeText(password));
        onView(withId(R.id.confirmPassword)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(ViewActions.click());
    }
}
