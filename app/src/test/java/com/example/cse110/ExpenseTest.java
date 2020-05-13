package com.example.cse110;
import com.example.cse110.Model.Expense;

import org.junit.Assert;
import org.junit.Test;

public class ExpenseTest {
    @Test
    public void constructor_null_and_invalid_inputs() {
        try {
            Expense test = new Expense(Integer.MAX_VALUE, null, 0, 13, 100, 100, null);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void constructor_double_with_only_period() {
        Expense test = new Expense(0, "test", 1., 5, 5, 5, null);
        Assert.assertEquals("1.00", test.getCostAsString());
    }

    @Test
    public void constructor_double_with_only_1_num() {
        Expense test = new Expense(0, "test", 1.5, 5, 5, 5, null);
        Assert.assertEquals("1.50", test.getCostAsString());
    }
}
