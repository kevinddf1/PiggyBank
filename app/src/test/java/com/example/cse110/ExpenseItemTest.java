package com.example.cse110;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExpenseItemTest {

    @Test
    public void constructorAllNullInputs() {
        try {
            ExpenseItem nullValues = new ExpenseItem(null, null, 0);
            fail("Should have thrown an exception");
        }
        catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void constructorCostNullInput() {
        try {
            ExpenseItem nullValues = new ExpenseItem(null, "test", 5);
            fail("Should have thrown an exception");
        }
        catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void constructorNameNullInput() {
        try {
            ExpenseItem nullValues = new ExpenseItem("25", null , 5);
            fail("Should have thrown an exception");
        }
        catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void constructorMonthInvalidInput() {
        try {
            ExpenseItem nullValues = new ExpenseItem("30", "test", 0);
            fail("Should have thrown an exception");
        }
        catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void constructor_invalid_cost_with_greater_than_2_decimals() {
        ExpenseItem largeDecCost = new ExpenseItem("8.0005", "food", 4);
        assertEquals("-$ 8.00 ", largeDecCost.getExpenseCost());
    }

    @Test
    public void constructor_cost_with_1_decimal() {
        ExpenseItem oneDec = new ExpenseItem("9.5", "food", 12);
        assertEquals("-$ 9.5 ", oneDec.getExpenseCost());
    }
}