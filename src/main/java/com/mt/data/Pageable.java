package com.mt.data;

import java.util.List;

public class Pageable {
    private int pageIndex;
    private int pageSize;
    private Sortable sort;
    private List<Sortable> sortables;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Sortable getSort() {
        return sort;
    }

    public void setSort(Sortable sort) {
        this.sort = sort;
    }

    public List<Sortable> getSortables() {
        return sortables;
    }

    public void setSortables(List<Sortable> sortables) {
        this.sortables = sortables;
    }

}
