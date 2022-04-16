package com.mt.data;

import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PagedResult<T> {

    private List<T> data;
    private long total;
    private int pageIndex;
    private int pageSize;
    private Map<String, Object> infos;

    public PagedResult() {
    }

    public PagedResult(List<T> data, long total, int pageIndex, int pageSize) {
        this.data = data;
        this.total = total;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

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

    public <D> PagedResult<D> map(Converter<T, D> converter) {
        List<D> list = data.stream().map(item -> converter.convert(item)).collect(Collectors.toList());
        return new PagedResult<D>(list, this.getTotal(), this.getPageIndex(), this.getPageSize());
    }

    public Map<String, Object> getInfos() {
        return infos;
    }

    public void setInfos(Map<String, Object> infos) {
        this.infos = infos;
    }
}
