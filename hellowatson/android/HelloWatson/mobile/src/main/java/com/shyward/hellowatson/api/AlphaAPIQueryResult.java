package com.shyward.hellowatson.api;

import com.wolfram.alpha.WAQueryResult;

/**
 * Value object holding the Alpha API result object
 *
 * Created by Mark Reimer on 5/4/15.
 * Copyright © ADT LLC, 2015
 */
public class AlphaAPIQueryResult {
    private WAQueryResult waQueryResult;
    private String result;

    public AlphaAPIQueryResult() {

    }


    public void setWaQueryResult(WAQueryResult obj) {
        this.waQueryResult = obj;
    }

    public void setResult(String theResult) {
        this.result = theResult;
    }

    public String getResult() {
        return this.result;
    }

    public WAQueryResult getWaQueryResult() {
        return this.waQueryResult;
    }


}
