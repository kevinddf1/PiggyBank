package com.example.cse110;
import com.example.cse110.Controller.Expense;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExpenseTest {

    Expense formatOneDecimal;
    Expense formatOneDecOnePlace;
    Expense formatCorrect;

    @Before
    public void setup() {
        formatOneDecimal = new Expense(0, "test", 1., 5, 5, 5, null);
        formatOneDecOnePlace = new Expense(0, "test", 1.5, 5, 5, 5, null);
        formatCorrect = new Expense(5, "Cheeseburger", 35.63, 2, 6, 26, null);
    }

    @Test
    public void constructor_null_and_invalid_inputs() {
        try {
            Expense willFail = new Expense(Integer.MAX_VALUE, null, 0, 13, 100, 100, null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void constructor_double_with_only_period() {
        Assert.assertEquals("1.00", formatOneDecimal.getCostAsString());
    }

    @Test
    public void constructor_double_with_only_1_num() {
        Assert.assertEquals("1.50", formatOneDecOnePlace.getCostAsString());
    }

    @Test
    public void constructor_correct_input() {
        Assert.assertEquals("35.63", formatCorrect.getCostAsString());
    }
}
