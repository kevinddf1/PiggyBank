package com.example.cse110;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class CreatingExpensesTest {

    @Rule
    public ActivityTestRule<ExpensesListActivity> mExpensesListsActivityActivityTestRule =
            new ActivityTestRule<ExpensesListActivity>(ExpensesListActivity.class);

    @Test
    public void test() {
        String toBeTyped = "Boba";
        // Types the name of the expense into the text field
        onView(ViewMatchers.withId(R.id.expense_name)).perform(ViewActions.typeText(toBeTyped));
        // Types the cost of the expense into the text field
        onView(ViewMatchers.withId(R.id.expense_cost)).perform(ViewActions.typeText("8.57"));
        // Presses the + button to add the expense to the list
        onView(withId(R.id.AddToList)).perform(ViewActions.click());
        // Verify that the expense is displayed
        onView(withId(R.id.Categories)).check(matches(withText(toBeTyped)));
    }
}
