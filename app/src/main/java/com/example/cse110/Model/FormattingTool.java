package com.example.cse110.Model;

/**
 * Helper class to allow for formatting money displays, avoiding redundant code.
 */
public class FormattingTool {


    private static final int DISTANCE_FROM_MILLIONS_COMMA = 9;
    private static final int DISTANCE_FROM_THOUSANDS_COMMA = 6;
    private static final int LENGTH_LESS_THAN_THOUSANDS = 6;
    private static final int LENGTH_LESS_THAN_MILLIONS = 9;
    private static final int BEGIN_INDEX = 0;
    private static final int DISTANCE_FROM_MILLIONS_COMMA_NO_DECIMAL = 6;
    private static final int DISTANCE_FROM_THOUSANDS_COMMA_NO_DECIMAL = 3;
    private static final int LENGTH_LESS_THAN_THOUSANDS_NO_DECIMAL = 3;
    private static final int LENGTH_LESS_THAN_MILLIONS_NO_DECIMALS = 6;
    private static final int CORRECT_DECIMAL = 2;
    private static final int TOO_SHORT_DECIMAL = 1;
    private static final int MISSING_DECIMAL = -1;

    public static void formattingTool(){

    }

    /**
     * Helper method to format the rendering in regards to decimal places.
     * @param valueToFormat The string to fix decimal placement
     * @return The formatted string
     */
    public String formatDecimal(String valueToFormat){
        String costString = valueToFormat;

        // Add formatting for whole numbers
        if(costString.indexOf('.') == MISSING_DECIMAL){
            costString = costString.concat(".00");
        }else{
            //Ensure only valid input
            int costLength = costString.length();
            int decimalPlace = costString.indexOf(".");

            // If the user inputs a number formatted as "<num>.", appends a 00 after the decimal
            if (costLength - decimalPlace == TOO_SHORT_DECIMAL) {
                costString = costString.substring(BEGIN_INDEX, decimalPlace + 1) +  "00";
            }
            // If the user inputs a number formatted as "<num>.1", where 1 could be any number,
            // appends a 0 to the end
            else if (costLength - decimalPlace == CORRECT_DECIMAL) {
                costString = costString.substring(BEGIN_INDEX, decimalPlace + CORRECT_DECIMAL) + "0";
            }
            // If the user inputs a number with >= 2 decimal places, only displays up to 2
            else {
                costString = costString.substring(BEGIN_INDEX, costString.indexOf(".") + CORRECT_DECIMAL + 1);
            }
        }

        return costString;

    }

    /**
     * Helper method to format a display of money value, including cents
     * @param valueToFormat The String to manipulate
     * @return The new string to display
     */
    public String formatMoneyString(String valueToFormat){
        int thousandsComma = valueToFormat.length() - DISTANCE_FROM_THOUSANDS_COMMA;
        int millionsComma = valueToFormat.length() - DISTANCE_FROM_MILLIONS_COMMA;
        if(valueToFormat.length() <= LENGTH_LESS_THAN_THOUSANDS){
            return valueToFormat;
        }else if(valueToFormat.length() <= LENGTH_LESS_THAN_MILLIONS){
            return valueToFormat.substring(BEGIN_INDEX, thousandsComma) + "," + valueToFormat.substring(thousandsComma);
        }

        return valueToFormat.substring(BEGIN_INDEX, millionsComma) + "," + valueToFormat.substring(millionsComma , thousandsComma) + "," + valueToFormat.substring(thousandsComma );
    }


    /**
     * Helper method to format a display of money value, only integers
     * @param valueToFormat The string to manipulate
     * @return The new string to display
     */
    public String formatIntMoneyString(String valueToFormat){
        int hundredthComma = valueToFormat.length() - DISTANCE_FROM_THOUSANDS_COMMA_NO_DECIMAL;
        int millionsComma = valueToFormat.length() - DISTANCE_FROM_MILLIONS_COMMA_NO_DECIMAL;

        if (valueToFormat.length() <= LENGTH_LESS_THAN_THOUSANDS_NO_DECIMAL){
            return  valueToFormat;
        }else if (valueToFormat.length() <= LENGTH_LESS_THAN_MILLIONS_NO_DECIMALS){
            return valueToFormat.substring(BEGIN_INDEX, hundredthComma) + "," + valueToFormat.substring(hundredthComma);
        }
        return valueToFormat.substring(BEGIN_INDEX, millionsComma) + "," + valueToFormat.substring(millionsComma , hundredthComma) + "," + valueToFormat.substring(hundredthComma );
    }


    public int getMonthInt(String month) {
        switch (month) {
            case "January":
                return 0;

            case "February":
                return 1;

            case "March":
                return 2;

            case "April":
                return 3;

            case "May":
                return 4;

            case "June":
                return 5;

            case "July":
                return 6;

            case "August":
                return 7;

            case "September":
                return 8;

            case "October":
                return 9;

            case "November":
                return 10;

            case "December":
                return 11;

            default:
                throw new IllegalStateException("Unexpected value: " + month);
        }
    }

}
