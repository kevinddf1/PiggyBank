package com.example.cse110;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.cse110.View.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AccountLoginTest {

    @Rule
    public ActivityTestRule<LoginActivity> mLoginActivityTestRule =
            new ActivityTestRule<LoginActivity>(LoginActivity.class);

    @Test
    public void loginDummyTest() {
        String email = "autoTest555@ucsd.edu";
        String password = "password123";
        onView(withId(R.id.username)).perform(ViewActions.click());
        onView(withId(R.id.username)).perform(ViewActions.typeText(email));
        onView(withId(R.id.username)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.password)).perform(ViewActions.click());
        onView(withId(R.id.password)).perform(ViewActions.typeText(password));
        onView(withId(R.id.password)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.loginButton)).perform(ViewActions.click());

    }
}