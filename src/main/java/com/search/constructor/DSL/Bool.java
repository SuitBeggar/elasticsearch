package com.search.constructor.DSL;

import java.util.List;

public class Bool {
    private List<Must> must;

    private List<Must_not> must_not;

    private List<Should> should;


    private Integer minimum_should_match;

    public int getMinimum_should_match() {
        return minimum_should_match;
    }

    public void setMinimum_should_match(int minimum_should_match) {
        this.minimum_should_match = minimum_should_match;
    }

    public List<Must> getMust() {
        return must;
    }

    public void setMust(List<Must> must) {
        this.must = must;
    }

    public List<Must_not> getMust_not() {
        return must_not;
    }

    public void setMust_not(List<Must_not> must_not) {
        this.must_not = must_not;
    }

    public List<Should> getShould() {
        return should;
    }

    public void setShould(List<Should> should) {
        this.should = should;
    }

    public void setMinimum_should_match(Integer minimum_should_match) {
        this.minimum_should_match = minimum_should_match;
    }
}
