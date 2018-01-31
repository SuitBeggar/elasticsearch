package com.search.constructor.DSL;

public class DslCon {
    private Query query;

    private Filter filter;


    private int from;

    private int size;

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Query getQuery() {
        return query;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setQuery(Query query) {
        this.query = query;
    }


}
