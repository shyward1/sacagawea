package com.shyward.hellowatson;

/**
 * Created by shyward on 5/4/15.
 */
public class SupportedCountries {


    public boolean isCountrySupported(String testCountry)
    {
        boolean retval = false;
        if (testCountry.equalsIgnoreCase("Russia"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("United Kingdom"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("United States"))
        {
            retval = true;
        }
        //else if (testCountry.equalsIgnoreCase("Venezuela"))
        //{
        //    retval = true;
        //}
        else if (testCountry.equalsIgnoreCase("Canada"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("France"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("Germany"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("South Africa"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("Brazil"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("Mexico"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("Japan"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("India"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("Pakistan"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("Finland"))
        {
            retval = true;
        }
        else if (testCountry.equalsIgnoreCase("Australia"))
        {
            retval = true;
        }
        return retval;
    }
}
